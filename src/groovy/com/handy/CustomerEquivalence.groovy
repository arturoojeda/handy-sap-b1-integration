package com.handy

import com.google.common.base.Equivalence
import com.handy.Customer.Customer
import grails.util.Holders

class CustomerEquivalence extends Equivalence<Customer> {

    @Override
    protected boolean doEquivalent(Customer customer, Customer t1) {

        if (Holders.grailsApplication.config.erp.sap.enabled) {
            customer.description == t1.description &&
                    customer.city == t1.city &&
                    customer.postalCode == t1.postalCode &&
                    customer.owner == t1.owner &&
                    customer.phoneNumber == t1.phoneNumber &&
                    customer.email == t1.email &&
                    customer.comments == t1.comments &&
                    customer.enabled == t1.enabled &&
                    customer.zoneId == t1.zoneId &&
                    customer.priceList == t1.priceList &&
                    customer.balance?.setScale(2, BigDecimal.ROUND_DOWN) == t1.balance &&
                    customer.credit?.setScale(2, BigDecimal.ROUND_DOWN) == t1.credit
        } else if (Holders.grailsApplication.config.erp.p1.enabled) {
            customer.description == t1.description &&
                    customer.address == t1.address &&
                    customer.address2 == t1.address2 &&
                    customer.city == t1.city &&
                    customer.postalCode == t1.postalCode &&
                    customer.owner == t1.owner &&
                    customer.phoneNumber == t1.phoneNumber &&
                    customer.email == t1.email &&
                    customer.comments == t1.comments &&
                    customer.enabled == t1.enabled &&
                    customer.zoneId == t1.zoneId &&
                    customer.priceList == t1.priceList &&
                    customer.balance?.setScale(2, BigDecimal.ROUND_DOWN) == t1.balance &&
                    customer.credit?.setScale(2, BigDecimal.ROUND_DOWN) == t1.credit
        } else if (Holders.grailsApplication.config.erp.intelisis.enabled) {
            customer.description == t1.description &&
                    customer.enabled == t1.enabled &&
                    customer.zone == t1.zone &&
                    customer.address == t1.address &&
                    customer.address2 == t1.address2 &&
                    customer.city == t1.city &&
                    customer.postalCode == t1.postalCode &&
                    customer.owner == t1.owner &&
                    customer.phoneNumber == t1.phoneNumber &&
                    customer.email == t1.email &&
                    customer.comments == t1.comments &&
                    customer.priceList == t1.priceList
        }

    }

    @Override
    protected int doHash(Customer customer) {
        customer.code.hashCode()
    }

}