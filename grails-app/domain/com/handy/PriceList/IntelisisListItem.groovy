package com.handy.PriceList

import grails.util.Environment
import grails.util.Holders

class IntelisisListItem implements Serializable {

    String productCode
    String listCode
    BigDecimal price

    static constraints = {
    }

    static mapping = {
        if (!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isIntelisisEnabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name

            id composite: ['productCode', 'listCode']
            productCode column: getTableConfig().productCode.name, sqlType: getTableConfig().productCode.sqlType
            listCode column: getTableConfig().listCode.name, sqlType: getTableConfig().listCode.sqlType
            price column: getTableConfig().price.name, sqlType: getTableConfig().price.sqlType

        } else {
            datasource 'local'
            id composite: ['productCode', 'listCode']
        }
    }

    static def isIntelisisEnabled(){
        return Holders.grailsApplication.config.erp.intelisis.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.intelisis.tables.priceList.priceListItem
    }
}
