Pasos para la instalacion:

- Intala el SDK de JAVA 7 (JDK) y asegurate de agregarlo como variable de la linea de comando (que la linea de comando responda al comando "java" y que la variable de entorno JAVA_HOME apunte a la carpeta donde reside Java)

- Instala Tomcat 7 para 64 bits

- Crea la carpeta **/handy/sap-sync/** (Windows: C:/handy/sap-sync/) y asegurate que el usuario con el que corre Tomcat tiene acceso de escritura (sudo chmod 777 handy).

- Instala la DI API de SAP B1.

- Asegúrate de tener instalado [Grails 2.2.1](http://grails.org/download.html) correctamente. Lo puedes comprobar desde la línea de comando corriendo el comando "grails".

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

- Utilizando un editor de texto regular, edita el archivo de configuraciones con las credenciales de Handy sobre las siguientes variables y el token de LogEntries. En "company", introduce el nombre de tu empresa:

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

- Accede a la carpeta donde descargaste el código fuente de la aplicacin y corre el comando "grails prod war" para generar el archivo WAR de la aplicacion, que se generará en el subdirectorio /target

- Asegurate de que el servidio de Tomcat está detenido y copia el archivo WAR a la carpeta "webapps" de la instalacion de Tomcat 7. Cambia el nombre del archivo WAR a "handy.war"

- Crea el campo personalizado de [status de cliente (OCRD.U_HandyCustomerStatus)](http://ayuda.handy.la/general/integracion-sap-b1-handy) en la BD de SAP B1.

- Crea el campo personalizado de [familia de producto (U_HandyProductCategory)](http://ayuda.handy.la/general/integracion-sap-b1-handy) en la BD de SAP B1.

- Corre el servicio de Tomcat

- Desde un navegador web, accede a la URL http://localhost:8080/handy (sensible a mayusculas y minusculas) y verifica que la pagina web te responde.

