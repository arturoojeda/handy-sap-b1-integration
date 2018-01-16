package com.handy

import com.google.common.base.Equivalence
import com.handy.ProductPriceCustomer.Medicom.Aacuerdo
import com.handy.Zone.Zone

class AacuerdoEquivalence extends Equivalence<Aacuerdo> {

    def grailsApplication

    @Override
    protected boolean doEquivalent(Aacuerdo o1, Aacuerdo t1) {

        return  o1.docEntry == t1.docEntry &&
                o1.logInst == t1.logInst &&
                o1.customerCode == t1.customerCode &&
                o1.enabled == t1.enabled
    }

    @Override
    protected int doHash(Aacuerdo aacuerdo) {
        aacuerdo.customerCode.hashCode()
    }

}