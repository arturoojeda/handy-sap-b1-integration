package com.handy.ProductPriceCustomer.Medicom

import grails.util.Environment
import grails.util.Holders

class SapAlacuerdo implements Serializable {
    int docEntry
    int logInst
    String productCode
    BigDecimal price

    static constraints = {
    }

    static mapping = {
        if (!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if (isSapEnabled() && getTableConfig().enabled && getAgreementsConfig()) {
            datasource 'erp'
            version false
            table getTableConfig().name

            id composite: ['docEntry', 'logInst', 'productCode']
            docEntry column: getTableConfig().column.docEntry.name, sqlType: getTableConfig().column.docEntry.sqlType
            logInst column: getTableConfig().column.logInst.name, sqlType: getTableConfig().column.logInst.sqlType
            productCode column: getTableConfig().column.productCode.name, sqlType: getTableConfig().column.productCode.sqlType
            price column: getTableConfig().column.price.name, sqlType: getTableConfig().column.price.sqlType
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
        return Holders.grailsApplication.config.erp.sap.tables.alacuerdo
    }

    static def getAgreementsConfig() {
        return Holders.grailsApplication.config.configuration.productPriceCustomer.agreements
    }
}
