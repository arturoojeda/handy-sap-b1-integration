package com.handy

import com.handy.Customer.P1Customer
import com.handy.Inventory.P1Inventory
import com.handy.Product.P1Product
import groovy.sql.Sql

import java.math.RoundingMode

class ApiService {

    def grailsApplication
    def dataSource_erp

    def productPriceAndInventory(def json) {
        String customerCode = json.customer_code
        def jsonProducts = json.products
        def products = []

        jsonProducts.each {
            if (grailsApplication.config.erp.p1.enabled) {

                P1Inventory inventory
                def customer = P1Customer.findById(customerCode)
                def product = P1Product.get(it.code)
                def sql = new Sql(dataSource_erp)
                def listaPreciosDet

                def precioU = product.getListPrice(customer.priceList)

                if (product)
                    inventory = P1Inventory.findByProductIdAndWarehouseId(product.erpId, grailsApplication.config.erp.p1.company.warehouse)

                if (customer.listaPrecios > 1) {
                    listaPreciosDet = sql.firstRow("Select * from TbaListaPreciosDet where IDListaPrecios = ${customer.listaPrecios} and IDProducto = $product.erpId")
                }

                if (listaPreciosDet != null) {
                    def priceWithoutTax = product.getPriceWithoutTax(listaPreciosDet.get('TipoPrecio'))
                    def countDecimal = listaPreciosDet.get('DescPorc')
                    def descPorc = countDecimal.toBigDecimal().setScale(4, RoundingMode.DOWN)
                    def desc = descPorc * priceWithoutTax
                    def iva2 = product.iva.add(new BigDecimal("1")).setScale(2, RoundingMode.DOWN)
                    precioU = ((priceWithoutTax - desc) * iva2).setScale(4, RoundingMode.DOWN)
                }

                if (inventory && customer) {
                    products.add([code: it.code, price: precioU, inventory: inventory.quantity])
                }

            }
        }

        products
    }
}
