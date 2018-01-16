package com.handy

import com.handy.SalesOrder.SalesOrder
import com.sap.smb.sbo.api.Documents
import com.sap.smb.sbo.api.SBOCOMConstants
import com.sap.smb.sbo.api.SBOCOMUtil

class SapService {

    def grailsApplication
    def dbErp

    def connect() {
        try {

            log.info("Se intenta conectar a Sap")

            def company = getCompany()
            company.connect()
            return company

        } catch (Exception e) {
            log.error "Exception al conectar a Sap", e
        }
    }

    def disconnect(def company) {
        try {

            log.info("Se intenta desconectar de Sap")

            company.disconnect()

        } catch (Exception e) {
            log.error "Exception al desconectar de Sap", e
        }
    }

    def checkIfSalesOrderExist(SalesOrder salesOrder){
        try{
            String query = "SELECT DocEntry, DocNum FROM OQUT WHERE CardCode = '$salesOrder.customerCode' AND Comments LIKE '%$salesOrder.id%'"
            def rows = dbErp.rows query

            if(rows.isEmpty())
                return null
            else
                rows.first()

        }catch (Exception e){
            log.error("Error al checar en BD de SAP si existe el pedido: $salesOrder.id", e)
            return null
        }
    }

    def saveSalesOrder(SalesOrder salesOrder, def company) {

        def error = false
        def message = ""

        try {

            log.info("Se intenta guardar el pedido en SAP. ID: $salesOrder.id  Cliente: $salesOrder.customerCode")


            def row = null

            if(salesOrder.status == 2){
                row = checkIfSalesOrderExist(salesOrder)
            }

            if(row){

                salesOrder.documentId = row.DocEntry
                salesOrder.documentNum = row.DocNum
                salesOrder.status = 0
                log.info("El pedido estaba en Deadlock y sí se guardó. HANDY ID: $salesOrder.id SAP: $row")

            } else{

                if (company.isConnected()) {

                    def quotation = new Documents(company.getBusinessObject(SBOCOMConstants.BoObjectTypes_Document_oQuotations))
                    quotation.setCardCode(salesOrder.customerCode)
                    quotation.setHandWritten(SBOCOMConstants.BoYesNoEnum_tNO)
                    quotation.setComments(salesOrder.finalComment)

                    if (salesOrder.scheduledDateForDelivery)
                        quotation.setDocDueDate(salesOrder.scheduledDateForDelivery)

                    if (grailsApplication.config.configuration.salesOrder.quotationSelectExchange)
                        quotation.setDocCurrency(grailsApplication.config.handy.currency)


                    log.info("Cantidad de items:  ${salesOrder.items.size()} - ${salesOrder.id}")

                    salesOrder.items.each { item ->
                        quotation.getLines().setItemCode(item.productCode)
                        quotation.getLines().setQuantity(item.quantity)

                        if (grailsApplication.config.configuration.salesOrder.takeHandyPrice) {
                            quotation.getLines().setUnitPrice(item.price)

                            if (grailsApplication.config.configuration.salesOrder.selectExchange)
                                quotation.getLines().setCurrency(grailsApplication.config.handy.currency)u
                        }

                        quotation.getLines().add()
                    }



                    if (quotation.add() != 0) {
                        log.info("Failed to create sales quote ${salesOrder.customerCode} ${salesOrder.id}")

                        if(company?.getLastError()?.getErrorMessage()?.toString()?.contains("Deadlock"))
                            salesOrder.status = 2 //Este estatus es para cuando cae en Deadlock
                        else
                            salesOrder.status = 1
                    }

                    if (salesOrder.status != 0) {
                        def errMsg = company.getLastError();
                        log.error("Create sales quote failed for ( ${salesOrder.id} ):\nError Code: ${errMsg.getErrorCode()}\nError Message: ${errMsg.getErrorMessage()}\n${salesOrder.toString()}");
                        message = errMsg.getErrorMessage()
                    } else {
                        if (grailsApplication.config.configuration.salesOrder.update)
                            quotation.update()

                        salesOrder.documentId = quotation.docEntry
                        salesOrder.documentNum = quotation.docNum

                        if (salesOrder.documentId != 0 || salesOrder.documentNum != 0)
                            log.info("HANDY ID: $salesOrder.id docEntry: $quotation.docEntry docNum: $quotation.docNum")
                    }

                    log.info("Estatus de pedido: ${salesOrder.status == 0 ? 'Guardado' : 'No Guardado'}   ID: $salesOrder.id  Cliente: $salesOrder.customerCode")

                } else {
                    log.info("Error. No se conectó con SAP: $company.lastErrorDescription")
                    salesOrder.status = 2
                }

            }

        } catch (Exception e) {
            salesOrder.status = 1
            error = true
            message = e.message

            log.error "Exception al guardar Pedido $salesOrder.id", e
        }

        [salesOrder:salesOrder, error:error, message:message]
    }

    def getCompany() {

        def company = SBOCOMUtil.newCompany()
        def config = grailsApplication.config.erp.sap
        company.licenseServer = config.company.licenseServer
        company.server = config.company.server
        company.companyDB = config.company.companyDB
        company.userName = config.company.userName
        company.password = config.company.password
        company.dbUserName = config.company.dbUserName
        company.dbPassword = config.company.dbPassword
        company.dbServerType = new Integer(config.company.dbServerType)
        company.language = SBOCOMConstants.BoSuppLangs_ln_English

        company
    }
}
