package com.handy

import com.handy.ProductPriceCustomer.Medicom.Aacuerdo
import com.handy.ProductPriceCustomer.Medicom.Alacuerdo
import com.handy.ProductPriceCustomer.ProductPriceCustomer
import groovyx.net.http.ContentType
import groovyx.net.http.EncoderRegistry
import groovyx.net.http.HTTPBuilder

class AgreementService {

    def grailsApplication

    def sendAacuerdo() {
        List<Alacuerdo> alacuerdos = new ArrayList<Alacuerdo>()
        List<Aacuerdo> aacuerdos
        def productPriceCustomerList = []

        try {
            aacuerdos = Aacuerdo.withCriteria {
                eq 'status', 0
                ne 'customerCode', '+'
                ne 'customerCode', '}'
                maxResults grailsApplication.config.configuration.params.maxA
                order('docEntry','asc')
                order('logInst','asc')
            }

            if (aacuerdos.isEmpty())
                return

            aacuerdos.each { aacuerdo ->

                ProductPriceCustomer productPriceCustomer
                Alacuerdo alacuerdo = Alacuerdo.findByDocEntryAndLogInst(aacuerdo.docEntry, aacuerdo.logInst)

                if (alacuerdo) {
                    alacuerdos.add(alacuerdo)
                    productPriceCustomer = new ProductPriceCustomer()
                    productPriceCustomer.customerCode = aacuerdo.customerCode
                    productPriceCustomer.enabled = 'N'.equalsIgnoreCase(aacuerdo.enabled)
                    productPriceCustomer.productCode = alacuerdo.productCode
                    productPriceCustomer.price = alacuerdo.price ?: 0.00
                }

                if (productPriceCustomer)
                    productPriceCustomerList.add(productPriceCustomer)
            }

            post(productPriceCustomerList, aacuerdos, alacuerdos)

        } catch (Exception e) {
            log.error('Error al enviar aacuerdos', e)
        }
    }

    def sendAlacuerdo() {
        List<Alacuerdo> alacuerdos = new ArrayList<Alacuerdo>()
        List<Aacuerdo> aacuerdos = new ArrayList<Aacuerdo>()
        def productPriceCustomerList = []

        try {
            alacuerdos = Alacuerdo.withCriteria {
                eq 'status', 0
                order('docEntry','asc')
                order('logInst','asc')
                maxResults grailsApplication.config.configuration.params.maxAL
            }

            if (alacuerdos.isEmpty())
                return

            alacuerdos.each { alacuerdo ->

                ProductPriceCustomer productPriceCustomer
                Aacuerdo aacuerdo = Aacuerdo.findByDocEntryAndLogInst(alacuerdo.docEntry, alacuerdo.logInst)

                if (aacuerdo) {
                    aacuerdos.add(aacuerdo)
                    productPriceCustomer = new ProductPriceCustomer()
                    productPriceCustomer.customerCode = aacuerdo.customerCode
                    productPriceCustomer.enabled = 'N'.equalsIgnoreCase(aacuerdo.enabled)
                    productPriceCustomer.productCode = alacuerdo.productCode
                    productPriceCustomer.price = alacuerdo.price ?: 0.00

                }

                if (productPriceCustomer && !productPriceCustomerList.contains(productPriceCustomer))
                    productPriceCustomerList.add(productPriceCustomer)
            }

            post(productPriceCustomerList, aacuerdos, alacuerdos)

        } catch (Exception e) {
            log.error('Error al enviar alacuerdos', e)
        }
    }

    def updateAacuerdo(def difference) {

        Aacuerdo aacuerdo

        try {
            aacuerdo = Aacuerdo.findByDocEntryAndLogInst(difference.reference.docEntry, difference.reference.logInst)

            if (aacuerdo) {
                aacuerdo.customerCode = difference.reference.customerCode
                aacuerdo.enabled = difference.reference.enabled

                try {
                    aacuerdo = aacuerdo.merge()
                } catch (Exception e) {

                }

            } else {
                aacuerdo = new Aacuerdo()
                aacuerdo.docEntry = difference.reference.docEntry
                aacuerdo.logInst = difference.reference.logInst
                aacuerdo.customerCode = difference.reference.customerCode
                aacuerdo.enabled = difference.reference.enabled
            }

            aacuerdo.status = 0

            if (aacuerdo.docEntry && aacuerdo.logInst)
                aacuerdo.save(failOnError: true)

        } catch (Exception ex) {
            log.error "Error al almacenar aacuerdo en sincronizador", ex
        }
    }

