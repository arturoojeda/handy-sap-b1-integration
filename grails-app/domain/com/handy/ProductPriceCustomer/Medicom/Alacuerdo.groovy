package com.handy.ProductPriceCustomer.Medicom

class Alacuerdo implements Serializable{

    int docEntry
    int logInst
    String productCode
    BigDecimal price
    int status = 0

    static constraints = {
    }

    static mapping = {
        datasource 'local'
        version false
        id composite: ['docEntry', 'logInst','productCode']
    }
}