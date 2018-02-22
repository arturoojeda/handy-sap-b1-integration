handy {
    username = ''
    password = ''
    logentriesToken = ''
    company = ''
    currency = 'MXN'
}

aws.sqs = ''

dataSource_erp {
    username = 'sa'
    password = 'Qu@ntum'

    // SQL Server
    dialect = org.hibernate.dialect.SQLServerDialect
    driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
    url = "jdbc:sqlserver://IP_O_HOSTNAME:1433;databaseName=NOMBRE_DE_BD"

}

dataSource_local {
    //url = "jdbc:derby:C:\\Handy\\handy-sync-engine-derby-db;create=true;territory=es_MX;collation=TERRITORY_BASED"
    url = "jdbc:derby:/handy/handy-sync-engine-derby-db;create=true;territory=es_MX;collation=TERRITORY_BASED"
}


configuration {

    salesOrder {
        receive = true
        takeHandyPrice = false
        invoiceNotification = true
        quotationSelectExchange = false
        selectExchange = false
        update = false
    }

    customer {
        send = true
        alwaysActive = false
    }
    product {
        send = true
    }
    priceList {
        send = false
        sendFromProduct = true
    }
    productPriceCustomer {
        send = false
        agreements = false
    }

    sqs{
        enabled = true
        url = ''
    }

    params {
        tb = 60000l
        tp = '0 0 0 ? * * *'
        tso = 60000l
        ths = 60000l
        tcp = '0 0/5 * 1/1 * ? *'
        thb = '0 0/10 * 1/1 * ? *'
        maxC = 5
        maxP = 15
        maxPC = 15
        maxPL = 15
        maxPLI = 15
        maxA = 15
        maxAL = 15
    }
}

