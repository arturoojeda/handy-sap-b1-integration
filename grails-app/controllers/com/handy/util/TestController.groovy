package com.handy.util

import com.handy.Customer.Customer
import com.handy.Customer.SapCustomer
import com.handy.SalesOrder.SalesOrder
import grails.converters.JSON
import com.sap.smb.sbo.api.*

class TestController {

    def grailsApplication
    def testService
    def salesOrderService

    def index() {
        //render([external_file: System.getenv().get("HandySyncEngineConfiguration").toString(), home: System.getenv().get("HOME").toString(), customers: SapCustomer.list()] as JSON)
        render([customers: Customer.list().size(), erpCustomers: SapCustomer.list()] as JSON)
    }

    def saveSalesOrderInERP(){
        def result = salesOrderService.send()
        render "SAVED: $result"
    }

    def statusZero(){

        SalesOrder.list().each {
            it.status = 0
            it.save()
        }
        render 'OK'
    }

    def testInitialImport(){
        def customerFiles = grailsApplication.mainContext.getResource('/data/customer-data-1.csv').file
        def importer = new CustomerCSVImporter(customerFiles)
        importer.save()
    }

    def createCustomers(){
        testService.createCustomers()
render 'OK'
    }

    def createProduct(){
        testService.createProducts()
        render 'OK'
    }

    def getCompany(){

        def company = SBOCOMUtil.newCompany()
        def config = grailsApplication.config.erp
        company.licenseServer = config.system.api.license.server
        company.server = config.system.api.database.server
        company.companyDB = config.system.api.company.database
        company.userName = config.system.api.user.name
        company.password = config.system.api.user.password
        company.dbUserName = config.system.api.database.username
        company.dbPassword = config.system.api.database.password
        company.dbServerType = SBOCOMConstants.BoDataServerTypes_dst_MSSQL2012
        company.language = SBOCOMConstants.BoSuppLangs_ln_English
        company
    }


    def createSalesOrder(SalesOrder salesOrder) {

        int agentCode = 1

        log.debug("try create sales quote for ( ${salesOrder.customerCode} ${salesOrder.id} )")

        def company = getCompany()
        company.connect()

        if (company.isConnected()) {
            def quotation = new Documents(company.getBusinessObject(SBOCOMConstants.BoObjectTypes_Document_oQuotations))
            quotation.setCardCode(salesOrder.customerCode)
            quotation.setHandWritten(SBOCOMConstants.BoYesNoEnum_tNO)
            quotation.setComments("Handy ID ${salesOrder.id}. Tipo: ${salesOrder.type}")

            log.debug("try add lines for " + salesOrder.customerCode)
            salesOrder.items.each { item ->
                quotation.getLines().setItemCode(item.productCode)
                quotation.getLines().setQuantity(item.quantity)
                quotation.getLines().setUnitPrice(item.price)
                /*if (salesOrder.currencyCode != null) {
                    quotation.getLines().setCurrency(salesOrder.currencyCode)
                }*/
                quotation.getLines().add()
            }

            log.debug("add sales quote status")
            if (quotation.add() != 0) {
                log.debug("Failed to create sales quote ${salesOrder.customerCode} ${salesOrder.id}")
                salesOrder.status = 1
            }

            if (salesOrder.status == 0) {
                //log.debug("try add sales agent for " + salesOrder.customerCode)
                //def agentCode = parseInt(salesOrder.agentCode)
                // quotation.setSalesPersonCode(agentCode)

                try {
                    def sql = "UPDATE OQUT SET SlpCode=${agentCode} WHERE DocEntry=${quotation.getDocEntry()} AND DocNum=${quotation.getDocNum()}"
                    def records = new Recordset(company.getBusinessObject(SBOCOMConstants.BoObjectTypes_BoRecordset))
                    records.doQuery(sql)
                } catch (Exception x) {
                    log.error("Failed to update sales quote ${salesOrder.id} ${agentCode}", e)
                    x.printStackTrace()
                    salesOrder.status += 1
                }
            }


            if (salesOrder.status != 0) {
                log.debug("try get error  for " + salesOrder.id)
                def errMsg = company.getLastError();
                log.debug("Create sales quote failed for ( ${salesOrder.id} ):\nError Code: ${errMsg.getErrorCode()}\nError Message: ${errMsg.getErrorMessage()}\n${salesOrder.toString()}");
            } else {
                log.debug("try add quote id for " + salesOrder.id)
                salesOrder.documentId = quotation.docEntry.toString() + ":" + quotation.docNum.toString()
            }

            log.debug("sales quote status ( ${salesOrder.status} ) ( ${salesOrder.documentId} ${quotation.docEntry.toString()} ${quotation.docNum.toString()} ) ( ${salesOrder.id} ${salesOrder.customerCode} )")

        }
        company.disconnect()
        salesOrder
    }

    def getErpSalesOrder(long id){
        def company = getCompany()

        def obj = new Documents(company.getBusinessObject(SBOCOMConstants.BoObjectTypes_Document_oQuotations))
        obj.getByKey(id)

        company.disconnect()

        obj.asXML
    }

    def getErpProduct(long id){
        def company = getCompany()
        def sbo = SBOCOMUtil.getSBObob(company)
        if(id != 0) {
            def obj = new Items(company.getBusinessObject(SBOCOMConstants.BoObjectTypes_oItems))
            obj.getByKey(id)
            company.disconnect()
            obj.asXML
        } else {
            company.disconnect()
            sbo.itemList.asXML
        }

    }

    def query(String sql){
        def company = getCompany()
        def records = new Recordset(company.getBusinessObject(SBOCOMConstants.BoObjectTypes_BoRecordset))
        records.doQuery(sql)

        company.disconnect()
        records.asXML
    }

    def schema(int type){
        def company = getCompany()
        def obj = company.getBusinessObjectXmlSchema(type)
        company.disconnect()
        return obj
    }



}
