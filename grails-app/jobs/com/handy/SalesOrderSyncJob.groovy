package com.handy

import com.handy.SalesOrder.SalesOrder
import grails.util.Holders

class SalesOrderSyncJob {

    def concurrent = false
    def grailsApplication
    def salesOrderService

    static triggers = {
      simple repeatInterval: Holders.grailsApplication.config.configuration.params.tso
    }

    def execute() {
        if(grailsApplication.config.configuration.sqs.enabled){
            SalesOrder.withTransaction {
                salesOrderService.send()
            }
        }
        if(grailsApplication.config.configuration.salesOrder.invoiceNotification){
            salesOrderService.checkInvoice()
        }
    }

}
