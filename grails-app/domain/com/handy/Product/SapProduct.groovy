package com.handy.Product

import grails.util.Environment
import grails.util.Holders

class SapProduct {

    //Propiedades necesarias
    String id
    String description
    String family

    //Propiedades opcionales
    String barcode

    static constraints = {
    }

    static mapping = {
        if(!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isSapEnabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name

            id              column: getTableConfig().column.id.name,             sqlType: getTableConfig().column.id.sqlType
            //code            column: getTableConfig().column.code.name,           sqlType: getTableConfig().column.code.sqlType
            description     column: getTableConfig().column.description.name,    sqlType: getTableConfig().column.description.sqlType
            family          column: getTableConfig().column.family.name,         sqlType: getTableConfig().column.family.sqlType

            barcode         column: getTableConfig().column.barcode.name,        sqlType: getTableConfig().column.barcode.sqlType
            //details         column: getTableConfig().column.details.name,        sqlType: getTableConfig().column.details.sqlType
            //price           table: getTableConfig().column.price.priceList.name, column: getTableConfig().column.price.name,          sqlType: getTableConfig().column.price.sqlType
            //applyDiscounts  column: getTableConfig().column.applyDiscounts.name, sqlType: getTableConfig().column.applyDiscounts.sqlType

        } else{
            datasource 'local'
            id column: 'id', sqlType: 'int', generator: 'assigned'
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

    static def isSapEnabled(){
        return Holders.grailsApplication.config.erp.sap.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.sap.tables.product
    }

}


