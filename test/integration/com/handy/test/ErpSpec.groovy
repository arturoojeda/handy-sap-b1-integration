package com.handy.test

import com.handy.SalesOrder.SalesOrder
import com.handy.SalesOrder.SalesOrderItem
import grails.plugin.spock.IntegrationSpec

/**
 * Created by cuauhtemoc on 6/1/16.
 */
class ErpSpec extends IntegrationSpec{
    def salesOrderService

    def setup() {}

    def cleanup() {}

    void "Test connection"() {
        when:
        String comment =  "Handy ID: |100234| Tipo: 'Regular'. Vendedor: |Cuauhtemoc Valero|temo@handy.la"

        String [] commentParts = comment.tokenize('|')

        String salesOrderId = commentParts[1]
        String createdBy = commentParts[4]


        then:
        salesOrderId == '100234'
        createdBy == 'temo@handy.la'
    }

    void "Save Sales Order"(){
        when:
        SalesOrder salesOrder = new SalesOrder()
        salesOrder.customerCode = 'C0232'
        salesOrder.customerId = 120
        salesOrder.type = 'Venta'
        salesOrder.id = 10

        SalesOrderItem salesOrderItem = new SalesOrderItem()
        salesOrderItem.productCode = 'CREMRICHGRAN1'
        salesOrderItem.price = 10.50
        salesOrderItem.quantity = 5

        SalesOrderItem salesOrderItem2 = new SalesOrderItem()
        salesOrderItem2.productCode = 'CREMRICHGRAN4'
        salesOrderItem2.price = 20.10
        salesOrderItem2.quantity = 10

        salesOrder.addToItems(salesOrderItem)
        salesOrder.addToItems(salesOrderItem2)

        salesOrder = salesOrderService.save(salesOrder)

        then:
        salesOrder.documentId != 0
    }

}
