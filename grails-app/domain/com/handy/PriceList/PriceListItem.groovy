package com.handy.PriceList

import grails.util.Holders

class PriceListItem implements Serializable {

    String productCode
    long listId
    BigDecimal price
    String listCode
    int status = 0

    static constraints = {
        listCode nullable: true
    }

    static mapping = {
        datasource 'local'
        version false

        if(Holders.grailsApplication.config.erp.intelisis.enabled){
            id composite: ['productCode', 'listCode'], generator: 'assigned'
        } else{
            id composite: ['productCode', 'listId'], generator: 'assigned'
        }

        price defaultValue: "0"
    }
}
