Pre-requisitos:

- Intala el SDK de JAVA 7 (JDK) y asegurate de agregarlo como variable de la linea de comando (que la linea de comando responda al comando "java" y que la variable de entorno JAVA_HOME apunte a la carpeta donde reside Java)

- Instala Tomcat 7 para 64 bits

- Instala la DI API de SAP B1.

- Asegúrate de tener instalado [Grails 2.2.1](http://grails.org/download.html) correctamente. Lo puedes comprobar desde la línea de comando corriendo el comando "grails".

Deberás recibir la siguiente respuesta en la linea de comandos:
```
| Enter a script name to run. Use TAB for completion: 
grails> 
```

- Descarga el codigo fuente de este repositorio

- Accede a la carpeta donde descargaste el código fuente de la aplicacin y corre el comando "grails prod war" para generar el archivo WAR de la aplicacion, que se generará en el subdirectorio /target

- Asegurate de que el servidio de Tomcat está detenido y copia el archivo WAR a la carpeta "webapps" de la instalacion de Tomcat 7. Cambia el nombre del archivo WAR a "handy.war"

- Crea el campo personalizado de [status de cliente (OCRD.U_HandyCustomerStatus)](http://ayuda.handy.la/general/integracion-sap-b1-handy) en la BD de SAP B1.

- Crea el campo personalizado de [familia de producto (U_HandyProductCategory)](http://ayuda.handy.la/general/integracion-sap-b1-handy) en la BD de SAP B1.


Debes de crear la siguiente carpeta en tu servidor para que la aplicación pueda correr:
/handy/sap-sync/

O en Windows:

C:/handy/sap-sync/

También debes de dar permisos de escritura a la carpeta de handy con el siguiente comando de terminal:
sudo chmod 777 handy

