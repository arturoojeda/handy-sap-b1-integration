package com.handy

class Sync {

    Date salesOrderLastUpdated
    long salesOrderLastUpdatedTime
    Date checkInvoiceLastUpdated
    boolean productFinished
    boolean customerFinished
    long aacuerdoOffset = 0
    long alacuerdoOffset = 0

    static final String CUSTOMER = 'Customer'
    static final String PRODUCT = 'Product'
    static final String PRODUCT_PRICE_CUSTOMER = 'ProductPriceCustomer'
    static final String ZONE = 'Zone'
    static final String PRICE_LIST = 'PriceList'
    static final String PRICE_LIST_ITEM = 'PriceListItem'
    static final String AACUERDO = 'Aacuerdo'
    static final String ALACUERDO = 'Alacuerdo'

    static constraints = {
    }

    static mapping = {
        datasource 'local'
        version false
    }

    static def getConfiguration(){
        Sync.get(1)
    }
}
