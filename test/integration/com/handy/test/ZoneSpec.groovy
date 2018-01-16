package com.handy.test

import com.handy.Customer.Customer
import com.handy.Util
import com.handy.Zone.SapZone
import com.handy.Zone.Zone
import grails.plugin.spock.IntegrationSpec

class ZoneSpec extends IntegrationSpec {

    def customerService

    def setup() {
        def customer = new Customer(code: 'C001', status: 1, zoneId: 2)
        customer.id = 1
        customer.save(failOnError: true)
    }

    def cleanup() {
    }

    void "When SAP has one new zone, to calculate the set with the difference properly"() {
        given:
            def localList = [new Zone(id: 1, name: 'Zona 1')]
            def erpList = [
                    new SapZone(id: 1, name: 'Zona 1'),
                    new SapZone(id: 2, name: 'Zona 2')
            ]
        when:
            def result = Util.subtractLists localList, erpList
        then:
            result.size() == 1
            result.first().reference.name == 'Zona 2'
    }

    void "When SAP has two new zones, to calculate the set with the difference properly"() {
        given:
            def localList = [new Zone(id: 1, name: 'Zona 1')]
            def erpList = [
                new SapZone(id: 1, name: 'Zona 1'),
                new SapZone(id: 2, name: 'Zona 2'),
                new SapZone(id: 3, name: 'Zona 3')
            ]
        when:
            def result = Util.subtractLists localList, erpList
        then:
            result.size() == 2
            result.first().reference.name == 'Zona 3'
            result.last().reference.name == 'Zona 2'
    }

    void "When SAP has one new zone, check if it is created properly in the sync DB, without a zone with the same id existing, and the customer updated"() {
        given:
            def local_firstZone = new Zone(name: 'Zona 1')
            local_firstZone.id = 1
            local_firstZone.save(failOnError: true)

            def localList = Zone.list()

            def sap_firstZone = new SapZone(name: 'Zona 1')
            sap_firstZone.id = 1

            def sap_secondZone = new SapZone(name: 'Zona 2')
            sap_secondZone.id = 2

            def erpList = [sap_firstZone, sap_secondZone]
        when:
            def result = Util.subtractLists localList, erpList
            customerService.updateZone(result.first())
        then:
            Zone.count() == 2
            Zone.get(2) != null
            Zone.get(2)?.name == 'Zona 2'
            Customer.withNewSession {
                /*
                a new session is required because customers are updates through a HQL
                query (executeUpdate) so if the finder is executed under the same session
                it will returned a cached version of it without the status change
                 */
                Customer.findByCode('C001').status == 0
            }
    }


}