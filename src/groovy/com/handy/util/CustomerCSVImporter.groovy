package com.handy.util

import com.handy.Customer.SapCustomer

class CustomerCSVImporter {

    def sessionFactory
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

    File file

    public CustomerCSVImporter(File file) {
        this.file = file
    }

    public def save(){
        def results = []
        int line = 1, index = 0

        file.eachCsvLine {
            try {
                results << importRow(it, line)

                if (index >= 100){
                    cleanUpGorm()
                    index = 0
                }

                index++
                line++
            } catch (Exception ex){
                results.push(error: true, message: ex.message, line: line, asset: null)
            }
        }

        results
    }

    private def importRow(tokens, i){
        def result = [:]

        def data = [enabled: true]
        data.code = tokens[0].toString().trim()
        data.description = tokens[1].toString().trim()
        data.address = tokens[2].toString().trim()
        data.city = tokens[3].toString().trim()
        data.postalCode = tokens[4].toString().trim()
        data.zone = tokens[5].toString().trim()
        data.latitude = tokens[6].toString().trim()
        data.longitude = tokens[7].toString().trim()
        data.owner = tokens[8].toString().trim()
        data.phoneNumber = tokens[9].toString().trim()
        data.email = tokens[10].toString().trim()
        data.comments = tokens[11].toString().trim()

        def customer = SapCustomer.findOrCreateByCode data.code
        customer.properties = data

        boolean error = customer.save() == null

        result.put 'customer', customer
        result.put 'error', error
        result.put 'line', i
        if (error) result.put 'message', customer.errors.toString()

        result
    }

    private cleanUpGorm(){
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }

}
