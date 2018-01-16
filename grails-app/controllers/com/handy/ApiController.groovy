package com.handy

import com.handy.Customer.P1Customer
import com.handy.Inventory.P1Inventory
import com.handy.Product.P1Product
import grails.converters.JSON

class ApiController {

    def apiService

    static allowedMethods = [ productPrice: 'POST']

    def productPrice() {
        try{
            render ([error: false, message: 'OK', products: apiService.productPriceAndInventory(request.JSON)] as JSON)
        }
        catch (Exception e){
            log.error('Error al solicitar inventario y precio de producto', e)
            render ([error: true, message: 'Invalid request'] as JSON)
        }
    }

}
