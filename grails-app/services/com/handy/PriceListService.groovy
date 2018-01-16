package com.handy

import com.handy.PriceList.PriceList
import com.handy.PriceList.PriceListItem
import com.handy.Product.Product
import groovyx.net.http.ContentType
import groovyx.net.http.EncoderRegistry
import groovyx.net.http.HTTPBuilder

class PriceListService {

    def grailsApplication

    def sync() {
        send()
    }

    def send() {
        try {
            if (Product.countByStatus(0) > 0)
                return

            def list = getPendingPriceList()

            if (list.empty) {
                log.debug "No hay listas de precios para enviar"
                return
            }

            def http = new HTTPBuilder(grailsApplication.config.handy.server)
            def body = [priceList: list]
            http.auth.basic(grailsApplication.config.handy.username, grailsApplication.config.handy.password)
            http.encoderRegistry = new EncoderRegistry(charset: 'UTF-8')

            try {
                http.post(path: '/api/priceList/createOrUpdate', body: body, requestContentType: ContentType.JSON) { resp, json ->
                    analyzeResponseData(resp, json, list)
                }
            } catch (Exception ex) {
                log.error "Caso 3: Error en solicitud web. Error:", ex
            }

        } catch (Exception ex) {
            log.error "Error no controlado en proceso", ex
        }
    }

    def getPendingPriceList() {

        def results = []

        def priceLists = PriceList.withCriteria {
            eq 'status', 0
            maxResults grailsApplication.config.configuration.params.maxPL
        }

        priceLists.each { priceList ->
            def items
            if (grailsApplication.config.erp.sap.enabled || grailsApplication.config.erp.p1.enabled) {
                items = PriceListItem.withCriteria {
                    eq 'listId', priceList.id
                    eq 'status', 0
                    maxResults grailsApplication.config.configuration.params.maxPLI

                }
            } else if (grailsApplication.config.erp.intelisis.enabled) {
                items = PriceListItem.withCriteria {
                    eq 'listCode', priceList.id
                    eq 'status', 0
                    maxResults grailsApplication.config.configuration.params.maxPLI

                }
            }


            if (items) {
                def list = []

                items.each {
                    list.add([product: it.productCode, price: it.price ?: 0.00])
                }

                results.add(code: priceList.id, name: priceList.name, items: list)
            } else {
                results.add(code: priceList.id, name: priceList.name)
            }


        }


        return results
    }

    def analyzeResponseData(def resp, def json, def priceLists) {

        if (resp.statusLine.statusCode == 200) {

            if (json.error) {

                if (json.processedPriceList == 0 && priceLists.size() > 0) {

                    log.info "Caso 1: Solicitud inválida lista de precios"
                    log.error("Error al enviar listas de precios a Handy: $json.message")

                } else {

                    log.info "Caso 2: Parcialmente, puede haber errores en las listas de precios"
                    log.error json.errors
                }

            } else {
                log.info "✈ $priceLists.size Listas de precios enviadas han sido guardadas correctamente en Handy. ✈"
                priceLists.each { updatePriceListStatus(it, false) }
            }

        } else {
            log.error "Caso 3: Error en solicitud web. Código de error: ${resp.statusLine.statusCode}"
        }

        int count0 = PriceList.countByStatus(1)
        int count1 = PriceList.count()
        int count2 = PriceList.countByStatus(2)

        log.info("PriceList Enviados a Handy: $count0 Total: $count1 Errores: $count2")
        if (count0 == (count1 - count2))
            log.info("****************** PriceList terminado *******************")
    }

    def update(def difference) {
        try {
            def priceList = PriceList.get(difference.reference.id) ?: new PriceList()

            priceList.properties = difference.reference.properties
            priceList.id = difference.reference.id
            priceList.status = 0

            try {
                priceList = priceList.merge()
            } catch (Exception e) {

            }

            priceList.save(failOnError: true)

        } catch (Exception ex) {
            log.error "Error al almacenar priceList en sincronizador", ex
        }
    }