    def updateAlacuerdo(def difference) {

        Alacuerdo alacuerdo

        try {

            alacuerdo = Alacuerdo.findByDocEntryAndLogInstAndProductCode(difference.reference.docEntry, difference.reference.logInst,difference.reference.productCode)

            if (alacuerdo) {

                alacuerdo.productCode = difference.reference.productCode
                alacuerdo.price = difference.reference.price

                try {
                    alacuerdo = alacuerdo.merge()
                } catch (Exception e) {

                }

            } else {
                alacuerdo = new Alacuerdo()
                alacuerdo.docEntry = difference.reference.docEntry
                alacuerdo.logInst = difference.reference.logInst
                alacuerdo.productCode = difference.reference.productCode
                alacuerdo.price = difference.reference.price
            }

            alacuerdo.status = 0

            if (alacuerdo.docEntry && alacuerdo.logInst)
                if(!alacuerdo.save()){
                    alacuerdo.errors.each {
                        println it
                    }
                }

        } catch (Exception ex) {
            log.error "Error al almacenar alacuerdo en sincronizador", ex
        }
    }

    def post(def list, List<Aacuerdo> aacuerdos, List<Alacuerdo> alacuerdos) {
        def http = new HTTPBuilder(grailsApplication.config.handy.server)
        def body = [agreements: list.collect { generateJsonFormat(it) }, push_to_mobile: false]
        http.auth.basic(grailsApplication.config.handy.username, grailsApplication.config.handy.password)
        http.encoderRegistry = new EncoderRegistry(charset: 'UTF-8')

        try {
            http.post(path: '/api/product/createOrUpdatePriceAgreement', body: body, requestContentType: ContentType.JSON) { resp, json ->
                analyzeResponseData(resp, json, list, aacuerdos, alacuerdos)
            }
        } catch (Exception ex) {
            log.error "Caso 3: Error en solicitud web. Error:", ex
        }
    }

    def generateJsonFormat(ProductPriceCustomer productPriceCustomer) {
        return [
                customer_code: productPriceCustomer.customerCode,
                product_code : productPriceCustomer.productCode,
                price        : productPriceCustomer.price,
                enabled      : productPriceCustomer.enabled
        ]
    }

    def analyzeResponseData(def resp, def json, def list, List<Aacuerdo> aacuerdos, List<Alacuerdo> alacuerdos) {

        if (resp.statusLine.statusCode == 200) {

            if (json.error) {

                if (list.size() > 0) {

                    log.info "Caso 1: Solicitud inválida aacuerdos y alacuerdos"
                    list.each {
                        println it.dump()
                    }

                } else {

                    log.info "Caso 2: Parcialmente, puede haber errores aacuerdos y alacuerdos"

                }

            } else {
                updateStatus(aacuerdos, alacuerdos)
            }

        } else {
            log.error "Caso 3: Error en solicitud web. Código de error: ${resp.statusLine.statusCode}"
        }

        int count0 = Aacuerdo.countByStatus(1)
        int count1 = Alacuerdo.countByStatus(1)
        int count2 = Aacuerdo.countByStatus(2)
        int count3 = Alacuerdo.countByStatus(2)
        int count4 = Aacuerdo.count
        int count5 = Alacuerdo.count

        if(count0 == (count4 - count2) && count1 == (count5 - count3))
            log.info("****************** Aacuerdo y Alacuerdo terminados *******************")

    }

    def updateStatus(List<Aacuerdo> aacuerdos, List<Alacuerdo> alacuerdos) {
        aacuerdos.each { aacuerdo ->
            try {
                aacuerdo.status = 1
                aacuerdo.save()
            } catch (Exception e) {
                log.error('Error al actualizar aacuero status', e)
            }

        }
        alacuerdos.each { alacuerdo ->
            try {
                alacuerdo.status = 1
                alacuerdo.save()
            } catch (Exception e) {
                log.error('Error al actualizar alacuero status', e)
            }
        }
    }

}
