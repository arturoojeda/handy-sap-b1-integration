# Conector para SAP B1 (v8 y v9) hacia Handy para BD SQL Server.

[Conoce mas sobre esta integracion aqui](http://ayuda.handy.la/general/integracion-sap-b1-handy).

Especificaciones recomendadas para el servidor donde residira la aplicacion:

3 GB de memoria asignada para Handy, 4 core CPU y 5 GB de almacenamiento asignado para Handy. 

NO se recomienda utilizar servidores de 32 bits por limitaciones de memoria.

Esta aplicacon es compatible con servidores Windows y Linux.

Pasos para la instalacion:

- Intala el SDK de JAVA 7 (JDK) y asegurate de agregarlo como variable de la linea de comando (que la linea de comando responda al comando "java" y que la variable de entorno JAVA_HOME apunte a la carpeta donde reside Java)

- Instala Tomcat 7 para 64 bits

- Crea la carpeta **/handy/sap-sync/** (Windows: C:/handy/sap-sync/) y asegurate que el usuario con el que corre Tomcat tiene acceso de escritura (sudo chmod 777 handy).

- Instala la DI API de SAP B1 de 64 bits (x64).

- Asegúrate de tener instalado [Grails 2.2.1](http://grails.org/download.html) correctamente. Lo puedes comprobar desde la línea de comando corriendo el comando "grails". También de te dejamos [este video](https://www.youtube.com/watch?v=Nu3GgjuUOtg) que muestra como montar todo el ambiente de Grails + Java completo en Windows.

Deberás recibir la siguiente respuesta en la linea de comandos:
```
| Enter a script name to run. Use TAB for completion: 
grails> 
```

- Descarga el codigo fuente de este repositorio

- Copia el archivo de configuraciones de este reposotorio llamado [ExternalConfigurations.groovy](https://github.com/arturoojeda/handy-sap-b1-integration/blob/master/ExternalConfigurations.groovy) a la carpeta raiz de Tomcat

- Crea una variable de entorno llamada **HandySyncEngineConfiguration** que apunte a dicho archivo con ruta absoluta.

- Crea un usuario administrador en el portal Handy, en tu cuenta. Se recomienda llamar al usuario "Integracion SAP" para su facil identificacion. Las credenciales de este usuario las necesitaras a continuacion.

- Crea una cuenta gratuita en [LogEntries](https://logentries.com/), el sistema que se estara utilizando para llevar los logs de operacion del sistema. Una vez creada la cuenta, copia el token de LogEntries para configurarlo a continuacion.

- Utilizando un editor de texto regular (recomendamos usar [SublimeText](https://www.sublimetext.com/) o [Notepad++](https://notepad-plus-plus.org/)) , edita el archivo de configuraciones con las credenciales de Handy sobre las siguientes variables y el token de LogEntries. En "company", introduce el nombre de tu empresa:

```
handy {
    username = ''
    password = ''
    logentriesToken = ''
    company = ''
    currency = 'MXN'
}
```

- De la misma manera, configura los datos de conexion a la BD de SAP en el siguiente apartado del mismo archivo:

```
dataSource_erp {
    username = 'USER'
    password = 'PASSWORD'

    // SQL Server
    dialect = org.hibernate.dialect.SQLServerDialect
    driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
    //Pruebas
    url = "jdbc:sqlserver://HOSTNAME:1433;databaseName=DATABASE_NAME"
}
```

- Sobre el mismo archivo, selecciona los datos de Handy que quieres sincronizar:

```
// ¿Guardar pedidos de Handy como oferta de ventas en SAP B1?
salesOrder {
    receive = true
    takeHandyPrice = false
    invoiceNotification = true
    quotationSelectExchange = false
    selectExchange = false
    update = false
}

// ¿Crear clientes en SAP B1 hacia Handy?
customer {
    send = true
    alwaysActive = false
}

// ¿Crear productos en SAP B1 hacia Handy?
product {
    send = true
}

// ¿Crear listas de precio en SAP B1 hacia Handy?
priceList {
    send = false
    sendFromProduct = true
}

// ¿Crear acuerdos de precios especiales en SAP B1 hacia Handy?
productPriceCustomer {
    send = false
    agreements = false
}
```
- Configura los datos de conexion a la instancia de SAP y también BD de nuevo:

```
erp {

    sap {
        enabled = false
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
 }
 ```

- Accede a la carpeta donde descargaste el código fuente de la aplicacin y corre el comando "grails prod war" para generar el archivo WAR de la aplicacion, que se generará en el subdirectorio /target

- Asegurate de que el servidio de Tomcat está detenido y copia el archivo WAR a la carpeta "webapps" de la instalacion de Tomcat 7. Cambia el nombre del archivo WAR a "handy.war"

- Crea el campo personalizado de [status de cliente (OCRD.U_HandyCustomerStatus)](http://ayuda.handy.la/general/integracion-sap-b1-handy) en la BD de SAP B1. Debe de ser tipo nvarchar(20)

- Crea el campo personalizado de [familia de producto (OITM.U_HandyProductCategory)](http://ayuda.handy.la/general/integracion-sap-b1-handy) en la BD de SAP B1. Debe de ser de tipo nvarchar(30)

- Para obtener un mejor rendimiento de la instancia de Tomcat, (duplica los valores default de asignacion de memoria)[http://www.mkyong.com/tomcat/tomcat-javalangoutofmemoryerror-permgen-space/].

- Corre el servicio de Tomcat

- Desde un navegador web, accede a la URL http://localhost:8080/handy (sensible a mayusculas y minusculas) y verifica que la pagina web te responde.

## Solucion de problemas

### Ofertas de venta no se guardan en SAP B1
- Verifica que el cliente no esté inactivo en SAP B1
- Verifica que el cliente usado en Handy, existe en SAP B1
