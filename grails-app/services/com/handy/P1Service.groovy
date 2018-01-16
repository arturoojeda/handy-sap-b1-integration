package com.handy

import com.handy.Customer.P1Customer
import com.handy.Product.P1Product
import com.handy.SalesOrder.SalesOrder
import groovy.sql.Sql

import java.math.RoundingMode

class P1Service {

    def dataSource_erp

    def saveSalesOrder(SalesOrder salesOrder) {

        def date = salesOrder.mobileDateCreated.format('yyyy-dd-MM')
        def time = salesOrder.mobileDateCreated.format('hh:mm:ss')
        def sql = new Sql(dataSource_erp)
        def body = []
        def importeConIVA = new BigDecimal("0")
        def importeSinIVA = new BigDecimal("0")
        def tIva = new BigDecimal("0")
        def tCosto = new BigDecimal("0")
        def customer = P1Customer.get(salesOrder.customerCode)

        salesOrder.items.each {

            def product = P1Product.get(it.productCode)

            if (product != null) {

                try {
                    def listaPreciosDet
                    def IDProducto = product.erpId
                    def TipoPrecio = customer.priceList
                    def DescPorc = new BigDecimal(0)
                    def IVA = product.iva.setScale(2, RoundingMode.DOWN)
                    def IVA2 = IVA.add(new BigDecimal("1")).setScale(2, RoundingMode.DOWN)
                    def PrecioU = it.price.divide(IVA2, 4, RoundingMode.DOWN)

                    if (customer.listaPrecios > 1) {
                        listaPreciosDet = sql.firstRow("Select * from TbaListaPreciosDet where IDListaPrecios = ${customer.listaPrecios} and IDProducto = $IDProducto")
                    }

                    if (listaPreciosDet != null) {

                        TipoPrecio = listaPreciosDet.get('TipoPrecio')
                        PrecioU = product.getPriceWithoutTax(TipoPrecio)
                        def countDecimal = listaPreciosDet.get('DescPorc')
                        DescPorc = countDecimal.toBigDecimal().setScale(4, RoundingMode.DOWN)

                    }

                    def Cantidad = it.quantity
                    def DescImp = (PrecioU * DescPorc).setScale(4, RoundingMode.DOWN)
                    def Importe = (Cantidad * PrecioU) - (DescImp * Cantidad)
                    def CostoCalc = product.costoCalc.setScale(4, RoundingMode.DOWN)
                    def ImporteNeto = (Importe * IVA2).setScale(4, RoundingMode.DOWN)
                    def PUNeto = (PrecioU * IVA2).setScale(4, RoundingMode.DOWN)
                    def IVAImporte = (Importe * IVA).setScale(4, RoundingMode.DOWN)

                    if (IVA > 0) {
                        importeConIVA = importeConIVA.add(Importe)
                        tIva = tIva.add(IVAImporte)
                    } else
                        importeSinIVA = importeSinIVA.add(Importe)

                    tCosto = tCosto.add(CostoCalc)

                    body.add("INSERT INTO TbaVentaDet ( IDVenta, IDProducto, Cantidad, PrecioU, DescPorc, DescImp, Importe, CostoCalc, TipoPrecio, IVA, ImporteNeto, PUNeto, IVAImporte)" +
                            " VALUES                  (@IDVenta,$IDProducto,$Cantidad,$PrecioU,$DescPorc,$DescImp,$Importe,$CostoCalc,$TipoPrecio,$IVA,$ImporteNeto,$PUNeto,$IVAImporte)")

                } catch (Exception ex) {
                    log.error("Create sales quote failed for ( ${salesOrder.id} ):\nError Message: ${ex}\n")
                }
            }
        }

        try {
            sql.withTransaction {

                def docNum = Integer.parseInt(sql.firstRow("SELECT TOP 1 NoDoc FROM TbaVenta WHERE TipoDoc = 'R' ORDER BY IDVenta DESC").NoDoc) + 1

                def IDCliente = sql.firstRow "SELECT IDCliente FROM TbaCliente WHERE Codigo = '${salesOrder.customerCode}'"
                def IDVenta = sql.executeInsert "INSERT INTO TbaVenta (Fecha,TipoDoc,NoDoc,IDCliente,IDDiasCredito,IDAlmacen,CondPago,FechaPago,Anticipo,FormaPago,ImporteConIVA,ImporteSinIVA,DescPorc,DescImporte,SubTotal,TIVA,TImpuesto2,TImpuesto3,TImpuesto4,TImpuesto5,Total,Saldo,Efectivo,MonedaImporte,Afecto,Imprimio,ImpresionFecha,TImpuesto6,TImpuesto7,TImpuesto8,TImpuesto9,TImpuesto10,EnviarNombre,EnviarDom,EnviarPob,EnviarNotas,EnviarTel,TCosto,Estatus,ImpresionHora,Modificado,Cancelado,ImporteExento)" +
                        " VALUES (convert(datetime,'$date 00:00:00'),'R','${docNum}',${IDCliente.IDCliente},0,1,'C',convert(datetime,'$date 00:00:00'),0,'E',${importeConIVA.setScale(4, RoundingMode.CEILING)},${importeSinIVA.setScale(4, RoundingMode.CEILING)},0,0,${importeConIVA.add(importeSinIVA).setScale(4, RoundingMode.CEILING)},${tIva.setScale(4, RoundingMode.CEILING)},0,0,0,0,${importeConIVA.add(importeSinIVA).add(tIva).setScale(4, RoundingMode.CEILING)},0,0,0,0,0,convert(datetime,'$date $time'),0,0,0,0,0,'','','','','',${tCosto.setScale(4, RoundingMode.CEILING)},'N',convert(datetime,'$date $time'),-1,0,${importeSinIVA.setScale(4, RoundingMode.CEILING)})"

                body.each {
                    sql.execute(it.replace("@IDVenta", "${IDVenta[0].get(0)}"))
                }

                log.info("HANDY ID: $salesOrder.id docNum: $docNum")

            }

            sql.close()

        } catch (Exception ex) {

            salesOrder.status = 1
            log.info("Failed to create sales quote ${salesOrder.customerCode} ${salesOrder.id}")
            log.error("Create sales quote failed for ( ${salesOrder.id} ):\nError Message: ${ex}\n")

        }

        salesOrder

    }
}
