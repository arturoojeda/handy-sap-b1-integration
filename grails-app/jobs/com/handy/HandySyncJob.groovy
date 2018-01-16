package com.handy

import grails.util.Holders

class HandySyncJob {

    def customerService
    def productService
    def productPriceCustomerService
    def concurrent = false
    def grailsApplication
    def priceListService
    def agreementService

    static triggers = {
      simple repeatInterval: Holders.grailsApplication.config.configuration.params.ths
    }

    def execute() {

        if(grailsApplication.config.configuration.product.send){
            productService.sync()
        }

        if (Sync.get(1).productFinished && (grailsApplication.config.configuration.priceList.send || grailsApplication.config.configuration.priceList.sendFromProduct) ) {
            priceListService.sync()
        }

        if(grailsApplication.config.configuration.customer.send){
            customerService.sync()
        }

        if(Sync.get(1).productFinished && Sync.get(1).customerFinished && grailsApplication.config.configuration.productPriceCustomer.send){
            if(grailsApplication.config.configuration.productPriceCustomer.agreements){
                agreementService.sendAacuerdo()
                agreementService.sendAlacuerdo()
            } else
                productPriceCustomerService.sync()
        }
    }

}
