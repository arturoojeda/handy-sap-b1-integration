package com.handy

import com.handy.Customer.Customer
import com.handy.Customer.IntelisisCustomer
import com.handy.PriceList.PriceList
import com.handy.Zone.Zone
import grails.converters.JSON
import groovyx.net.http.ContentType
import groovyx.net.http.EncoderRegistry
import groovyx.net.http.HTTPBuilder
import org.apache.http.client.config.RequestConfig
import org.apache.http.config.SocketConfig
import org.apache.http.impl.client.HttpClients
import org.hibernate.FlushMode

class CustomerService {

    def grailsApplication
    def intelisisService

    def sync() {
        send()
    }

    def send() {
        if (PriceList.countByStatus(0) > 0)
            return

        List<Customer> customerList
        try {
            customerList = Customer.withCriteria {
                eq 'status', 0
                maxResults grailsApplication.config.configuration.params.maxC
            }

            if (customerList.empty) {
                log.debug "No hay clientes para enviar"
                return
            }

            def http = new HTTPBuilder(grailsApplication.config.handy.server)
            def body = [customers: customerList.collect { generateJsonFormat(it) }, push_to_mobile: false]
            http.auth.basic(grailsApplication.config.handy.username, grailsApplication.config.handy.password)
            http.encoderRegistry = new EncoderRegistry(charset: 'UTF-8')

            try {
                http.post(path: '/api/customer/createOrUpdate', body: body, requestContentType: ContentType.JSON) { resp, json ->
                    analyzeResponseData(resp, json, customerList)
                }
            } catch (Exception ex) {
               // log.error "Caso 3: Error en solicitud web. Error:", ex
            }

        } catch (Exception ex) {
            log.error "Error no controlado en proceso", ex
        }

    }

    def generateJsonFormat(Customer customer) {

        Zone zone = Zone.get(customer.zoneId)

        if (grailsApplication.config.erp.sap.enabled) {
            def enabled
            if (grailsApplication.config.configuration.customer.alwaysActive) {
                enabled = true
            } else {
                enabled = '02'.equalsIgnoreCase(customer.enabled)
            }
            return [
                    code            : customer.code,
                    description     : customer.description ? customer.description : customer.code,
                    zone_description: zone ? zone.name : 'Sin zona',

                    latitude        : customer.latitude,
                    longitude       : customer.longitude,
                    address         : customer.fullAddress,
                    city            : customer.city,
                    postalCode      : customer.postalCode,
                    owner           : customer.owner,
                    phoneNumber     : customer.phoneNumber,
                    comments        : customer.comments,
                    email           : customer.email,
                    is_prospect     : customer.prospect,
                    is_mobile       : customer.mobile,
                    enabled         : enabled,
                    discount        : customer.discount,
                    price_list      : PriceList.get(customer.priceList)?.id,
                    balance         : customer.balance ? customer.balance : 0.00,
                    credit          : customer.credit ? customer.credit : 0.00
            ]
        } else if (grailsApplication.config.erp.p1.enabled) {
            return [
                    code            : customer.code,
                    description     : customer.description ? customer.description : customer.code,
                    zone_description: zone ? zone.name : 'Sin zona',

                    latitude        : customer.latitude,
                    longitude       : customer.longitude,
                    address         : customer.fullAddress,
                    city            : customer.city,
                    postal_code     : customer.postalCode,
                    owner           : customer.owner,
                    phone_number    : customer.phoneNumber,
                    comments        : customer.comments,
                    email           : customer.email,
                    is_prospect     : customer.prospect,
                    is_mobile       : customer.mobile,
                    enabled         : '-1'.equalsIgnoreCase(customer.enabled),
                    discount        : customer.discount,
                    price_list      : PriceList.get(customer.priceList)?.id,
                    balance         : customer.balance ? customer.balance : 0.00,
                    credit          : customer.credit ? customer.credit : 0.00
            ]
        } else if (grailsApplication.config.erp.intelisis.enabled) {
            return [
                    code            : customer.code.startsWith('H') ? customer.code.substring(1) : customer.code,
                    description     : customer.description ? customer.description : customer.code,
                    zone_description: customer.zone ?: 'Sin zona',

                    latitude        : customer.latitude,
                    longitude       : customer.longitude,
                    address         : customer.fullAddress,
                    city            : customer.city,
                    postalCode      : customer.postalCode,
                    owner           : customer.owner,
                    phoneNumber     : customer.phoneNumber,
                    comments        : customer.comments,
                    email           : customer.email,
                    enabled         : 'ALTA'.equalsIgnoreCase(customer.enabled),
                    price_list      : PriceList.get(customer.priceList)?.id
            ]
        }
    }

