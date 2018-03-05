# Conector para SAP B1 (v8 y v9) hacia Handy para BD SQL Server.

[Conoce mas sobre esta integracion aqui](http://ayuda.handy.la/general/integracion-sap-b1-handy).

## Especificaciones recomendadas para el servidor donde residira la aplicacion

Windows Server con 3 GB de memoria RAM asignada para Handy, 4 core CPU y 5 GB de almacenamiento libres.

Se realizaron pruebas con Windows Server 2012 y 2016 operando correctamente. Con Windows 7 hay ocasiones en las que se presentan bloqueos de seguridad de las conexiones salientes.

NO se recomienda utilizar servidores de 32 bits por limitaciones de memoria.

Esta aplicacion es compatible con servidores Windows y Linux.

## Pasos para la instalacion

- Intala el SDK de JAVA 7 (JDK) y asegurate de agregarlo como variable de la linea de comando (que la linea de comando responda al comando "java" y que la variable de entorno JAVA_HOME apunte a la carpeta donde reside Java). Oracle ya no tiene disponible el JDK 7 para su descarga. Si estás usando Windows, te recomendamos [esta opcion](https://github.com/alexkasko/openjdk-unofficial-builds) ([64 bits](https://bitbucket.org/alexkasko/openjdk-unofficial-builds/downloads/openjdk-1.7.0-u80-unofficial-windows-amd64-installer.zip), [32 bits](https://bitbucket.org/alexkasko/openjdk-unofficial-builds/downloads/openjdk-1.7.0-u80-unofficial-windows-i586-installer.zip)), aunque es no oficial. Para Linux y macOS, puedes usar [OpenJDK](http://openjdk.java.net/). [Este video](https://www.youtube.com/watch?v=Nu3GgjuUOtg), elabora sobre la instalacion de Java, ademas de la instalacion de Grails (siguientes pasos).

- Instala Tomcat 7 para 64 bits. Es mas sencillo si lo instalas como servicio porque ademas puedes hacer que arranque automaticamente al encender el servidor.

- Crea la carpeta **/handy/** (Windows: C:/handy/) y asegurate que el usuario con el que corre Tomcat tiene acceso de escritura (sudo chmod 777 handy). En esta carpeta se almacenara la BD temporal de sincronizacion.

- Instala la DI API de SAP B1 de 64 bits (x64).

- Instala[Grails 2.2.1](http://grails.org/download.html). Puedes comprobar la instalacion desde la línea de comando corriendo el comando "grails". También de te dejamos [este video](https://www.youtube.com/watch?v=Nu3GgjuUOtg) que muestra como montar todo el ambiente de Grails + Java completo en Windows.

Deberás recibir la siguiente respuesta en la linea de comandos:
```
| Enter a script name to run. Use TAB for completion: 
grails> 
```

- Descarga el codigo fuente de este repositorio

- Copia los archivos de la DI API (C:\Program Files\SAP\SAP Business One DI API\JCO\LIB) a la subcarpeta "lib" de este proyecto, reemplazando los archivos existentes. Esto asegura que los archivos JAR de la versin de la API instalada coincidan con tu version de SAP durante la compilacion del codigo fuente.

- Copia el archivo de configuraciones de este reposotorio llamado [ExternalConfigurations.groovy](https://github.com/arturoojeda/handy-sap-b1-integration/blob/master/ExternalConfigurations.groovy) a la carpeta **/handy/** que creaste antes.

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

- Accede a la carpeta donde descargaste el código fuente de la aplicacion y corre el comando "grails prod war" para generar el archivo WAR de la aplicacion, que se generará en el subdirectorio **/target**

- Asegurate de que el servicio de Tomcat está detenido y copia el archivo WAR a la carpeta "webapps" de la instalacion de Tomcat 7. Cambia el nombre del archivo WAR a "handy.war"

- Crea el campo personalizado de [status de cliente (OCRD.U_HandyCustomerStatus)](http://ayuda.handy.la/general/integracion-sap-b1-handy) en la BD de SAP B1. Debe de ser tipo **nvarchar(20)**. Una vez creado este campo, deberas llenarlo con el valor "02" para los clientes que quieres sincronizar a Handy y "01" para los que no quieres sincronizar.

- Crea el campo personalizado de [familia de producto (OITM.U_HandyProductCategory)](http://ayuda.handy.la/general/integracion-sap-b1-handy) en la BD de SAP B1. Debe de ser de tipo **nvarchar(30)**. Una vez creado este campo, deberas llenarlo con el nombre de la familia de productos correspondiente que quieres que aparezca en Handy para cada producto. Si no lo llenas, los productos se crearan bajo la familia llamada "Sin familia". NOTA: si ya tenas un campo personalizado con este dato, no tienes que crear este campo, sólo tienes que ir al archivo ExternalConfigurations.groovy y en la seccion erp->sap->tables->product->column->family. Coloca ahi el nombre del campo y su tipo.

- Para obtener un mejor rendimiento de la instancia de Tomcat, (duplica los valores default de asignacion de memoria)[http://www.mkyong.com/tomcat/tomcat-javalangoutofmemoryerror-permgen-space/].

![alt text](https://github.com/arturoojeda/handy-sap-b1-integration/blob/master/tomcat%20memory.png?raw=true "Duplicar memoria Tomcat")


- Corre el servicio de Tomcat y espera unos 5 minutos para que todo inicialice.

- Desde un navegador web, accede a la URL http://localhost:8080/handy (sensible a mayusculas y minusculas) y verifica que la pagina web te responde.

## Solucion de problemas

### Ofertas de venta no se guardan en SAP B1
- Verifica que el cliente no esté inactivo en SAP B1
- Verifica que el cliente usado en Handy, existe en SAP B1

### La aplicacion no compila
- Si es por un error de depdendencias, borra la carpeta /Usuarios/tu_usuario/.grails (aplica de forma similar para Linux/macOS 7 Windows)

### La aplicacion no inicializa
- Asegura que el usuario con el que corre el servicio de Tomcat tiene acceso a leer el archivo ExternalConfigurations.groovy. Si esto no funciona, fusiona el archivo Config.groovy del proyecto con el de ExternalConfigurations.groovy ya que pudiera ser que tu archivo externo no se esta leyendo por permisos hacia el archivo externo. 
