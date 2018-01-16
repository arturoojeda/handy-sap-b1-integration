<%--
  Created by IntelliJ IDEA.
  User: cuauhtemoc
  Date: 5/29/16
  Time: 7:31 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <r:require modules="application" />
    <title>Handy Sync</title>

</head>

<body>
    <div class="container">
        <div id="handy" role="banner" style="width:100%;text-align: center"><a href="http://handy.la"><img style="width: 200px;" src="${resource(dir: 'images', file: 'logo.png')}" alt="Handy"/></a></div>
        <h2>Handy Sync - ${company}</h2>
        <br/>
        <div class="panel panel-primary">
            <div class="panel-heading">
                <h3 class="panel-title">Información</h3>
            </div>
            <div class="panel-body">
                <table class="table">

                    <g:if test="${config.salesOrder.receive}">
                        <tr class="success">
                            <th>Pedidos</th>
                            <th></th>
                            %{--<th></th>--}%
                        </tr>
                        <tr>
                            <th>ERP</th>
                            <th>Sincronizador</th>
                            %{--<th>Handy</th>--}%
                        </tr>
                        <tr>
                            <td>${salesOrderCount}</td>
                            <td>${0}</td>
                            %{--<td id="sales-count">0</td>--}%
                        </tr>

                        <tr>
                            <th></th>
                            <th></th>
                            %{--<th></th>--}%
                        </tr>
                    </g:if>

                    <g:if test="${config.customer.send}">
                        <tr class="success">
                            <th>Clientes</th>
                            <th></th>
                            %{--<th></th>--}%
                        </tr>
                        <tr>
                            <th>ERP</th>
                            <th>Sincronizador</th>
                            %{--<th>Handy</th>--}%
                        </tr>
                        <tr>
                            <td>${erpCustomerCount}</td>
                            <td>${customerCount}</td>
                            %{--<td id="customer-count">0</td>--}%
                        </tr>

                        <tr>
                            <th></th>
                            <th></th>
                            %{--<th></th>--}%
                        </tr>
                    </g:if>

                    <g:if test="${config.product.send}">
                        <tr class="success">
                            <th>Productos</th>
                            <th></th>
                            %{--<th></th>--}%
                        </tr>
                        <tr>
                            <th>ERP</th>
                            <th>Sincronizador</th>
                            %{--<th>Handy</th>--}%
                        </tr>
                        <tr>
                            <td>${erpProductCount}</td>
                            <td>${productCount}</td>

                            %{--<td id="product-count">0</td>--}%
                        </tr>

                        <tr>
                            <th></th>
                            <th></th>
                            %{--<th></th>--}%
                        </tr>
                    </g:if>

                    <g:if test="${config.productPriceCustomer.send}">
                        <tr class="success">
                            <th>Precio de Producto por Cliente</th>
                            <th></th>
                            %{--<th></th>--}%
                        </tr>
                        <tr>
                            <th>ERP</th>
                            <th>Sincronizador</th>
                            %{--<th>Handy</th>--}%
                        </tr>
                        <tr>
                            <td>${erpProductPriceCustomerCount}</td>
                            <td>${productPriceCustomerCount}</td>

                            %{--<td id="product-price-customer-count">0</td>--}%
                        </tr>

                        <tr>
                            <th></th>
                            <th></th>
                            %{--<th></th>--}%
                        </tr>
                    </g:if>

            </table>
            </div>
        </div>

       %{-- <div class="panel panel-primary">
            <div class="panel-heading">
                <h3 class="panel-title">Configuraciones</h3>
            </div>
            <div class="panel-body">
                <table class="table table-striped">
                    <tr>
                        <th>Recibir Pedidos</th>
                        <td>${config.salesOrder.receive ? 'SI' : 'NO'}</td>
                    </tr>
                    <tr>
                        <th>Enviar Clientes</th>
                        <td>${config.customer.send ? 'SI' : 'NO'}</td>
                    </tr>
                    <tr>
                        <th>Enviar Productos</th>
                        <td>${config.product.send ? 'SI' : 'NO'}</td>
                    </tr>
                    <tr>
                        <th>Enviar Precio de Producto por Cliente</th>
                        <td>${config.productPriceCustomer.send ? 'SI' : 'NO'}</td>
                    </tr>
                </table>
            </div>
        </div>--}%

        <br/>
    </div>
</body>
</html>