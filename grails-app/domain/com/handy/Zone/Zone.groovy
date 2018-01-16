package com.handy.Zone

class Zone {

    long id
    String name

    static constraints = {
        name nullable: true
    }

    static mapping = {
        datasource 'local'
        version false

        id generator: 'assigned'
        name sqlType: 'varchar(500)'
    }

}