    def analyzeResponseData(def resp, def json, def customerList) {

        if (resp.statusLine.statusCode == 200) {

            if (json.error) {

                if (json.processesCustomers == 0 && customerList.size() > 0) {

                    log.info "Caso 1: Solicitud inválida customer"
                    log.error("Error al enviar clientes a Handy: $json.message")

                } else {

                    log.info "Caso 2: Parcialmente, puede haber errores customer"

                    customerList.each { customer ->
                        if (json.savedCustomers.find { it.code == customer.code })
                            updateStatus(customer, false)
                        else
                            updateStatus(customer, true)
                    }

                }

            } else {
                customerList.each { updateStatus(it, false) }
            }

        } else {
            log.error "Caso 3: Error en solicitud web. Código de error: ${resp.statusLine.statusCode}"
        }

        int count0 = Customer.countByStatus(1)
        int count1 = Customer.count()
        int count2 = Customer.countByStatus(2)

        log.info("✈ Customer Enviados a Handy: $count0 Total: $count1 Errores: $count2 ✈")
        if (count0 == (count1 - count2)) {
            log.info("****************** Customer terminado *******************")
            Sync sync = Sync.get(1)
            sync.customerFinished = true
            sync.save()
        }

    }

    def update(def difference) {
        try {
            def customer = Customer.get(difference.reference.id) ?: new Customer()

            customer.properties = difference.reference.properties
            customer.id = difference.reference.id
            customer.status = 0

            try {
                customer = customer.merge()
            } catch (Exception e) {

            }

            customer.save(failOnError: true)

        } catch (Exception ex) {
            log.error "Error al almacenar cliente en sincronizador", ex
        }
    }

    def updateZone(def difference) {
        try {
            def zone = Zone.get(difference.reference.id)

            if (zone){
                zone.name = difference.reference.name
            } else {
                zone = new Zone(name: difference.reference.name)
                zone.id = difference.reference.id
            }

            zone.save(failOnError: true)

            Customer.executeUpdate("update Customer set status = 0 where zoneId = :zoneId", [zoneId: zone.id])
        } catch (Exception ex) {
            log.error "Error al almacenar zona en sincronizador", ex
        }
    }

    def updateStatus(Customer customer, boolean error) {
        try {
            if (error) {
                customer.status = 2 //Enviada con errores
                log.error("Error al envíar $customer.code Se pondrá en status 2.")
            } else
                customer.status = 1 //Enviada correctamente

            customer.save(failOnError: true)
        } catch (Exception e) {
            log.error "Error al actualizar status de cliente", e
        }
    }

    def getCustomerHandyAPI(long id) {
        IntelisisCustomer intelisisCustomer

        def http = new HTTPBuilder(grailsApplication.config.handy.server)
        http.auth.basic(grailsApplication.config.handy.username, grailsApplication.config.handy.password)
        http.encoderRegistry = new EncoderRegistry(charset: 'UTF-8')

        try {
            http.get(path: "/api/customer/list", query: [id: id]) { resp, json ->

                if (json.error) {
                    log.info("Caso 2: Error al solicitar cliente API Handy. ID: $id")
                    return false
                } else {

                    def jsonCustomer = json.customers[0]

                    intelisisCustomer = new IntelisisCustomer(jsonCustomer)
                    intelisisCustomer.id = "H$jsonCustomer.code"
                    intelisisCustomer.enabled = 'ALTA'

                    if (jsonCustomer.created_by && false) {
                        def user = intelisisService.getUser(jsonCustomer.created_by)
                        if (user)
                            intelisisCustomer.zone = user.agente
                    }
                    intelisisCustomer.zone = 'HANDY'

                    if (!intelisisCustomer.save()) {
                        log.error("Error al guardar cliente en Intelisis ${intelisisCustomer.dump()}")
                        return false
                    }
                }

            }
        } catch (Exception ex) {
            log.error "Caso 3: Error en solicitud web pedidos recibidos. Error:", ex
            return false
        }

        return true
    }

}
