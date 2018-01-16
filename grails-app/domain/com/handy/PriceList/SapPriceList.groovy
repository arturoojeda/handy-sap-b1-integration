package com.handy.PriceList

import grails.util.Environment
import grails.util.Holders

class SapPriceList {

    long id
    String name

    static constraints = {
    }

    static mapping = {
        if (!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isSapEnabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name

            id column: getTableConfig().column.id.name, sqlType: getTableConfig().column.id.sqlType
            name column: getTableConfig().column.name.name, sqlType: getTableConfig().column.name.sqlType
            
        } else{
            datasource 'local'
        }

    }
    
    static def isSapEnabled(){
        return Holders.grailsApplication.config.erp.sap.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.sap.tables.priceList
    }

}
