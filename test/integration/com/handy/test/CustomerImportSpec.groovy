package com.handy.test

import com.handy.Customer.Customer
import com.handy.util.CustomerCSVImporter
import grails.plugin.spock.IntegrationSpec

class CustomerImportSpec extends IntegrationSpec {

    def grailsApplication

    def setup() {}

    def cleanup() {}

    void "Import customers"() {
        when:
        def customerFiles = grailsApplication.mainContext.getResource('/data/customer-data-1.csv').file
        def importer = new CustomerCSVImporter(customerFiles)
        importer.save()

        then:
        Customer.count() == 61174
    }
}