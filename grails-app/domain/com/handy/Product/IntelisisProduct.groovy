package com.handy.Product

import grails.util.Environment
import grails.util.Holders

class IntelisisProduct {

    //Propiedades necesarias
    String id
    String description
    String family
    String group
    String enabled

    //Propiedades opcionales

    static constraints = {
    }

    static mapping = {
        if(!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isIntelisisEnabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name

            id              column: getTableConfig().column.id.name,             sqlType: getTableConfig().column.id.sqlType
            //code            column: getTableConfig().column.code.name,           sqlType: getTableConfig().column.code.sqlType
            description     column: getTableConfig().column.description.name,    sqlType: getTableConfig().column.description.sqlType
            family          column: getTableConfig().column.family.name,         sqlType: getTableConfig().column.family.sqlType
            group           column: getTableConfig().column.group.name,          sqlType: getTableConfig().column.group.sqlType
            enabled           column: getTableConfig().column.enabled.name,          sqlType: getTableConfig().column.enabled.sqlType
            //price           table: getTableConfig().column.price.priceList.name, column: getTableConfig().column.price.name,          sqlType: getTableConfig().column.price.sqlType
            //applyDiscounts  column: getTableConfig().column.applyDiscounts.name, sqlType: getTableConfig().column.applyDiscounts.sqlType

        } else{
            datasource 'local'
            id column: 'id', sqlType: 'int', generator: 'assigned'
            group column: 'stringGroup'
        }

    }

    transient beforeUpdate = {
        if (!Environment.DEVELOPMENT) {
            throw new RuntimeException('No se permite actualizar')
        }

    }

    transient beforeSave = {
        if (!Environment.DEVELOPMENT) {
            throw new RuntimeException('No se permite actualizar')
        }
    }

    def beforeInsert() {
        if (!Environment.DEVELOPMENT) {
            throw new RuntimeException('No se permite actualizar')
        }
    }

    static def isIntelisisEnabled(){
        return Holders.grailsApplication.config.erp.intelisis.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.intelisis.tables.product
    }
}