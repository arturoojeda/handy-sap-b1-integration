package com.handy.SalesOrder

class SalesOrderItem {
    String productCode
    long productId
    BigDecimal price
    BigDecimal quantity
    String comments
    BigDecimal total

    static belongsTo = [salesOrder: SalesOrder]

    static constraints = {
        comments nullable: true
    }

    static mapping = {
        datasource 'local'
        version false

        id generator: 'assigned'
        sort id: "asc"
        total formula: 'quantity * price'
    }
}
