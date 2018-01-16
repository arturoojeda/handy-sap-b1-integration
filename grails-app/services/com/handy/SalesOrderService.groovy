package com.handy

import com.handy.Invoice.SapInvoice
import com.handy.SalesOrder.SalesOrder
import com.handy.SalesOrder.SalesOrderItem
import groovy.time.TimeCategory
import groovyx.net.http.ContentType
import groovyx.net.http.EncoderRegistry
import groovyx.net.http.HTTPBuilder
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class SalesOrderService {

    def grailsApplication
    def sapService
    def intelisisService
    def p1Service

    def send() {

        int saved = 0
        try {
            def list = SalesOrder.withCriteria {

                or{
                    eq 'status', 0
                    eq 'status', 2
                }

            }

            if (list.empty) {
                log.debug "No hay pedidos para guardar en ERP"
                return
            }

            def connectionSap
            if (grailsApplication.config.erp.sap.enabled) connectionSap = sapService.connect()

            list.each { salesOrder ->

                if (grailsApplication.config.erp.sap.enabled) {

                    def result = sapService.saveSalesOrder(salesOrder, connectionSap)
                    salesOrder = result.salesOrder


                    if (salesOrder.status != 0 && salesOrder.status != 2) {
                        if (grailsApplication.config.configuration.salesOrder.invoiceNotification) {
                            sendNotification(salesOrder.id.toString(), "Error. El pedido $salesOrder.id no ha sido guardado correctamente en tu ERP. Causa: $result.message ")
                        }
                    }

                } else if (grailsApplication.config.erp.intelisis.enabled)
                    salesOrder = intelisisService.saveSalesOrder(salesOrder)

                else if (grailsApplication.config.erp.p1.enabled)
                    salesOrder = p1Service.saveSalesOrder(salesOrder)

                if (salesOrder.status == 0) {
                    salesOrder.status = 5
                    saved++
                }

                try{
                    salesOrder.attach()
                }catch (Exception ex){
                    log.error('Error al hacer attached', ex)
                }

                salesOrder.save()
            }

            if (grailsApplication.config.erp.sap.enabled)
                sapService.disconnect(connectionSap)

        } catch (Exception e) {
            log.error('Ocurrio un error al procesar los pedidos', e)
        }

        saved
    }

    def save(def salesOrderJson){
        try {
            SalesOrder salesOrder = new SalesOrder()
            salesOrder.id = salesOrderJson.id
            salesOrder.customerId = salesOrderJson.customer_id
            salesOrder.customerCode = salesOrderJson.customer_code
            salesOrder.type = salesOrderJson.type
            salesOrder.salesManHandyName = salesOrderJson.created_by?.name
            salesOrder.salesManHandyUser = salesOrderJson.created_by?.username
            salesOrder.scheduledDateForDelivery = salesOrderJson.delivery_scheduled_date ? new Date(salesOrderJson.delivery_scheduled_date) : null
            salesOrder.mobileDateCreated = salesOrderJson.date_created ? new Date(salesOrderJson.date_created) : null
            salesOrder.comment = salesOrderJson.comment

            salesOrderJson.items.each { item ->
                SalesOrderItem salesOrderItem = new SalesOrderItem()
                salesOrderItem.id = item.id
                salesOrderItem.productCode = item.product_code
                salesOrderItem.productId = item.product_id
                salesOrderItem.price = item.price
                salesOrderItem.quantity = item.quantity
                salesOrderItem.comments = item.comments
                salesOrder.addToItems(salesOrderItem)
            }

            if (!salesOrder.save(flush: true)) {
                log.error("Error al guardar el pedido $salesOrderJson.id")
                salesOrder.errors.each { log.error it }
                return false
            }

        } catch (Exception e) {
            log.error("Exception al guardar el pedido $salesOrderJson.id", e)
            return false
        }

        return true
    }

    def checkInvoice() {
        def configuration = Sync.configuration

        try {
            Date date = new Date()

            use(TimeCategory) {

                Date today = new Date().clearTime()
                Date lastDate = configuration.checkInvoiceLastUpdated
                int hours = (lastDate.getHours() * 100) + (lastDate.getMinutes())
                lastDate = lastDate.clearTime()

                def list = []
                if (today == lastDate) {
                    list = SapInvoice.withCriteria {
                        eq 'dateCreated', lastDate
                        gt 'docTime', hours
                        like 'comment', '%Handy%'
                    }
                } else {
                    list = SapInvoice.withCriteria {
                        or {
                            and {
                                eq 'dateCreated', lastDate
                                gt 'docTime', hours
                            }
                            gt 'dateCreated', lastDate
                        }
                        like 'comment', '%Handy%'
                    }
                }

                if (list.empty) {
                    return
                }

                list.each { invoice ->
                    if (invoice.idHandy) {
                        log.info("Se envia push del pedido: $invoice.idHandy")
                        if (!sendNotification(invoice.idHandy, "Nueva factura de cliente\nHandy id: $invoice.idHandy\nDocEntry: $invoice.id\nDocNum: $invoice.docNum\n$invoice\nCliente: $invoice.customerDescription($invoice.customerCode)\nTotal: $invoice.total"))
                            return
                    }

                }
            }

            configuration.checkInvoiceLastUpdated = date
            configuration.save()

        } catch (Exception e) {
            log.error('Ocurrio un error al procesar los pedidos', e)
        }

    }

    def sendNotification(String id, String message) {
        try {
            def http = new HTTPBuilder(grailsApplication.config.handy.server)
            def body = [salesOrderId: id, message: message, type: 'salesOrderNotification']
            http.auth.basic(grailsApplication.config.handy.username, grailsApplication.config.handy.password)
            http.encoderRegistry = new EncoderRegistry(charset: 'UTF-8')

            try {
                http.post(path: '/api/notification/push', body: body, requestContentType: ContentType.JSON) { resp, json ->
                    if (json.error)
                        return false
                    else
                        return true
                }
            } catch (Exception ex) {
                log.error "Caso 3: Error en solicitud web. Error:", ex
                return false
            }
        } catch (Exception e) {
            log.error("Error al enviar notificación", e)
            return false
        }
    }


}
