package com.handy

import com.google.common.base.Equivalence
import com.google.common.collect.Sets
import com.handy.Customer.Customer
import com.handy.Customer.IntelisisCustomer
import com.handy.Customer.P1Customer
import com.handy.Customer.SapCustomer
import com.handy.PriceList.IntelisisListItem
import com.handy.PriceList.IntelisisPriceList
import com.handy.PriceList.PriceList
import com.handy.PriceList.PriceListItem
import com.handy.PriceList.SapPriceList
import com.handy.PriceList.SapPriceListItem
import com.handy.Product.IntelisisProduct
import com.handy.Product.P1Product
import com.handy.Product.Product
import com.handy.Product.SapProduct
import com.handy.ProductPriceCustomer.Medicom.Aacuerdo
import com.handy.ProductPriceCustomer.Medicom.Alacuerdo
import com.handy.ProductPriceCustomer.Medicom.SapAacuerdo
import com.handy.ProductPriceCustomer.Medicom.SapAlacuerdo
import com.handy.ProductPriceCustomer.ProductPriceCustomer
import com.handy.ProductPriceCustomer.SapProductPriceCustomer
import com.handy.Zone.P1Zone
import com.handy.Zone.SapZone
import com.handy.Zone.Zone

/**
 * Created by arturo on 8/17/17.
 */
class Util {

    static def subtractLists(def list1, def list2) {
        def equivalence
        Set<Equivalence.Wrapper<Object>> set1 = new HashSet<>();
        Set<Equivalence.Wrapper<Object>> set2 = new HashSet<>();

        if (list2.size() > 0) {
            if (list2?.first()?.instanceOf(SapCustomer) || list2?.first()?.instanceOf(P1Customer) || list2?.first()?.instanceOf(IntelisisCustomer))
                equivalence = new CustomerEquivalence()

            else if (list2?.first()?.instanceOf(SapProduct) || list2?.first()?.instanceOf(P1Product) || list2?.first()?.instanceOf(IntelisisProduct))
                equivalence = new ProductEquivalence()

            else if (list2?.first()?.instanceOf(SapProductPriceCustomer))
                equivalence = new ProductPriceCustomerEquivalence()

            else if (list2?.first()?.instanceOf(SapPriceList) || list2?.first()?.instanceOf(IntelisisPriceList))
                equivalence = new PriceListEquivalence()

            else if (list2?.first()?.instanceOf(SapPriceListItem) || list2?.first()?.instanceOf(IntelisisListItem))
                equivalence = new PriceListItemEquivalence()

            else if (list2?.first()?.instanceOf(SapZone) || list2?.first()?.instanceOf(P1Zone))
                equivalence = new ZoneEquivalence()

            else if (list2?.first()?.instanceOf(SapAacuerdo))
                equivalence = new AacuerdoEquivalence()

            else if (list2?.first()?.instanceOf(SapAlacuerdo))
                equivalence = new AlacuerdoEquivalence()

            list1.each { set1.add(equivalence.wrap(it)) }

            list2.eachWithIndex {
                object2, index ->
                    def object1

                    try {
                        if (list2.first().instanceOf(SapCustomer) || list2.first().instanceOf(P1Customer) || list2.first().instanceOf(IntelisisCustomer)) {
                            object1 = new Customer(object2.properties)
                        } else if (list2.first().instanceOf(SapProduct) || list2.first().instanceOf(P1Product) || list2.first().instanceOf(IntelisisProduct)) {
                            object1 = new Product(object2.properties)
                        } else if (list2.first().instanceOf(SapProductPriceCustomer)) {
                            object1 = new ProductPriceCustomer(object2.properties)
                        } else if (list2.first().instanceOf(SapPriceList) || list2.first().instanceOf(IntelisisPriceList)) {
                            object1 = new PriceList(object2.properties)
                        } else if (list2.first().instanceOf(SapPriceListItem) || list2.first().instanceOf(IntelisisListItem)) {
                            object1 = new PriceListItem(object2.properties)
                        } else if (list2.first().instanceOf(SapZone) || list2.first().instanceOf(P1Zone)) {
                            object1 = new Zone(object2.properties)
                        } else if (list2.first().instanceOf(SapAacuerdo)) {
                            object1 = new Aacuerdo(object2.properties)
                        } else if (list2.first().instanceOf(SapAlacuerdo)) {
                            object1 = new Alacuerdo(object2.properties)
                        }

                        object1.id = object2.id
                        set2.add(equivalence.wrap(object1))
                    } catch (Exception ex) {
                        println "SubtractLists: $object2", ex
                    }
            }

        }

        Sets.difference(set2, set1)
    }

}
