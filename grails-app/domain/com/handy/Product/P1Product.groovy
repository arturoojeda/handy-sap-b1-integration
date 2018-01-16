package com.handy.Product

import grails.util.Environment
import grails.util.Holders

import java.math.RoundingMode

class P1Product {

    //Propiedades necesarias
    String id
    String description
    String familyId
    String enabled
    BigDecimal price
    BigDecimal price2
    BigDecimal price3
    BigDecimal price4
    BigDecimal price5
    int erpId
    BigDecimal iva
    BigDecimal costoCalc

    static constraints = {
    }

    static mapping = {
        if(!Environment.DEVELOPMENT)
            cache usage: "read-only"

        if(isP1Enabled() && getTableConfig().enabled){
            datasource 'erp'
            version false
            table getTableConfig().name

            id              column: getTableConfig().column.id.name,             sqlType: getTableConfig().column.id.sqlType
            erpId           column: getTableConfig().column.erpId.name,          sqlType: getTableConfig().column.erpId.sqlType
            description     column: getTableConfig().column.description.name,    sqlType: getTableConfig().column.description.sqlType
            familyId        column: getTableConfig().column.familyId.name,       sqlType: getTableConfig().column.familyId.sqlType
            enabled         column: getTableConfig().column.enabled.name,        sqlType: getTableConfig().column.enabled.sqlType
            price           column: getTableConfig().column.price.name,          sqlType: getTableConfig().column.price.sqlType
            price2          column: getTableConfig().column.price2.name,         sqlType: getTableConfig().column.price2.sqlType
            price3          column: getTableConfig().column.price3.name,         sqlType: getTableConfig().column.price3.sqlType
            price4          column: getTableConfig().column.price4.name,         sqlType: getTableConfig().column.price4.sqlType
            price5          column: getTableConfig().column.price5.name,         sqlType: getTableConfig().column.price5.sqlType
            iva             column: getTableConfig().column.iva.name,            sqlType: getTableConfig().column.iva.sqlType
            costoCalc       column: getTableConfig().column.costoCalc.name,      sqlType: getTableConfig().column.costoCalc.sqlType

        } else{
            datasource 'local'
            id column: 'id', sqlType: 'int', generator: 'assigned'
        }

    }

    transient beforeUpdate = {
        if (!Environment.DEVELOPMENT) {
            throw new RuntimeException('No se permite actualizar')
        }

    }

    transient beforeSave = {
        if (!Environment.DEVELOPMENT) {
            throw new RuntimeException('No se permite actualizar')
        }
    }

    def beforeInsert() {
        if (!Environment.DEVELOPMENT) {
            throw new RuntimeException('No se permite actualizar')
        }
    }

    static def isP1Enabled(){
        return Holders.grailsApplication.config.erp.p1.enabled
    }

    static def getTableConfig() {
        return Holders.grailsApplication.config.erp.p1.tables.product
    }

    def getListPrice(long id){
        switch (id){
            case 1: return price
                break
            case 2: return price2
                break
            case 3: return price3
                break
            case 4: return price4
                break
            case 5: return price5
                break
            default: return price
        }
    }

    def getPriceWithoutTax(long id){

        def iva2 = iva.add(new BigDecimal("1")).setScale(2, RoundingMode.DOWN)

        switch (id) {
            case 1:
                return price.divide(iva2, 4, RoundingMode.DOWN)
                break
            case 2:
                return price2.divide(iva2, 4, RoundingMode.DOWN)
                break
            case 3:
                return price3.divide(iva2, 4, RoundingMode.DOWN)
                break
            case 4:
                return price4.divide(iva2, 4, RoundingMode.DOWN)
                break
            case 5:
                return price5.divide(iva2, 4, RoundingMode.DOWN)
                break
            default: return price.divide(iva2, 4, RoundingMode.DOWN)
        }

    }
}