    def updateItem(def difference) {

        PriceListItem priceListItem

        try {
            if (grailsApplication.config.erp.sap.enabled || grailsApplication.config.erp.p1.enabled)
                priceListItem = PriceListItem.findByProductCodeAndListId(difference.reference.productCode, difference.reference.listId)
            else if (grailsApplication.config.erp.intelisis.enabled)
                priceListItem = PriceListItem.findByProductCodeAndListCode(difference.reference.productCode, difference.reference.listCode)

            if (priceListItem) {
                priceListItem.price = difference.reference.price
                priceListItem.status = 0

                if (!priceListItem.productCode)
                    return

            } else {
                priceListItem = new PriceListItem()
                priceListItem.productCode = difference.reference.productCode
                priceListItem.price = difference.reference.price
                priceListItem.status = 0

                if (grailsApplication.config.erp.sap.enabled || grailsApplication.config.erp.p1.enabled)
                    priceListItem.listId = difference.reference.listId
                else if (grailsApplication.config.erp.intelisis.enabled)
                    priceListItem.listCode = difference.reference.listCode

            }

            try {
                priceListItem = priceListItem.merge()
            } catch (Exception e) {

            }

            priceListItem.save(failOnError: true)

            PriceList priceList

            if (grailsApplication.config.erp.sap.enabled || grailsApplication.config.erp.p1.enabled)
                priceList = PriceList.get(priceListItem.listId)
            else if (grailsApplication.config.erp.intelisis.enabled)
                priceList = PriceList.get(priceListItem.listCode)

            if (priceList) {
                priceList.status = 0
                priceList.save()

                if (priceList.id == grailsApplication.config.erp.sap.tables.priceList.primaryList || priceList.id == grailsApplication.config.erp.intelisis.tables.priceList.primaryList) {
                    def Product product = Product.get(priceListItem.productCode)
                    if (product) {
                        product.status = 0
                        product.save()
                    }
                }

            }

        } catch (Exception ex) {
            log.error "Error al almacenar PriceListItem en sincronizador", ex
        }
    }

    def updatePriceListStatus(def processes, boolean error) {
        try {
            PriceList list = PriceList.findById(processes.code)
            if (error) {
                list.status = 2 //Enviada con errores
                log.error("Error al envíar priceList $processes.code Se pondrá en status 2.")
                processes.items.each {
                    PriceListItem item
                    if (grailsApplication.config.erp.sap.enabled || grailsApplication.config.erp.p1.enabled)
                        item = PriceListItem.findByProductCodeAndListId(it.productCode, processes.id)
                    else if (grailsApplication.config.erp.intelisis.enabled)
                        item = PriceListItem.findByProductCodeAndListCode(it.productCode, processes.id)

                    item.status = 2 //Enviada con errores
                    log.error("Error al envíar priceList $processes.code Se pondrá en status 2.")
                    item.save()
                }
            } else {
                def result
                if (grailsApplication.config.erp.sap.enabled || grailsApplication.config.erp.p1.enabled)
                    result = PriceListItem.countByListIdAndStatus(list.id, 0)
                else if (grailsApplication.config.erp.intelisis.enabled)
                    result = PriceListItem.countByListCodeAndStatus(list.id, 0)

                if (result == 0)
                    list.status = 1 //Enviada correctamente
                processes.items.each {
                    PriceListItem item

                    if (grailsApplication.config.erp.sap.enabled || grailsApplication.config.erp.p1.enabled)
                        item = PriceListItem.findByProductCodeAndListId(it.product, list.id)
                    else if (grailsApplication.config.erp.intelisis.enabled)
                        item = PriceListItem.findByProductCodeAndListCode(it.product, list.id)

                    item.status = 1
                    item.save()
                }
            }

            list.save(failOnError: true)
        } catch (Exception e) {
            log.error "Error al actualizar status de lista de precios", e
        }

    }
}
