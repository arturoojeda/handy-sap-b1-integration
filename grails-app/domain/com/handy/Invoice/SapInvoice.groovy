package com.handy.Invoice

import grails.util.Environment
import grails.util.Holders

class SapInvoice {

    long id //docEntry
    long docNum
    Date dateCreated
    int docTime
    String customerCode
    String customerDescription
    String comment
    double total

    static constraints = {

    }

    static mapping = {
        if(!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isSapEnabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name

            id                      column: getTableConfig().column.id.name,                     sqlType: getTableConfig().column.id.sqlType
            docNum                  column: getTableConfig().column.docNum.name,                 sqlType: getTableConfig().column.docNum.sqlType
            dateCreated             column: getTableConfig().column.dateCreated.name,            sqlType: getTableConfig().column.dateCreated.sqlType
            customerCode            column: getTableConfig().column.customerCode.name,           sqlType: getTableConfig().column.customerCode.sqlType
            customerDescription     column: getTableConfig().column.customerDescription.name,    sqlType: getTableConfig().column.customerDescription.sqlType
            comment                 column: getTableConfig().column.comment.name,                sqlType: getTableConfig().column.comment.sqlType
            total                   column: getTableConfig().column.total.name,                  sqlType: getTableConfig().column.total.sqlType
            docTime                 column: getTableConfig().column.docTime.name,                sqlType: getTableConfig().column.docTime.sqlType

        } else{
            datasource 'local'
        }

    }

    static def isSapEnabled(){
        return Holders.grailsApplication.config.erp.sap.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.sap.tables.invoice
    }

    def getIdHandy(){
        try{
            String [] commentParts = comment.tokenize('|')

            return commentParts[1]
        }catch(Exception e){
            log.error('Error al obtener id de pedido', e)
        }

        return null
    }

}
