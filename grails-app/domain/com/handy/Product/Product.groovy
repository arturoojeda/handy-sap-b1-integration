package com.handy.Product

import com.handy.PriceList.PriceListItem
import com.handy.PriceList.SapPriceListItem
import grails.util.Holders

class Product {

    String id
    String description
    String family
    long familyId
    BigDecimal price
    BigDecimal price2
    BigDecimal price3
    BigDecimal price4
    BigDecimal price5
    int status = 0
    String enabled

    String barcode

    static constraints = {
        barcode nullable: true
        description nullable: true
        family nullable: true
        price nullable: true
        price2 nullable: true
        price3 nullable: true
        price4 nullable: true
        price5 nullable: true
        enabled nullable: true
    }

    static mapping = {
        datasource 'local'
        version false

        id generator: 'assigned'
        description sqlType: 'varchar(500)'
        family sqlType: 'varchar(500)'
        barcode sqlType: 'varchar(500)'
    }

    def getCode() {
        id
    }

    def getFinalPrice() {

        def priceListItem
        def config = Holders.grailsApplication.config

        if (config.erp.sap.enabled)
            priceListItem = SapPriceListItem.findByProductCodeAndListId(id, primaryList)

        else if (config.erp.intelisis.enabled)
            priceListItem = PriceListItem.findByProductCodeAndListCode(id, primaryList)

        else if(config.erp.p1.enabled)
            return price ?: 0.00


        if (priceListItem)
            return priceListItem.price
        else
            return 0.00
    }

    def getPrimaryList() {

        def config = Holders.grailsApplication.config

        if (config.erp.sap.enabled)
            return config.erp.sap.tables.priceList.primaryList
        else if (config.erp.intelisis.enabled)
            return config.erp.intelisis.tables.priceList.primaryList
        else
            return 0
    }

}
