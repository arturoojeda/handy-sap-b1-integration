package com.handy

import com.google.common.base.Equivalence
import com.handy.Zone.Zone

class ZoneEquivalence extends Equivalence<Zone> {

    @Override
    protected boolean doEquivalent(Zone zone, Zone t1) {
        zone.name == t1.name
    }

    @Override
    protected int doHash(Zone zone) {
        zone.name.hashCode()
    }

}