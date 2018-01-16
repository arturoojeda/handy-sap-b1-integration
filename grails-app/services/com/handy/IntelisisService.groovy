package com.handy

import com.handy.Customer.IntelisisCustomer
import com.handy.SalesOrder.IntelisisSalesOrder
import com.handy.SalesOrder.IntelisisSalesOrderItem
import com.handy.SalesOrder.SalesOrder
import groovy.sql.Sql

class IntelisisService {

    def dataSource_erp

    def saveSalesOrder(SalesOrder salesOrder){
        try{
            IntelisisSalesOrder intelisisSalesOrder
            IntelisisCustomer customer = IntelisisCustomer.findById(salesOrder.customerCode)

            if(!customer)
                log.error("Este cliente no está dado de alta en Intelisis ${salesOrder.dumb()}")

            def user = getUser(salesOrder.salesManHandyUser)

            intelisisSalesOrder = new IntelisisSalesOrder()
            intelisisSalesOrder.customerCode = salesOrder.customerCode
            intelisisSalesOrder.condition = customer.condition
            intelisisSalesOrder.dateCreated1 = new Date()
            intelisisSalesOrder.lastUpdated1 = new Date()
            intelisisSalesOrder.deliveryDate = salesOrder.scheduledDateForDelivery ?: new Date()
            intelisisSalesOrder.limitDate = salesOrder.scheduledDateForDelivery ?: new Date()
            intelisisSalesOrder.comments = salesOrder.finalComment

            intelisisSalesOrder.priceList = customer.priceList ?: 'LISTA "A" FABRICAS'
            intelisisSalesOrder.warehouse = user.almacen ?: 'AG-LC'
            intelisisSalesOrder.agent = user.agente
            intelisisSalesOrder.branch = user.sucursal
            intelisisSalesOrder.originBranch = user.sucursal
            intelisisSalesOrder.saleBranch = user.sucursal
            intelisisSalesOrder.uen = user.uen
            intelisisSalesOrder.user = user.usuario

            println intelisisSalesOrder.dump()

            if(!intelisisSalesOrder.save())
                throw new Exception("SalesOrder $salesOrder.id is not valid $intelisisSalesOrder.errors")

            salesOrder.items.eachWithIndex { salesOrderItem, i ->
                def item = new IntelisisSalesOrderItem()

                item.salesOrderId = intelisisSalesOrder.id
                item.productCode = salesOrderItem.productCode
                item.price = salesOrderItem.price
                item.quantity = salesOrderItem.quantity
                item.row = (i + 1) * 2048
                item.rowId = i + 1I
                item.rowSub = 0
                item.tax = 16.0
                item.deliveryDate = salesOrder.scheduledDateForDelivery
                item.warehouse = intelisisSalesOrder.warehouse
                item.agent = intelisisSalesOrder.agent
                if(!item.save())
                    throw new Exception("SalesOrder $salesOrder.id is not valid $intelisisSalesOrder.errors")
            }

            log.info("Pedido $salesOrder.id guardado correctamente en Intelisis")

        }catch (Exception e){
            salesOrder.status = 1
            log.error("Error al crear pedido $salesOrder.id en Intelisis", e)
        }

        salesOrder
    }

    def getUser(String username){
        def sql = new Sql(dataSource_erp)
        def result = sql.rows("select usuario, oficina as 'agente', defAlmacen as 'almacen', sucursal, uen from usuario where email = $username and oficina is not null and defAlmacen is not null")

        if(result.isEmpty())
            log.error("El usuario no está dado de alta correctamente en Intelisis $username")

        result.first()
    }

}
