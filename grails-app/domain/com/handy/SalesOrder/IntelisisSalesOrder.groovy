package com.handy.SalesOrder

import grails.util.Holders

class IntelisisSalesOrder {

    long id
    String customerCode
    Date dateCreated1
    Date lastUpdated1
    Date deliveryDate
    Date limitDate
    String comments

    String priceList = 'GDL PRECIOS'
    String warehouse = 'ACC-VER'
    String agent = 'AGLC-001'
    String user = 'INT5'
    int branch = 1401  //Usuario.Sucursal
    int originBranch = 1401
    int saleBranch = 1401
    int uen = 2 //Casado con usuario

    String condition = 'CONTADO' //Todos los clientes tienen o contado o crédito Cte.[Condicion]

    String company = 'GNAP'
    String type = 'Cotizacion'
    String concept = 'Venta'
    String currency = 'Pesos'
    BigDecimal typeCurrency = 1.0
    String status = 'SINAFECTAR'
    String priority = 'Normal'
    int rowId = 1
    BigDecimal amount = 0.0
    BigDecimal tax = 0.0

    static constraints = {
    }

    static mapping = {

        if(isIntelisisEnabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name //Venta

            id              column: getTableConfig().column.id.name,             sqlType: getTableConfig().column.id.sqlType
            customerCode    column: getTableConfig().column.customerCode.name,   sqlType: getTableConfig().column.customerCode.sqlType
            dateCreated1     column: getTableConfig().column.dateCreated.name,    sqlType: getTableConfig().column.dateCreated.sqlType
            lastUpdated1     column: getTableConfig().column.lastUpdated.name,    sqlType: getTableConfig().column.lastUpdated.sqlType
            deliveryDate    column: getTableConfig().column.deliveryDate.name,    sqlType: getTableConfig().column.deliveryDate.sqlType
            limitDate       column: getTableConfig().column.limitDate.name,    sqlType: getTableConfig().column.limitDate.sqlType
            comments        column: getTableConfig().column.comments.name,    sqlType: getTableConfig().column.comments.sqlType

            priceList       column: getTableConfig().column.priceList.name,    sqlType: getTableConfig().column.priceList.sqlType
            warehouse       column: getTableConfig().column.warehouse.name,    sqlType: getTableConfig().column.warehouse.sqlType
            agent           column: getTableConfig().column.agent.name,    sqlType: getTableConfig().column.agent.sqlType
            company         column: getTableConfig().column.company.name,    sqlType: getTableConfig().column.company.sqlType
            type            column: getTableConfig().column.type.name,    sqlType: getTableConfig().column.type.sqlType
            concept         column: getTableConfig().column.concept.name,    sqlType: getTableConfig().column.concept.sqlType
            currency        column: getTableConfig().column.currency.name,    sqlType: getTableConfig().column.currency.sqlType
            typeCurrency    column: getTableConfig().column.typeCurrency.name,    sqlType: getTableConfig().column.typeCurrency.sqlType
            user            column: getTableConfig().column.user.name,    sqlType: getTableConfig().column.user.sqlType
            status          column: getTableConfig().column.status.name,    sqlType: getTableConfig().column.status.sqlType
            priority        column: getTableConfig().column.priority.name,    sqlType: getTableConfig().column.priority.sqlType
            rowId           column: getTableConfig().column.rowId.name,    sqlType: getTableConfig().column.rowId.sqlType
            condition       column: getTableConfig().column.condition.name,    sqlType: getTableConfig().column.condition.sqlType
            amount          column: getTableConfig().column.amount.name,    sqlType: getTableConfig().column.amount.sqlType
            tax             column: getTableConfig().column.tax.name,    sqlType: getTableConfig().column.tax.sqlType
            branch          column: getTableConfig().column.branch.name,    sqlType: getTableConfig().column.branch.sqlType
            originBranch    column: getTableConfig().column.originBranch.name,    sqlType: getTableConfig().column.originBranch.sqlType
            saleBranch      column: getTableConfig().column.saleBranch.name,    sqlType: getTableConfig().column.saleBranch.sqlType
            uen             column: getTableConfig().column.uen.name,    sqlType: getTableConfig().column.ueni.sqlType

        } else{
            datasource 'local'
            id column: 'id', sqlType: 'int', generator: 'assigned'
            user column: 'stringUser'
        }

    }

    static def isIntelisisEnabled(){
        return Holders.grailsApplication.config.erp.intelisis.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.intelisis.tables.salesOrder
    }

}
