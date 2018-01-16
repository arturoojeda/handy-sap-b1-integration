package com.handy

import com.google.common.base.Equivalence
import com.handy.Product.Product
import grails.util.Holders

class ProductEquivalence extends Equivalence<Product> {

    @Override
    protected boolean doEquivalent(Product product, Product t1) {
        if (Holders.grailsApplication.config.erp.sap.enabled) {
            product.code == t1.code &&
                    product.family == t1.family &&
                    product.description == t1.description &&
                    product.barcode == t1.barcode
        } else if (Holders.grailsApplication.config.erp.p1.enabled) {
            product.familyId == t1.familyId &&
                    product.description == t1.description &&
                    product.price?.setScale(2, BigDecimal.ROUND_DOWN) == t1.price &&
                    product.price2?.setScale(2, BigDecimal.ROUND_DOWN) == t1.price2 &&
                    product.price3?.setScale(2, BigDecimal.ROUND_DOWN) == t1.price3 &&
                    product.price4?.setScale(2, BigDecimal.ROUND_DOWN) == t1.price4 &&
                    product.price5?.setScale(2, BigDecimal.ROUND_DOWN) == t1.price5 &&
                    product.enabled == t1.enabled
        } else if (Holders.grailsApplication.config.erp.intelisis.enabled) {
            product.description == t1.description &&
                    product.family == t1.family &&
                    product.enabled == t1.enabled
        }

    }

    @Override
    protected int doHash(Product product) {
        product.code.hashCode()
    }

}