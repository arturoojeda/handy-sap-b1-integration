package com.handy

import com.handy.Customer.Customer
import com.handy.Customer.SapCustomer
import com.handy.Product.SapProduct
import com.handy.Product.Product
import com.handy.ProductPriceCustomer.SapProductPriceCustomer
import com.handy.ProductPriceCustomer.ProductPriceCustomer
import com.handy.SalesOrder.SalesOrder
import com.handy.SalesOrder.SalesOrderItem

class DashboardController {

    def grailsApplication
    def intelisisService

    def index() {
        [
            customerCount: Customer.count,
            erpCustomerCount: SapCustomer.count,
            productCount: Product.count,
            erpProductCount: SapProduct.count,
            productPriceCustomerCount: ProductPriceCustomer.count,
            erpProductPriceCustomerCount: SapProductPriceCustomer.count,
            salesOrderCount: SalesOrder.count,
            config: grailsApplication.config.configuration,
            company: grailsApplication.config.handy.company
        ]
    }

    def pedido(){
        SalesOrder salesOrder = new SalesOrder()
        salesOrder.id = params.id
        salesOrder.comment = params.comment ?: 'Entrega sin empaque'
        salesOrder.customerCode = params.customerCode ?: '00003'
        salesOrder.type = params.type ?: 'Regular'
        salesOrder.deleted = false
        salesOrder.scheduledDateForDelivery = new Date()
        salesOrder.status = 0
        salesOrder.salesManHandyName = params.salesManHandyName ?: 'Alberto Torres'
        salesOrder.salesManHandyUser = params.salesManHandyUser ?:'alberto.torres@miempresa.com.mx'
        salesOrder.addToItems(new SalesOrderItem(productCode: 'ADPEBSMA0003', price: 12.34, quantity: 2 ))
        salesOrder.addToItems(new SalesOrderItem(productCode: 'ADPEBSMA0004', price: 43.21, quantity: 10 ))
        salesOrder.addToItems(new SalesOrderItem(productCode: 'ADPEBSMA0005', price: 0.55, quantity: 1 ))

        salesOrder = intelisisService.saveSalesOrder(salesOrder)

        if(salesOrder.status == 0)
            render 'OK'
        else
            render 'Error al guardar el pedido'

    }
}
