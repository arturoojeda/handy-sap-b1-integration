package com.handy.Customer

import grails.util.Environment
import grails.util.Holders

class IntelisisCustomer {


    //Propiedades necesarias
    String id
    String description
    String zone
    String enabled

    //Propiedades opcionales
    String address
    String address2
    String city
    String postalCode
    String owner
    String phoneNumber
    String email
    String comments
    String priceList
    String type
    String condition
    String user

    Date lastUpdated

    static constraints = {
        address nullable: true
        address2 nullable: true
        city nullable: true
        postalCode nullable: true
        owner nullable: true
        phoneNumber nullable: true
        email nullable: true
        comments nullable: true
        priceList nullable: true
        type nullable: true
        condition nullable: true
        user nullable: true
    }

    static mapping = {
        if (!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isIntelisisEnabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name

            id              column: getTableConfig().column.id.name,                sqlType: getTableConfig().column.id.sqlType, generator: 'assigned'
            description     column: getTableConfig().column.description.name,       sqlType: getTableConfig().column.description.sqlType
            zone            column: getTableConfig().column.zone.name,              sqlType: getTableConfig().column.zone.sqlType
            enabled         column: getTableConfig().column.enabled.name,           sqlType: getTableConfig().column.enabled.sqlType

            address         column: getTableConfig().column.address.name,           sqlType: getTableConfig().column.address.sqlType
            address2        column: getTableConfig().column.address2.name,          sqlType: getTableConfig().column.address2.sqlType
            city            column: getTableConfig().column.city.name,              sqlType: getTableConfig().column.city.sqlType
            postalCode      column: getTableConfig().column.postalCode.name,        sqlType: getTableConfig().column.postalCode.sqlType
            owner           column: getTableConfig().column.owner.name,             sqlType: getTableConfig().column.owner.sqlType
            phoneNumber     column: getTableConfig().column.phoneNumber.name,       sqlType: getTableConfig().column.phoneNumber.sqlType
            email           column: getTableConfig().column.email.name,             sqlType: getTableConfig().column.email.sqlType
            comments        column: getTableConfig().column.comments.name,          sqlType: getTableConfig().column.comments.sqlType
            priceList       column: getTableConfig().column.priceList.name,         sqlType: getTableConfig().column.priceList.sqlType
            type            column: getTableConfig().column.type.name,              sqlType: getTableConfig().column.type.sqlType
            condition       column: getTableConfig().column.condition.name,         sqlType: getTableConfig().column.condition.sqlType
            user            column: getTableConfig().column.user.name,              sqlType: getTableConfig().column.user.sqlType
            lastUpdated     column: getTableConfig().column.lastUpdated.name,       sqlType: getTableConfig().column.lastUpdated.sqlType
        } else{
            datasource 'local'
        }

    }

    static def isIntelisisEnabled(){
        return Holders.grailsApplication.config.erp.intelisis.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.intelisis.tables.customer
    }

    def getCode() {
        return id
    }
}
