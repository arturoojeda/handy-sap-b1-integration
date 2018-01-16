package com.handy.ProductPriceCustomer

class ProductPriceCustomer implements Serializable {

    String productCode
    String customerCode
    BigDecimal price
    BigDecimal discount
    int listId
    boolean enabled = true
    int status = 0


    static constraints = {
    }

    static mapping = {
        datasource 'local'
        version false

        id composite: ['productCode', 'customerCode']
        productCode sqlType: 'varchar(500)'
        customerCode sqlType: 'varchar(500)'
    }
}
