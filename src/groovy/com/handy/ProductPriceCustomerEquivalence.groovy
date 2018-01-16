package com.handy

import com.google.common.base.Equivalence
import com.handy.ProductPriceCustomer.ProductPriceCustomer

class ProductPriceCustomerEquivalence extends Equivalence<ProductPriceCustomer> {

    @Override
    protected boolean doEquivalent(ProductPriceCustomer productPriceCustomer, ProductPriceCustomer t1) {
        if (productPriceCustomer.price) {
            productPriceCustomer.customerCode == t1.customerCode &&
                    productPriceCustomer.productCode == t1.productCode &&
                    productPriceCustomer.price.setScale(2, BigDecimal.ROUND_HALF_UP) == t1.price /*&&
                    productPriceCustomer.discount.setScale(2, BigDecimal.ROUND_HALF_UP) == t1.discount &&
                    productPriceCustomer.listId == t1.listId*/
        } else {
            true
        }

    }

    @Override
    protected int doHash(ProductPriceCustomer product) {
        product.customerCode.hashCode() +
                product.productCode.hashCode()
    }

}