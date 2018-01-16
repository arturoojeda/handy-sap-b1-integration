package com.handy

import com.google.common.base.Equivalence
import com.handy.ProductPriceCustomer.Medicom.Alacuerdo
import com.handy.Zone.Zone

class AlacuerdoEquivalence extends Equivalence<Alacuerdo> {

    def grailsApplication

    @Override
    protected boolean doEquivalent(Alacuerdo o1, Alacuerdo t1) {
        if(o1.price){
            return  o1.docEntry == t1.docEntry &&
                    o1.logInst == t1.logInst &&
                    o1.productCode == t1.productCode &&
                    o1.price.setScale(2, BigDecimal.ROUND_DOWN) == t1.price.setScale(2, BigDecimal.ROUND_DOWN)

        }else{
            return true
        }
    }

    @Override
    protected int doHash(Alacuerdo alacuerdo) {
        alacuerdo.productCode.hashCode()
    }

}