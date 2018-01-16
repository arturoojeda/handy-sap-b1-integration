package com.handy.ProductPriceCustomer.Medicom

import grails.util.Environment
import grails.util.Holders

class SapAacuerdo implements Serializable {

    int docEntry
    int logInst
    String customerCode
    String enabled

    static constraints = {
    }

    static mapping = {
        if(!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isSapEnabled() && getTableConfig().enabled && getAgreementsConfig()){
            datasource 'erp'
            version false
            table getTableConfig().name

            id              composite: ['docEntry', 'logInst']
            docEntry        column: getTableConfig().column.docEntry.name,      sqlType: getTableConfig().column.docEntry.sqlType
            logInst         column: getTableConfig().column.logInst.name,       sqlType: getTableConfig().column.logInst.sqlType
            customerCode    column: getTableConfig().column.customerCode.name,  sqlType: getTableConfig().column.customerCode.sqlType
            enabled         column: getTableConfig().column.enabled.name,       sqlType: getTableConfig().column.enabled.sqlType

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

    static def isSapEnabled(){
        return Holders.grailsApplication.config.erp.sap.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.sap.tables.aacuerdo
    }

    static def getAgreementsConfig(){
        return Holders.grailsApplication.config.configuration.productPriceCustomer.agreements
    }
}
