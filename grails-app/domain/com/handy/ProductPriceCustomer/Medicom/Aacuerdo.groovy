package com.handy.ProductPriceCustomer.Medicom

class Aacuerdo implements Serializable {

    int docEntry
    int logInst
    String customerCode
    String enabled
    int status = 0

    static constraints = {
    }

    static mapping = {
        datasource 'local'
        version false
        id composite: ['docEntry', 'logInst']
    }

}
