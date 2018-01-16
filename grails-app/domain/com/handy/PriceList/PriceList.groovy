package com.handy.PriceList

class PriceList {

    long id //todo String para Napresa, normal long
    String name
    int status = 0

    static constraints = {
    }

    static mapping = {
        datasource 'local'
        version false

        id generator: 'assigned'
        name sqlType: 'varchar(500)'

    }


}
