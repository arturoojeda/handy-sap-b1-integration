package com.handy

import grails.util.Holders

class BusJob {

    def concurrent = false
    def grailsApplication
    def busService

    static triggers = {
        simple repeatInterval: Holders.grailsApplication.config.configuration.params.tb
    }

    def execute() {
        if(Holders.grailsApplication.config.configuration.sqs.enabled)
            busService.receive()
    }
}
