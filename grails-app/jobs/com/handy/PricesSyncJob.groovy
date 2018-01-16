package com.handy

import grails.util.Holders

class PricesSyncJob {

    def syncService
    def concurrent = false
    def grailsApplication

    static triggers = {
        cron name: 'pricesTrigger', cronExpression: Holders.grailsApplication.config.configuration.params.tp
    }

    def execute() {
        log.info("PricesSyncJob")
        if (grailsApplication.config.configuration.priceList.send) {
            syncService.sync(Sync.PRICE_LIST)
            syncService.sync(Sync.PRICE_LIST_ITEM)
        }

        if (grailsApplication.config.configuration.productPriceCustomer.send) {

            if (grailsApplication.config.configuration.productPriceCustomer.agreements) {

                syncService.sync(Sync.ALACUERDO)
                syncService.sync(Sync.AACUERDO)

            } else
                syncService.sync(Sync.PRODUCT_PRICE_CUSTOMER)

        }
    }

}
