package com.handy.SalesOrder

class SalesOrder {
    String customerCode
    long customerId

    String type
    boolean deleted = false
    Date scheduledDateForDelivery
    Date mobileDateCreated
    int status = 0
    long documentId = 0
    long documentNum = 0
    String salesManHandyName
    String salesManHandyUser
    String comment
    String listPrice

    static hasMany = [items: SalesOrderItem]

    static constraints = {
        type nullable: true
        scheduledDateForDelivery nullable: true
        mobileDateCreated nullable: true
        salesManHandyName nullable: true
        salesManHandyUser nullable: true
        comment nullable: true
        listPrice nullable: true
    }

    static mapping = {
        datasource 'local'
        version false

        id generator: 'assigned'
        documentId defaultValue: "0"
        documentNum defaultValue: "0"
    }

    BigDecimal getTotal(){
        if (!items) return 0
        def total = 0
        items.each {
            if (it.quantity && it.productCode) total += it.quantity * it.price
        }
        total
    }

    int getProductQuantity(){
        items?.size() ?: 0
    }

    BigDecimal getTotalQuantity(){
        if (!items) return 0
        items.sum {
            it.quantity
        }
    }

    def getFinalComment() {
        String comment = "Handy ID: |$id| Tipo: ${type ?: 'Regular'}. Vendedor: |$salesManHandyName|$salesManHandyUser| Comentario: $comment"
        if (comment.length() > 253)
            comment = comment.substring(0, 252)
        comment
    }
}
