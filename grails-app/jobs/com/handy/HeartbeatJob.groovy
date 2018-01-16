package com.handy

import grails.util.Holders

class HeartbeatJob {

    // TODO tomar versión de subversion
    // TODO script para auto-actualizar app

    def grailsApplication

    static triggers = {
        cron name: 'heartbeatTrigger', cronExpression: Holders.grailsApplication.config.configuration.params.thb
    }

    def execute() {
        def version = grailsApplication.metadata['app.version']
        log.info " ⇄ ♥ ~ Heartbeat ~ ♥. App version: $version ⇄"
    }
}
