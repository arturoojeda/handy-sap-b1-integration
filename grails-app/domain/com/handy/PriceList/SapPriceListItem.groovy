package com.handy.PriceList

import grails.util.Environment
import grails.util.Holders

class SapPriceListItem implements Serializable {

    String productCode
    long listId
    BigDecimal price

    static constraints = {
    }

    static mapping = {
        if (!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isSapEnabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name

            id composite: ['productCode', 'listId']
            productCode column: getTableConfig().productCode.name, sqlType: getTableConfig().productCode.sqlType
            listId column: getTableConfig().listId.name, sqlType: getTableConfig().listId.sqlType
            price column: getTableConfig().price.name, sqlType: getTableConfig().price.sqlType

        } else {
            datasource 'local'
            id composite: ['productCode', 'listId']
        }
    }

    static def isSapEnabled(){
        return Holders.grailsApplication.config.erp.sap.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.sap.tables.priceList.priceListItem
    }
}
