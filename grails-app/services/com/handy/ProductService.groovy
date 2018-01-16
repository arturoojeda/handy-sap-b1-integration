package com.handy

import com.handy.PriceList.PriceList
import com.handy.PriceList.PriceListItem
import com.handy.Product.Product
import groovyx.net.http.ContentType
import groovyx.net.http.EncoderRegistry
import groovyx.net.http.HTTPBuilder

class ProductService {

    def grailsApplication

    def sync() {
        send()
    }

    def send() {

        try {
            def list = Product.withCriteria {
                eq 'status', 0
                maxResults grailsApplication.config.configuration.params.maxP
            }

            if (list.empty) {
                log.debug "No hay productos para enviar"
                return
            }

            def http = new HTTPBuilder(grailsApplication.config.handy.server)
            def body = [products: list.collect { generateJsonFormat(it) }, push_to_mobile: false]
            http.auth.basic(grailsApplication.config.handy.username, grailsApplication.config.handy.password)
            http.encoderRegistry = new EncoderRegistry(charset: 'UTF-8')

            try {
                http.post(path: '/api/product/createOrUpdate', body: body, requestContentType: ContentType.JSON) { resp, json ->
                    analyzeResponseData(resp, json, list)
                }
            } catch (Exception ex) {
                log.error "Caso 3: Error en solicitud web. Error:", ex
            }

        } catch (Exception ex) {
            log.error "Error no controlado en proceso", ex
        }
    }

    def generateJsonFormat(Product product) {

        if (grailsApplication.config.erp.intelisis.enabled) {
            return [
                    code          : product.code,
                    description   : product.description ? product.description : product.id,
                    product_family: product.family ? product.family : 'Sin familia',
                    price         : product.finalPrice,
                    enabled       : 'ALTA'.equalsIgnoreCase(product.enabled)
            ]
        }

        //SAP
        return [
                code          : product.code,
                description   : product.description ? product.description : product.id,
                product_family: product.family ? product.family : 'Sin familia',
                price         : product.finalPrice,
                barcode       : product.barcode,
                enabled       : true
        ]
    }

    def analyzeResponseData(def resp, def json, def productList) {

        if (resp.statusLine.statusCode == 200) {

            if (json.error) {

                if (json.processesCustomers == 0 && productList.size() > 0) {

                    log.info "Caso 1: Solicitud inválida productos"
                    log.error("Error al enviar Productos a Handy: $json.message")

                } else {

                    log.info "Caso 2: Parcialmente, puede haber errores productos"

                    productList.each { customer ->
                        if (json.savedCustomers.find { it.code == customer.code })
                            updateCustomerStatus(customer, false)
                        else
                            updateCustomerStatus(customer, true)
                    }

                }

            } else {
                productList.each { updateCustomerStatus(it, false) }
            }

        } else {
            log.error "Caso 3: Error en solicitud web. Código de error: ${resp.statusLine.statusCode}"
        }

        int count0 = Product.countByStatus(1)
        int count1 = Product.count()
        int count2 = Product.countByStatus(2)

        log.info("✈ Product Enviados a Handy: $count0 Total: $count1 Errores: $count2 ✈")
        if (count0 == (count1 - count2)) {
            log.info("****************** Product terminado *******************")
            Sync sync = Sync.get(1)
            sync.productFinished = true
            sync.save()
        }

    }

    def update(def difference) {
        try {
            def product = Product.get(difference.reference.id) ?: new Product()

            product.properties = difference.reference.properties
            product.id = difference.reference.id
            product.status = 0

            try {
                product = product.merge()
            } catch (Exception e) {

            }

            product.save(failOnError: true)

            if (grailsApplication.config.erp.p1.enabled) {
                saveOrUpdatePriceListItem(product)
            }

        } catch (Exception ex) {
            log.error "Error al almacenar producto en sincronizador", ex
        }
    }

    def updateCustomerStatus(Product product, boolean error) {
        try {
            if (error) {
                product.status = 2 //Enviada con errores
                log.error("Error al envíar product $product.code Se pondrá en status 2.")
            } else {
                product.status = 1 //Enviada correctamente
            }

            product.save(failOnError: true)
        } catch (Exception e) {
            log.error "Error al actualizar status de product", e
        }
    }

    def saveOrUpdatePriceListItem(Product product) {
        if (product.price) {
            updatePriceListItem(1, product.id, product.price)
        }
        if (product.price2) {
            updatePriceListItem(2, product.id, product.price2)
        }
        if (product.price3) {
            updatePriceListItem(3, product.id, product.price3)
        }
        if (product.price4) {
            updatePriceListItem(4, product.id, product.price4)
        }
        if (product.price5) {
            updatePriceListItem(5, product.id, product.price5)
        }
    }

    def updatePriceListItem(long listId, String productCode, def price) {
        if (price) {

            def list = PriceList.findById(listId)
            def item = PriceListItem.findByListIdAndProductCode(listId, productCode)

            if (!list) {
                list = new PriceList()
                list.id = listId
                list.name = listId.toString()
            }

            if (item) {
                item.price = price
                item.status = 0
            } else {
                item = new PriceListItem()
                item.listId = listId
                item.price = price
                item.productCode = productCode
            }

            item.save()

            list.status = 0
            list.save()
        }
    }

}
