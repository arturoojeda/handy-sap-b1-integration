package com.handy.PriceList

import grails.util.Environment
import grails.util.Holders

class IntelisisPriceList {

    String id
    String name

    static constraints = {
    }

    static mapping = {
        if (!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isIntelisisEnabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name

            id column: getTableConfig().column.id.name, sqlType: getTableConfig().column.id.sqlType
            name column: getTableConfig().column.name.name, sqlType: getTableConfig().column.name.sqlType

        } else{
            datasource 'local'
            id column: 'id', sqlType: 'int'
        }

    }

    static def isIntelisisEnabled(){
        return Holders.grailsApplication.config.erp.intelisis.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.intelisis.tables.priceList
    }

}
