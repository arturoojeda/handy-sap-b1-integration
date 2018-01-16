package com.handy.Customer

import grails.util.Environment
import grails.util.Holders

class SapCustomer {

    //Propiedades necesarias
    String id
    String description
    String enabled

    //Propiedades opcionales
    String address
    String city
    String postalCode
    String owner
    String phoneNumber
    String email
    String comments
    long zoneId
    BigDecimal balance
    BigDecimal credit
    long priceList


    static constraints = {
        address nullable: true
        city nullable: true
        postalCode nullable: true
        owner nullable: true
        phoneNumber nullable: true
        email nullable: true
        comments nullable: true
        balance nullable: true
        credit nullable: true
    }

    static mapping = {
        if (!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isSapEnabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name

            id              column: getTableConfig().column.id.name,                sqlType: getTableConfig().column.id.sqlType, generator: 'assigned'
            description     column: getTableConfig().column.description.name,       sqlType: getTableConfig().column.description.sqlType
            enabled         column: getTableConfig().column.enabled.name,           sqlType: getTableConfig().column.enabled.sqlType

            address         column: getTableConfig().column.address.name,           sqlType: getTableConfig().column.address.sqlType
            city            column: getTableConfig().column.city.name,              sqlType: getTableConfig().column.city.sqlType
            postalCode      column: getTableConfig().column.postalCode.name,        sqlType: getTableConfig().column.postalCode.sqlType
            owner           column: getTableConfig().column.owner.name,             sqlType: getTableConfig().column.owner.sqlType
            phoneNumber     column: getTableConfig().column.phoneNumber.name,       sqlType: getTableConfig().column.phoneNumber.sqlType
            email           column: getTableConfig().column.email.name,             sqlType: getTableConfig().column.email.sqlType
            comments        column: getTableConfig().column.comments.name,          sqlType: getTableConfig().column.comments.sqlType
            zoneId          column: getTableConfig().column.salesPersonCode.name,   sqlType: getTableConfig().column.salesPersonCode.sqlType
            balance         column: getTableConfig().column.balance.name,           sqlType: getTableConfig().column.balance.sqlType
            credit          column: getTableConfig().column.credit.name,            sqlType: getTableConfig().column.credit.sqlType
            priceList       column: getTableConfig().column.priceList.name,         sqlType: getTableConfig().column.priceList.sqlType

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
        return Holders.grailsApplication.config.erp.sap.tables.customer
    }

    def getCode() {
        return id
    }
}