erp {

    sap {
        enabled = true
        company {
            licenseServer = 'HOSTNAME:30000'
            server = 'HOSTNAME'
            companyDB = 'SAPBO_DB_NAME'
            userName = ''
            password = ''
            dbUserName = ''
            dbPassword = ''

            //dbServerType =  1 //MSSQL
            //dbServerType =  2 //DB_2
            //dbServerType = 3 //SYBASE
            //dbServerType = 4 //MSSQL2005
            //dbServerType = 5 //MAXDB
            //dbServerType = 6 //MSSQL2008
            dbServerType = 7 //MSSQL2012
            //dbServerType = 8 //MSSQL2014
            //dbServerType = 9 //HANADB
        }

        tables {
            customer {
                enabled = true
                name = 'OCRD'
                column {
                    id {
                        name = 'CardCode'
                        sqlType = 'nvarchar(15)'
                    }
                    description {
                        name = 'CardName'
                        sqlType = 'nvarchar(100)'
                    }
                    zone {
                        name = 'Block'
                        sqlType = 'nvarchar(100)'
                    }
                    enabled {
                        name = 'U_HandyCustomerStatus'
                        sqlType = 'nvarchar(20)'
                    }
                    address {
                        name = 'Address'
                        sqlType = 'nvarchar(100)'
                    }
                    city {
                        name = 'City'
                        sqlType = 'nvarchar(100)'
                    }
                    postalCode {
                        name = 'ZipCode'
                        sqlType = 'nvarchar(20)'
                    }
                    owner {
                        name = 'CntctPrsn'
                        sqlType = 'nvarchar(90)'
                    }
                    phoneNumber {
                        name = 'Phone1'
                        sqlType = 'nvarchar(20)'
                    }
                    email {
                        name = 'E_Mail'
                        sqlType = 'nvarchar(100)'
                    }
                    comments {
                        name = 'MailAddres'
                        sqlType = 'nvarchar(100)'
                    }
                    salesPersonCode {
                        name = 'SlpCode'
                        sqlType = 'int(10)'
                    }
                    priceList {
                        name = 'ListNum'
                        sqlType = 'smallint(5)'
                    }
                    balance {
                        name = 'Balance'
                        sqlType = 'numeric(19)'
                    }
                    credit {
                        name = 'CreditLine'
                        sqlType = 'numeric(19)'
                    }
                }
            }

            salesPerson {
                enabled = true
                name = 'OSLP'
                column {
                    id {
                        name = 'SlpCode'
                        sqlType = 'int(10)'
                    }
                    name {
                        name = 'SlpName'
                        sqlType = 'nvarchar(155)'
                    }
                }
            }

            invoice {
                enabled = true
                name = 'OINV'
                column {
                    id {
                        name = 'DocEntry'
                        sqlType = 'int(10)'
                    }
                    docNum {
                        name = 'DocNum'
                        sqlType = 'int(10)'
                    }
                    dateCreated {
                        name = 'DocDate'
                        sqlType = 'datetime'
                    }
                    customerCode {
                        name = 'CardCode'
                        sqlType = 'nvarchar(15)'
                    }
                    customerDescription {
                        name = 'CardName'
                        sqlType = 'nvarchar(100)'
                    }
                    comment {
                        name = 'Comments'
                        sqlType = 'nvarchar(254)'
                    }
                    total {
                        name = 'DocTotal'
                        sqlType = 'numeric(19)'
                    }
                    docTime {
                        name = 'DocTime'
                        sqlType = 'smallint'
                    }                     
                }
            }

            product {
                enabled = true
                name = 'OITM'
                column {
                    id {
                        name = 'ItemCode'
                        sqlType = 'nvarchar(20)'
                    }
                    description {
                        name = 'ItemName'
                        sqlType = 'nvarchar(100)'
                    }
                    family {
                        name = 'U_HandyProductCategory'
                        sqlType = 'nvarchar(30)'
                    }
                    enabled {
                        name = 'validFor'
                        sqlType = 'char(1)'
                    }
                    barcode {
                        name = 'CodeBars'
                        sqlType = 'nvarchar(16)'
                    }
                }
            }

            priceList {
                enabled = true
                list = [1l]
                primaryList = 1l //El id debe estar dentro de list (el arreglo anterior), si es número debe ser long

                name = 'OPLN'
                column {
                    id {
                        name = 'ListNum'
                        sqlType = 'smallint(5)'
                    }
                    name {
                        name = 'ListName'
                        sqlType = 'nvarchar(32)'
                    }
                }

                priceListItem {
                    enabled = true
                    name = 'ITM1'
                    productCode {
                        name = 'ItemCode'
                        sqlType = 'nvarchar(20)'
                    }
                    price {
                        name = 'Price'
                        sqlType = 'numeric(19)'
                    }
                    listId {
                        name = 'PriceList'
                        sqlType = 'smallint(5)'
                    }
                }

            }

            productPriceCustomer {
                enabled = false
                name = 'OSPP'
                column {
                    productCode {
                        name = 'ItemCode'
                        sqlType = 'nvarchar(20)'
                    }
                    customerCode {
                        name = 'CardCode'
                        sqlType = 'nvarchar(15)'
                    }
                    price {
                        name = 'Price'
                        sqlType = 'numeric(19)'
                    }
                }
            }

            aacuerdo {
                enabled = true
                name = '@AACUERDO'
                column {
                    docEntry {
                        name = 'DocEntry'
                        sqlType = 'int(10)'
                    }
                    logInst {
                        name = 'LogInst'
                        sqlType = 'int(10)'
                    }
                    customerCode {
                        name = 'U_CodigoC'
                        sqlType = 'nvarchar(20)'
                    }
                    enabled {
                        name = 'Canceled'
                        sqlType = 'char(1)'
                    }
                }
            }

            alacuerdo {
                enabled = true
                name = '@ALACUERDO'
                column {
                    docEntry {
                        name = 'DocEntry'
                        sqlType = 'int(10)'
                    }
                    logInst {
                        name = 'LogInst'
                        sqlType = 'int(10)'
                    }
                    productCode {
                        name = 'U_CodArt'
                        sqlType = 'nvarchar(20)'
                    }
                    price {
                        name = 'U_Precio'
                        sqlType = 'numeric(19)'
                    }
                }
            }

        }
    }

    p1 {
        enabled = false
        company {
            warehouse = 1
        }
        tables {
            customer {
                enabled = true
                name = 'TbaCliente'
                column {
                    id {
                        name = 'Codigo'
                        sqlType = 'varchar(20)'
                    }
                    description {
                        name = 'Nombre'
                        sqlType = 'varchar(255)'
                    }
                    enabled {
                        name = 'Activo'
                        sqlType = 'smallint(5)'
                    }
                    zoneId {
                        name = 'IDRuta'
                        sqlType = 'int(10)'
                    }
                    address {
                        name = 'Domicilio'
                        sqlType = 'varchar(255)'
                    }
                    address2 {
                        name = 'Colonia'
                        sqlType = 'varchar(100)'
                    }
                    city {
                        name = 'Poblacion'
                        sqlType = 'varchar(100)'
                    }
                    postalCode {
                        name = 'CP'
                        sqlType = 'nvarchar(20)'
                    }
                    owner {
                        name = 'Contacto'
                        sqlType = 'varchar(50)'
                    }
                    phoneNumber {
                        name = 'Telefono'
                        sqlType = 'varchar(50)'
                    }
                    email {
                        name = 'EMail'
                        sqlType = 'varchar(100)'
                    }
                    comments {
                        name = 'Observaciones'
                        sqlType = 'varchar(255)'
                    }
                    priceList {
                        name = 'TipoPrecio'
                        sqlType = 'smallint(5)'
                    }
                    balance {
                        name = 'CreditoLimite'
                        sqlType = 'float(53)'
                    }
                    credit {
                        name = 'CreditoSaldo'
                        sqlType = 'float(53)'
                    }
                    listaPrecios {
                        name = 'IDListaPrecios'
                        sqlType = 'int(10)'
                    }
                    erpId {
                        name = 'IDCliente'
                        sqlType = 'int(10)'
                    }
                }
            }

            zone {
                enabled = true
                name = 'TbaRuta'
                column {
                    id {
                        name = 'IDRuta'
                        sqlType = 'int(10)'
                    }
                    name {
                        name = 'Descripcion'
                        sqlType = 'varchar(50)'
                    }
                }
            }

            product {
                enabled = true
                name = 'TbaProducto'
                column {
                    id {
                        name = 'Codigo'
                        sqlType = 'varchar(20)'
                    }
                    erpId {
                        name = 'IDProducto'
                        sqlType = 'int(10)'
                    }
                    description {
                        name = 'Descripcion'
                        sqlType = 'varchar(200)'
                    }
                    familyId {
                        name = 'IDGrupo'
                        sqlType = 'int(10)'
                    }
                    enabled {
                        name = 'Activo'
                        sqlType = 'smallint(5)'
                    }
                    price {
                        name = 'Precio1'
                        sqlType = 'money(19)'
                    }
                    price2 {
                        name = 'Precio2'
                        sqlType = 'money(19)'
                    }
                    price3 {
                        name = 'Precio3'
                        sqlType = 'money(19)'
                    }
                    price4 {
                        name = 'Precio4'
                        sqlType = 'money(19)'
                    }
                    price5 {
                        name = 'Precio5'
                        sqlType = 'money(19)'
                    }
                    iva {
                        name = 'IVA'
                        sqlType = 'money(19)'
                    }
                    costoCalc {
                        name = 'CostoCalc'
                        sqlType = 'money(19)'
                    }
                }
            }

            family {
                enabled = true
                name = 'TbaGrupo'
                column {
                    id {
                        name = 'IDGrupo'
                        sqlType = 'int(10)'
                    }
                    name {
                        name = 'Description'
                        sqlType = 'varchar(50)'
                    }
                }
            }

            inventory {
                enabled = true
                name = 'TbaExistencia'
                column {
                    id {
                        name = 'IDExistencia'
                        sqlType = 'int(10)'
                    }
                    productId {
                        name = 'IDProducto'
                        sqlType = 'int(10)'
                    }
                    warehouseId {
                        name = 'IDAlmacen'
                        sqlType = 'int(10)'
                    }
                    quantity {
                        name = 'Existencia'
                        sqlType = 'float(53)'
                    }
                }
            }

        }
    }

    intelisis {
        enabled = false
        tables {
            customer {
                enabled = true
                name = 'CTE'
                column {
                    id {
                        name = 'Cliente'
                        sqlType = 'varchar(10)'
                    }
                    description {
                        name = 'Nombre'
                        sqlType = 'varchar(100)'
                    }
                    enabled {
                        name = 'Estatus'
                        sqlType = 'varchar(15)'
                    }
                    zone {
                        name = 'Agente'
                        sqlType = 'varchar(10)'
                    }
                    address {
                        name = 'Direccion'
                        sqlType = 'varchar(100)'
                    }
                    address2 {
                        name = 'DireccionNumero'
                        sqlType = 'varchar(20)'
                    }
                    city {
                        name = 'Poblacion'
                        sqlType = 'varchar(100)'
                    }
                    postalCode {
                        name = 'CodigoPostal'
                        sqlType = 'varchar(15)'
                    }
                    owner {
                        name = 'NombreCorto'
                        sqlType = 'varchar(20)'
                    }
                    phoneNumber {
                        name = 'Telefonos'
                        sqlType = 'varchar(100)'
                    }
                    email {
                        name = 'eMail1'
                        sqlType = 'varchar(50)'
                    }
                    comments {
                        name = 'Comentarios'
                        sqlType = 'text'
                    }
                    priceList {
                        name = 'ListaPreciosEsp'
                        sqlType = 'varchar(20)'
                    }
                    type {
                        name = 'Tipo'
                        sqlType = 'varchar(15)'
                    }
                    condition {
                        name = 'Condicion'
                        sqlType = 'varchar(50)'
                    }
                    lastUpdated {
                        name = 'UltimoCambio'
                        sqlType = 'datetime(23)'
                    }
                    user {
                        name = 'Usuario'
                        sqlType = 'varchar(10)'
                    }
                }
            }

            product {
                enabled = false
                name = 'ART'
                list = [
                        'P. TERMINADOS PRECOLADOS',
                        'PRODUCTO DISTRIBUCION',
                        'PRODUCTO TERMINADO'
                ]
                column {
                    id {
                        name = 'Articulo'
                        sqlType = 'varchar(20)'
                    }
                    description {
                        name = 'Descripcion1'
                        sqlType = 'varchar(100)'
                    }
                    family {
                        name = 'Familia'
                        sqlType = 'varchar(50)'
                    }
                    enabled {
                        name = 'Estatus'
                        sqlType = 'varchar(15)'
                    }
                    group {
                        name = 'Grupo'
                        sqlType = 'varchar(50)'
                    }
                }
            }

            priceList {
                enabled = false
                list = [
                        '$-PRO',
                        '$-PUB',
                        'LISTA "A" FABRICAS',
                        'LISTA "B" FABRICAS',
                        'LISTA "C" FABRICAS',
                        'LISTA "D" FABRICAS',
                        'LISTA "E" FABRICAS'
                ]
                primaryList = '$-PUB' //El id debe estar dentro de list (el arreglo anterior), si es número debe ser long

                name = 'ListaPrecios'
                column {
                    id {
                        name = 'Lista'
                        sqlType = 'varchar(20)'
                    }
                    name {
                        name = 'Descripcion'
                        sqlType = 'varchar(100)'
                    }
                }

                priceListItem {
                    enabled = false
                    name = 'ListaPreciosD'
                    productCode {
                        name = 'Articulo'
                        sqlType = 'varchar(20)'
                    }
                    price {
                        name = 'Precio'
                        sqlType = 'money'
                    }
                    listCode {
                        name = 'Lista'
                        sqlType = 'varchar(20)'
                    }
                }

            }

            salesOrder {
                enabled = false

                name = 'Venta'
                column {
                    id {
                        name = 'ID'
                        sqlType = 'int(10)'
                    }
                    customerCode {
                        name = 'Cliente'
                        sqlType = 'varchar(10)'
                    }
                    dateCreated {
                        name = 'FechaEmision'
                        sqlType = 'datetime(23)'
                    }
                    lastUpdated {
                        name = 'UltimoCambio'
                        sqlType = 'datetime(23)'
                    }
                    deliveryDate {
                        name = 'FechaRequerida'
                        sqlType = 'datetime(23)'
                    }
                    limitDate {
                        name = 'Vencimiento'
                        sqlType = 'datetime(23)'
                    }
                    comments {
                        name = 'Comentarios'
                        sqlType = 'text'
                    }
                    priceList {
                        name = 'ListaPreciosEsp'
                        sqlType = 'varchar(20)'
                    }
                    warehouse {
                        name = 'Almacen'
                        sqlType = 'varchar(10)'
                    }
                    agent {
                        name = 'Agente'
                        sqlType = 'varchar(10)'
                    }
                    user {
                        name = 'Usuario'
                        sqlType = 'varchar(10)'
                    }
                    branch {
                        name = 'Sucursal'
                        sqlType = 'int(10)'
                    }
                    originBranch {
                        name = 'SucursalOrigen'
                        sqlType = 'int(10)'
                    }
                    saleBranch {
                        name = 'SucursalVenta'
                        sqlType = 'int(10)'
                    }
                    uen {
                        name = 'UEN'
                        sqlType = 'int(10)'
                    }
                    condition {
                        name = 'Condicion'
                        sqlType = 'varchar(50)'
                    }
                    company {
                        name = 'Empresa'
                        sqlType = 'varchar(5)'
                    }
                    type {
                        name = 'Mov'
                        sqlType = 'varchar(20)'
                    }
                    concept {
                        name = 'Concepto'
                        sqlType = 'varchar(50)'
                    }
                    currency {
                        name = 'Moneda'
                        sqlType = 'varchar(10)'
                    }
                    typeCurrency {
                        name = 'TipoCambio'
                        sqlType = 'float(53)'
                    }
                    status {
                        name = 'Estatus'
                        sqlType = 'varchar(15)'
                    }
                    priority {
                        name = 'Prioridad'
                        sqlType = 'varchar(10)'
                    }
                    rowId {
                        name = 'RenglonID'
                        sqlType = 'int(10)'
                    }
                    amount {
                        name = 'Importe'
                        sqlType = 'money(19)'
                    }
                    tax {
                        name = 'Impuestos'
                        sqlType = 'money(19)'
                    }

                }

                item {
                    enabled = false
                    name = 'VentaD'
                    column {
                        id {
                            name = 'ID'
                            sqlType = 'int(10)'
                        }
                        price {
                            name = 'Precio'
                            sqlType = 'float(53)'
                        }
                        productCode {
                            name = 'Articulo'
                            sqlType = 'varchar(20)'
                        }
                        row {
                            name = 'Renglon'
                            sqlType = 'float(53)'
                        }
                        warehouse {
                            name = 'Almacen'
                            sqlType = 'varchar(10)'
                        }
                        agent {
                            name = 'Agente'
                            sqlType = 'varchar(10)'
                        }

                        rowSub {
                            name = 'RenglonSub'
                            sqlType = 'int(10)'
                        }
                        rowId {
                            name = 'RenglonID'
                            sqlType = 'int(10)'
                        }
                        tax {
                            name = 'Impuesto1'
                            sqlType = 'float(53)'
                        }
                        fact {
                            name = 'Factor'
                            sqlType = 'float(53)'
                        }
                        unit {
                            name = 'Unidad'
                            sqlType = 'varchar(50)'
                        }
                        deliveryDate {
                            name = 'FechaRequerida'
                            sqlType = 'datetime(23)'
                        }
                        branch {
                            name = 'Sucursal'
                            sqlType = 'int(10)'
                        }
                        originBranch {
                            name = 'SucursalOrigen'
                            sqlType = 'int(10)'
                        }
                        uen {
                            name = 'UEN'
                            sqlType = 'int(10)'
                        }
                        quantity {
                            name = 'Cantidad'
                            sqlType = 'float(53)'
                        }
                    }
                }

            }

        }
    }

}
