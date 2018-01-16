package com.handy

import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import grails.converters.JSON
import grails.util.Holders
import groovy.json.JsonSlurper

class BusService {

    def amazonWebService
    def customerService
    def salesOrderService

    def receive() {
        try {
            boolean salesOrderReceived
            String queueUrl = Holders.grailsApplication.config.configuration.sqs.url
            List messages = amazonWebService.sqs.receiveMessage(new ReceiveMessageRequest(queueUrl).withMessageAttributeNames("type")).messages
            messages.each { Message message ->

                def jsonSlurper = new JsonSlurper()
                def body = jsonSlurper.parseText(message.body)
                def attributeType = message.messageAttributes.get('type')
                boolean processed

                switch(attributeType.stringValue){
                    case 'new-customer':
                            processed = customerService.getCustomerHandyAPI(Long.parseLong(body.code))
                        break
                    case 'sales-order':
                        processed = salesOrderService.save(body)
                        salesOrderReceived = true
                        break

                }

                if (processed)
                    amazonWebService.sqs.deleteMessage(new DeleteMessageRequest(queueUrl, message.receiptHandle))
            }

        } catch (Exception e) {
            log.error('Error al leer mensajes de AWS SQS', e)
        }
    }
}
