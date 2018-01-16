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
import groovy.sql.Sql
import groovy.time.TimeCategory

class SyncService {

    // TODO Medir performance con un chingo de datos
    //7 minutos en comparar lista de 101,000
    //2 minutos en guardar 101,000 la primera vez
    def customerService
    def productService
    def productPriceCustomerService
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
    def sessionFactory
    def grailsApplication
    def priceListService
    def agreementService
    def dataSource_erp

    def sync(String type) {
        //log.debug("$type SYNC START " + new Date())

        def objects = getObjects(type)

        if (!objects.erpList)
            return

        if (!objects)
            return

        process(objects, type)

        //log.debug("$type SYNC END " + new Date())
    }

    def process(def objects, def type) {
        if (objects.count == 0) {
            log.info("⇄ $type: sincronizando por primera vez. ⇄")
            saveForTheFirstTime(objects.erpList)
        } else {
            def differences = Util.subtractLists(objects.list, objects.erpList)

            if (differences.size() > 0)
                log.info "<> $type: Se encontraron ${differences.size()} diferencias <>"

            int times = 0
            differences.each {
                if (times == 100) {
                    times = 0
                    cleanUpGorm()
                }

                updateObject(type, it)
                times++
            }

        }
    }

    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }

    def getObjects(String type) {
        def erpList
        def list
        def count

        switch (type) {
            case Sync.CUSTOMER:

                if (grailsApplication.config.erp.sap.enabled) {
                    erpList = SapCustomer.list()
                } else if (grailsApplication.config.erp.p1.enabled) {
                    erpList = P1Customer.withCriteria {
                        isNotNull('zoneId')
                        isNotNull('id')
                    }
                } else if (grailsApplication.config.erp.intelisis.enabled) {
                    erpList = IntelisisCustomer.withCriteria {
                        eq 'type', 'Cliente'
                    }
                }

                count = Customer.count
                if (count > 0) list = Customer.list()

                break

            case Sync.PRODUCT:

                if (grailsApplication.config.erp.sap.enabled) {
                    erpList = SapProduct.list()
                } else if (grailsApplication.config.erp.p1.enabled) {
                    erpList = P1Product.withCriteria {
                        isNotNull('id')
                    }
                } else if (grailsApplication.config.erp.intelisis.enabled) {
                    erpList = IntelisisProduct.withCriteria {
                        inList 'group', grailsApplication.config.erp.intelisis.tables.product.list
                    }
                }

                count = Product.count
                if (count > 0) list = Product.list()
                break

            case Sync.PRICE_LIST:
                if (grailsApplication.config.erp.intelisis.enabled) {
                    erpList = IntelisisPriceList.withCriteria {
                        inList 'id', grailsApplication.config.erp.intelisis.tables.priceList.list
                    }
                    count = PriceList.countByIdInList(grailsApplication.config.erp.intelisis.tables.priceList.list)
                } else {
                    erpList = SapPriceList.withCriteria {
                        inList 'id', grailsApplication.config.erp.sap.tables.priceList.list
                    }
                    count = PriceList.countByIdInList(grailsApplication.config.erp.sap.tables.priceList.list)
                }

                if (count > 0) list = PriceList.list()
                break

            case Sync.PRICE_LIST_ITEM:

                if (grailsApplication.config.erp.intelisis.enabled) {

                    erpList = IntelisisListItem.withCriteria {
                        inList 'listCode', grailsApplication.config.erp.intelisis.tables.priceList.list
                    }
                    count = PriceListItem.countByListCodeInList(grailsApplication.config.erp.intelisis.tables.priceList.list)
                    if (count == 0) {
                        log.info "Tabla de PRICE_LIST_ITEM: ${erpList.size()}"
                    }
                } else {

                    erpList = SapPriceListItem.withCriteria {
                        inList 'listId', grailsApplication.config.erp.sap.tables.priceList.list
                    }
                    count = PriceListItem.countByListIdInList(grailsApplication.config.erp.sap.tables.priceList.list)
                }

                if (count > 0) list = PriceListItem.list()
                break

            case Sync.PRODUCT_PRICE_CUSTOMER:

                erpList = SapProductPriceCustomer.withCriteria {
                    isNotNull('customerCode')
                    isNotNull('productCode')
                }

                count = ProductPriceCustomer.count
                if (count > 0) list = ProductPriceCustomer.list()
                break

            case Sync.ZONE:
                if (grailsApplication.config.erp.sap.enabled)
                    erpList = SapZone.list()
                else if (grailsApplication.config.erp.p1.enabled)
                    erpList = P1Zone.list()

                count = Zone.count
                if (count > 0) list = Zone.list()
                break

            case Sync.AACUERDO:
                def sql = new Sql(dataSource_erp)
                def sync = Sync.get(1)
                def aaof = grailsApplication.config.configuration.params.aaof
                def tiAg = (int) grailsApplication.config.configuration.params.tiag

                def date = new Date()
                use(TimeCategory) {
                    date = date.clearTime()
                    date = date - tiAg.days
                }

                def result = sql.rows("SELECT DocEntry as 'docEntry', LogInst as 'logInst', Canceled as 'enabled', U_CodigoC as 'customerCode' from [@AACUERDO] where U_CodigoC is not null and U_CodigoC <> '}' and U_CodigoC <> '+' and UpdateDate >= '${date.format('yyyy-MM-dd HH:mm:ss')}' order by DocEntry, LogInst OFFSET ${sync.aacuerdoOffset} ROWS FETCH NEXT ${aaof} ROWS ONLY")

                if (result.size() == 0)
                    sync.aacuerdoOffset = 0
                else
                    sync.aacuerdoOffset += aaof

                sync.save(failOnError: true);

                erpList = new ArrayList<SapAacuerdo>()
                result.each { row ->
                    erpList.add(new SapAacuerdo(row))
                }

                count = Aacuerdo.count
                println "Sync AACUERDO result         : ${result.size()}"
                println "Sync AACUERDO aacuerdoOffset : ${sync.aacuerdoOffset}"
                println "Sync AACUERDO count          : ${count}"
                if (count > 0) list = Aacuerdo.list()
                break

            case Sync.ALACUERDO:
                def sql = new Sql(dataSource_erp)
                def sync = Sync.get(1)
                def alof = grailsApplication.config.configuration.params.alof
                def tiAg = (int) grailsApplication.config.configuration.params.tiag

                def date = new Date()
                use(TimeCategory) {
                    date = date.clearTime()
                    date = date - tiAg.days
                }

                def result = sql.rows("SELECT aa.DocEntry as 'docEntry', aa.LogInst as 'logInst', al.U_CodArt as 'productCode', al.U_Precio as 'price' from [@AACUERDO] as aa left outer join [@ALACUERDO] as al on al.DocEntry=aa.DocEntry and al.LogInst=aa.LogInst where aa.U_CodigoC is not null and al.U_codArt is not null and al.U_Precio is not null and aa.U_CodigoC <> '}' and aa.UpdateDate >= '${date.format('yyyy-MM-dd HH:mm:ss')}' and aa.U_CodigoC <> '+' order by aa.DocEntry, aa.LogInst, al.U_CodArt OFFSET ${sync.alacuerdoOffset} ROWS FETCH NEXT ${alof} ROWS ONLY")

                if (result.size() == 0)
                    sync.alacuerdoOffset = 0
                else
                    sync.alacuerdoOffset += alof

                sync.save(failOnError: true);

                erpList = new ArrayList<SapAlacuerdo>()
                result.each { row ->
                    if (row.docEntry > 0 && row.logInst > 0 && row.productCode)
                        erpList.add(new SapAlacuerdo(row))
                }

                count = Alacuerdo.count
                println "Sync ALACUERDO result          : ${result.size()}"
                println "Sync ALACUERDO alacuerdoOffset : ${sync.alacuerdoOffset}"
                println "Sync ALACUERDO count           : ${count}"
                if (count > 0) list = Alacuerdo.list()
                break

            default:
                log.info("Tipo de objecto no encontrado: $type")
                return null
        }

        [list: list, count: count, erpList: erpList]
    }

    def updateObject(String type, def object) {
        switch (type) {
            case Sync.CUSTOMER:
                customerService.update(object)
                break
            case Sync.PRODUCT:
                productService.update(object)
                break
            case Sync.PRODUCT_PRICE_CUSTOMER:
                productPriceCustomerService.update(object)
                break
            case Sync.PRICE_LIST:
                priceListService.update(object)
                break
            case Sync.PRICE_LIST_ITEM:
                priceListService.updateItem(object)
                break
            case Sync.ZONE:
                customerService.updateZone(object)
                break
            case Sync.AACUERDO:
                agreementService.updateAacuerdo(object)
                break
            case Sync.ALACUERDO:
                agreementService.updateAlacuerdo(object)
                break
        }
    }

    def saveForTheFirstTime(def list) {
        def each = list.each { erpObject ->
            try {
                def object1

                if (erpObject.instanceOf(SapCustomer) || erpObject.instanceOf(P1Customer) || erpObject.instanceOf(IntelisisCustomer))
                    object1 = new Customer(erpObject.properties)

                else if (erpObject.instanceOf(SapProduct) || erpObject.instanceOf(IntelisisProduct) || erpObject.instanceOf(P1Product))
                    object1 = new Product(erpObject.properties)

                else if (erpObject.instanceOf(SapPriceList) || erpObject.instanceOf(IntelisisPriceList))
                    object1 = new PriceList(erpObject.properties)

                else if (erpObject.instanceOf(SapPriceListItem) || erpObject.instanceOf(IntelisisListItem))
                    object1 = new PriceListItem(erpObject.properties)

                else if (erpObject.instanceOf(SapProductPriceCustomer))
                    object1 = new ProductPriceCustomer(erpObject.properties)

                else if (erpObject.instanceOf(SapZone) || erpObject.instanceOf(P1Zone))
                    object1 = new Zone(erpObject.properties)

                else if (erpObject.instanceOf(SapAacuerdo)) {
                    object1 = new Aacuerdo(erpObject.properties)

                } else if (erpObject.instanceOf(SapAlacuerdo)) {
                    object1 = new Alacuerdo(erpObject.properties)
                }

                object1.id = erpObject.id

                erpObject.discard()

                if (!object1.save(flush: true)) {
                    log.error "Error class  : " + object1.class
                    log.error "Error object1: " + object1.properties
                    object1.errors.each { log.error it }
                } else {
                    if (erpObject.instanceOf(P1Product)) {
                        productService.saveOrUpdatePriceListItem((Product) object1)
                    }
                }
            } catch (Exception e) {

            }

        }
    }

}