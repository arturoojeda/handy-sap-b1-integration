package com.handy

import com.google.common.base.Equivalence
import com.handy.PriceList.PriceListItem
import grails.util.Holders

class PriceListItemEquivalence extends Equivalence<PriceListItem> {

    @Override
    protected boolean doEquivalent(PriceListItem priceListItem, PriceListItem t1) {

        if(Holders.grailsApplication.config.erp.intelisis.enabled){
            if(priceListItem.price){
                return priceListItem.price.setScale(2, BigDecimal.ROUND_DOWN) == t1.price &&
                        priceListItem.productCode == t1.productCode &&
                        priceListItem.listCode == t1.listCode

            }else{
                return true
            }
        }else{
            if(priceListItem.listId == t1.listId && priceListItem.price){
                        priceListItem.listId == t1.listId &&
                        priceListItem.price?.setScale(2, BigDecimal.ROUND_DOWN) == t1.price &&
                        priceListItem.productCode == t1.productCode

            }else{
                return true
            }
        }


    }

    @Override
    protected int doHash(PriceListItem priceListItem) {
        priceListItem.productCode.hashCode()
    }

}
