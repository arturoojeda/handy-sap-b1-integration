package com.handy.ProductPriceCustomer

import grails.util.Environment
import grails.util.Holders

class SapProductPriceCustomer implements Serializable {

    String productCode
    String customerCode
    BigDecimal price
    BigDecimal discount
    int listId
    //boolean enabled = true

    static constraints = {
    }

    static mapping = {
        if (!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if (isSapEnabled() && getTableConfig().enabled) {
            datasource 'erp'
            version false
            table getTableConfig().name

            id composite: ['productCode', 'customerCode']
            productCode column: getTableConfig().column.productCode.name, sqlType: getTableConfig().column.productCode.sqlType
            customerCode column: getTableConfig().column.customerCode.name, sqlType: getTableConfig().column.customerCode.sqlType
            price column: getTableConfig().column.price.name, sqlType: getTableConfig().column.price.sqlType
            discount column: getTableConfig().column.discount.name, sqlType: getTableConfig().column.discount.sqlType
            listId column: getTableConfig().column.listId.name, sqlType: getTableConfig().column.listId.sqlType
            //enabled         column: getTableConfig().column.enabled.name,        sqlType: getTableConfig().column.enabled.sqlType
        } else {
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

    static def isSapEnabled() {
        return Holders.grailsApplication.config.erp.sap.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.sap.tables.productPriceCustomer
    }

}
