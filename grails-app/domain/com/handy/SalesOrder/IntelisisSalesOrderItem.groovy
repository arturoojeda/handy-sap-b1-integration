package com.handy.SalesOrder

import grails.util.Environment
import grails.util.Holders

class IntelisisSalesOrderItem implements Serializable {

    long salesOrderId
    String productCode
    BigDecimal price

    BigDecimal row = 2048.0 //i * 2048
    String warehouse = 'ACC-VER'
    String agent = 'AGLC-001'

    long rowSub = 0
    long rowId = 1
    BigDecimal tax = 16.0
    BigDecimal fact = 1.0
    String unit = 'PZA'
    Date deliveryDate
    int branch = 1401
    int originBranch = 1401
    int uen = 2
    BigDecimal quantity = 1

    static constraints = {
    }

    static mapping = {

        if(isIntelisisEnabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name //VentaD

            id composite: ['salesOrderId', 'row', 'rowSub']

            salesOrderId    column: getTableConfig().column.id.name,             sqlType: getTableConfig().column.id.sqlType
            productCode     column: getTableConfig().column.productCode.name,             sqlType: getTableConfig().column.productCode.sqlType
            price           column: getTableConfig().column.price.name,             sqlType: getTableConfig().column.price.sqlType
            row             column: getTableConfig().column.row.name,             sqlType: getTableConfig().column.row.sqlType
            warehouse       column: getTableConfig().column.warehouse.name,             sqlType: getTableConfig().column.warehouse.sqlType
            agent           column: getTableConfig().column.agent.name,             sqlType: getTableConfig().column.agent.sqlType
            rowSub          column: getTableConfig().column.rowSub.name,             sqlType: getTableConfig().column.rowSub.sqlType
            rowId           column: getTableConfig().column.rowId.name,             sqlType: getTableConfig().column.rowId.sqlType
            tax             column: getTableConfig().column.tax.name,             sqlType: getTableConfig().column.tax.sqlType
            fact            column: getTableConfig().column.fact.name,             sqlType: getTableConfig().column.fact.sqlType
            unit            column: getTableConfig().column.unit.name,             sqlType: getTableConfig().column.unit.sqlType
            deliveryDate    column: getTableConfig().column.deliveryDate.name,             sqlType: getTableConfig().column.deliveryDate.sqlType
            branch          column: getTableConfig().column.branch.name,             sqlType: getTableConfig().column.branch.sqlType
            originBranch    column: getTableConfig().column.originBranch.name,             sqlType: getTableConfig().column.originBranch.sqlType
            uen             column: getTableConfig().column.uen.name,             sqlType: getTableConfig().column.uen.sqlType
            quantity        column: getTableConfig().column.quantity.name,             sqlType: getTableConfig().column.quantity.sqlType

        } else{
            datasource 'local'
            id column: 'id', sqlType: 'int', generator: 'assigned'
        }

    }

    static def isIntelisisEnabled(){
        return Holders.grailsApplication.config.erp.intelisis.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.intelisis.tables.salesOrder.item
    }

}
