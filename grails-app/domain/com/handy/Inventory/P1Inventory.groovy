package com.handy.Inventory

import grails.util.Environment
import grails.util.Holders

class P1Inventory {

    long id
    int productId
    int quantity
    int warehouseId

    static constraints = {
    }
    static mapping = {
        if(!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isP1Enabled() && getTableConfig().enabled){
            datasource 'erp'
            table getTableConfig().name

            version false

            id              column: getTableConfig().column.id.name,            sqlType: getTableConfig().column.id.sqlType
            productId       column: getTableConfig().column.productId.name,     sqlType: getTableConfig().column.productId.sqlType
            quantity        column: getTableConfig().column.quantity.name,      sqlType: getTableConfig().column.quantity.sqlType
            warehouseId     column: getTableConfig().column.warehouseId.name,   sqlType: getTableConfig().column.warehouseId.sqlType

        } else{
            datasource 'local'
        }

    }

    transient beforeUpdate = {
        return false
    }

    transient beforeSave = {
        return false
    }

    def beforeInsert() {
        return false
    }

    static def isP1Enabled(){
        return Holders.grailsApplication.config.erp.p1.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.p1.tables.inventory
    }

}

