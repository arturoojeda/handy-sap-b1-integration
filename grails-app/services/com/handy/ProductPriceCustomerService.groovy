package com.handy

import com.handy.PriceList.PriceListItem
import com.handy.ProductPriceCustomer.ProductPriceCustomer
import groovyx.net.http.ContentType
import groovyx.net.http.EncoderRegistry
import groovyx.net.http.HTTPBuilder

class ProductPriceCustomerService {

    def grailsApplication

    def sync() {
        send()
    }

    def send() {
        List<ProductPriceCustomer> list
        try {
            list = ProductPriceCustomer.withCriteria {
                eq 'status', 0
                ne 'customerCode', '*1'
                maxResults grailsApplication.config.configuration.params.maxPC
            }

            if (list.empty) {
                log.debug "No hay precios de productos por cliente para enviar"
                return
            }

            post(list)

        } catch (Exception ex) {
            log.error "Error no controlado en proceso", ex
        }
    }

    def post(def list) {
        def http = new HTTPBuilder(grailsApplication.config.handy.server)
        def body = [agreements: list.collect { generateJsonFormat(it) }, push_to_mobile: false]
        http.auth.basic(grailsApplication.config.handy.username, grailsApplication.config.handy.password)
        http.encoderRegistry = new EncoderRegistry(charset: 'UTF-8')

        try {
            http.post(path: '/api/product/createOrUpdatePriceAgreement', body: body, requestContentType: ContentType.JSON) { resp, json ->
                analyzeResponseData(resp, json, list)
            }
        } catch (Exception ex) {
            log.error "Caso 3: Error en solicitud web. Error:", ex
        }
    }

    def generateJsonFormat(ProductPriceCustomer productPriceCustomer) {
        return [
                customer_code: productPriceCustomer.customerCode,
                product_code : productPriceCustomer.productCode,
                price        : grailsApplication.config.configuration.productPriceCustomer.discount ? getSpecialPrice(productPriceCustomer) : productPriceCustomer.price,
                enabled      : productPriceCustomer.enabled
        ]
    }

    def analyzeResponseData(def resp, def json, def list) {

        if (resp.statusLine.statusCode == 200) {

            if (json.error) {

                if (json.processesCustomers == 0 && list.size() > 0) {

                    log.info "Caso 1: Solicitud inválida productPriceCustomer"
                    log.error("Error al enviar productPriceCustomer a Handy: $json.message")

                } else {

                    log.info "Caso 2: Parcialmente, puede haber errores productPriceCustomer"

                    list.each { customer ->
                        if (json.savedCustomers.find { it.code == customer.code })
                            updateCustomerStatus(customer, false)
                        else
                            updateCustomerStatus(customer, true)
                    }

                }

            } else {
                log.info "✈ $list.size productPriceCustomer envíados han sido guardados correctamente en Handy. ✈"
                list.each { updateCustomerStatus(it, false) }
            }

        } else {
            log.error "Caso 3: Error en solicitud web. Código de error: ${resp.statusLine.statusCode}"
        }

        int count0 = ProductPriceCustomer.countByStatus(1)
        int count1 = ProductPriceCustomer.countByCustomerCodeNotEqual('*1')
        int count2 = ProductPriceCustomer.countByCustomerCode('*1')
        int count3 = ProductPriceCustomer.countByStatus(2)

        log.info("ProductPriceCustomer Enviados a Handy: $count0 CustomerCode != *1: $count1 CustomerCode == *1: $count2 Errores: $count3")
        if (count0 == (count1 - count3))
            log.info("****************** ProductPriceCustomer terminado *******************")
    }

    def update(def difference) {

        try {

            def productPriceCustomer = ProductPriceCustomer.get(difference.reference.id) ?: new ProductPriceCustomer()

            productPriceCustomer.properties = difference.reference.properties
            productPriceCustomer.id = difference.reference.id
            productPriceCustomer.status = 0
            try {
                productPriceCustomer = productPriceCustomer.merge()
            } catch (Exception e) {

            }

            if (productPriceCustomer.customerCode && productPriceCustomer.productCode)
                productPriceCustomer.save(failOnError: true)

        } catch (Exception ex) {
            log.error "Error al almacenar cliente en sincronizador", ex
        }

    }

    def updateCustomerStatus(ProductPriceCustomer ProductPriceCustomer, boolean error) {
        try {
            if (error) {
                ProductPriceCustomer.status = 2 //Enviada con errores
                log.error("Error al envíar productPriceCustomer $ProductPriceCustomer.customerCode $ProductPriceCustomer.productCode Se pondrá en status 2.")
            } else
                ProductPriceCustomer.status = 1 //Enviada correctamente

            ProductPriceCustomer.save(failOnError: true)
        } catch (Exception e) {
            log.error "Error al actualizar status de productPriceCustomer", e
        }
    }

    def getSpecialPrice(ProductPriceCustomer productPriceCustomer) {

        try {
            def erpPriceListItem = PriceListItem.findByListIdAndProductCode(productPriceCustomer.listId, productPriceCustomer.productCode)

            if (erpPriceListItem) {
                return calculateSpecialPrice(erpPriceListItem.price, productPriceCustomer.discount)
            }

        } catch (Exception e) {
            log.error('Error al calcular precio especial', e)
        }

        return productPriceCustomer.price
    }

    def calculateSpecialPrice(BigDecimal price, BigDecimal discount) {
        return ((BigDecimal) price - (price * (discount / 100))).setScale(2, BigDecimal.ROUND_HALF_UP)
    }

}
