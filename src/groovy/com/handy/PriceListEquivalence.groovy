package com.handy

import com.google.common.base.Equivalence
import com.handy.PriceList.PriceList

/**
 * Created by deric on 07/07/16.
 */
class PriceListEquivalence extends Equivalence<PriceList> {

    @Override
    protected boolean doEquivalent(PriceList priceList, PriceList t1) {

        priceList.id == t1.id && priceList.name == t1.name

    }

    @Override
    protected int doHash(PriceList priceList) {
        priceList.name.hashCode()
    }

}
