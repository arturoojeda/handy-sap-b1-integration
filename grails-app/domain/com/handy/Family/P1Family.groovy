package com.handy.Family

import grails.util.Environment
import grails.util.Holders

class P1Family {

    long id
    String name

    static constraints = {
    }

    static mapping = {
        if(!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isP1Enabled() && getTableConfig().enabled){
            datasource 'erp'
            table getTableConfig().name

            version false

            id       column: getTableConfig().column.id.name,      sqlType: getTableConfig().column.id.sqlType
            name     column: getTableConfig().column.name.name,    sqlType: getTableConfig().column.name.sqlType

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
        return Holders.grailsApplication.config.erp.p1.tables.salesPerson
    }

}
