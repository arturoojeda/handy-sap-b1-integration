package com.handy.Customer

class Customer {

    //Propiedades necesarias
    String id
    String code
    String description
    String zone
    String enabled
    int status = 0
    long zoneId

    //Propiedades opcionales
    String address
    String address2
    String city
    String postalCode
    String latitude
    String longitude
    String owner
    String phoneNumber
    String email
    String comments
    boolean prospect
    boolean mobile
    double discount = 0.0
    BigDecimal balance
    BigDecimal credit
    String priceList

    static constraints = {
        description nullable: true
        zone nullable: true
        address nullable: true
        address2 nullable: true
        city nullable: true
        postalCode nullable: true
        latitude nullable: true
        longitude nullable: true
        owner nullable: true
        phoneNumber nullable: true
        email nullable: true
        comments nullable: true
        enabled nullable: true
        priceList nullable: true
        balance nullable: true
        credit nullable: true
    }

    static mapping = {

        datasource 'local'
        version false

        id generator: 'assigned'
        code sqlType: 'varchar(500)'
        description sqlType: 'varchar(500)'
        zone sqlType: 'varchar(500)'

        address sqlType: 'varchar(500)'
        city sqlType: 'varchar(500)'
        postalCode sqlType: 'varchar(500)'
        latitude sqlType: 'varchar(500)'
        longitude sqlType: 'varchar(500)'
        owner sqlType: 'varchar(500)'
        phoneNumber sqlType: 'varchar(500)'
        email sqlType: 'varchar(500)'
        comments sqlType: 'varchar(500)'

        zoneId defaultValue: "0"
    }

    def getFullAddress(){
        if(address)
            if(address2)
                return address + ' ' + address2
            else
                return address
        else if (address2)
            return address2
        else
            return ''
    }

    def currentZone(){
        if(zone)
            return zone
        else {

        }
    }
}
