package com.handy

import grails.util.Holders

class CustomerAndProductSyncJob {

    def syncService
    def concurrent = false
    def grailsApplication

    static triggers = {
        cron name: 'customerAndProduct', cronExpression: Holders.grailsApplication.config.configuration.params.tcp
    }

    def execute() {
        log.info("CustomerAndProductSyncJob")
        if (grailsApplication.config.configuration.product.send) {
            //syncService.sync(Sync.GENERAL_PRICE_LIST)
            syncService.sync(Sync.PRODUCT)
        }

        if (grailsApplication.config.configuration.customer.send) {

            if (!grailsApplication.config.erp.intelisis.enabled)
                syncService.sync(Sync.ZONE)

            syncService.sync(Sync.CUSTOMER)
        }

    }

}
