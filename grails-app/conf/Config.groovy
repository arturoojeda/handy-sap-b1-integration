import org.apache.log4j.Level
import org.apache.log4j.RollingFileAppender

if (System.getenv().get("HandySyncEngineConfiguration")) {
    grails.config.locations = ["file:" + System.getenv().get("HandySyncEngineConfiguration")]
}

grails.project.groupId = appName
grails.mime.file.extensions = true
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]

grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
grails.views.default.codec = "none"
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
grails.views.gsp.sitemesh.preprocess = true
grails.scaffolding.templates.domainSuffix = 'Instance'
grails.json.legacy.builder = false
grails.enable.native2ascii = true
grails.spring.bean.packages = []
grails.web.disable.multipart=false
grails.exceptionresolver.params.exclude = ['password']
grails.hibernate.cache.queries = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
    }
}

dataSource {
    pooled = true
}

hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}

environments {

    development {
        grails.logging.jul.usebridge = false
        log4j = {
            appenders {
                console name: 'stdout', layout: pattern(conversionPattern: '%c{2} %m%n'), threshold: org.apache.log4j.Level.INFO
            }

            debug 'org.hibernate.SQL'
            trace 'org.hibernate.type.descriptor.sql.BasicBinder'

            error  'org.codehaus.groovy.grails.web.servlet',
                    'org.codehaus.groovy.grails.web.pages',
                    'org.codehaus.groovy.grails.web.sitemesh',
                    'org.codehaus.groovy.grails.web.mapping.filter',
                    'org.codehaus.groovy.grails.web.mapping',
                    'org.codehaus.groovy.grails.commons',
                    'org.codehaus.groovy.grails.plugins',
                    'org.codehaus.groovy.grails.orm.hibernate',
                    'org.springframework',
                    'org.hibernate',
                    'net.sf.ehcache.hibernate'

            root {
                error 'stdout'
                warn 'stdout'
                info 'stdout'
                additivity = true
            }
        }
        dataSource_local {
            dbCreate = 'update'
            driverClassName = 'org.apache.derby.jdbc.EmbeddedDriver'
            url = "jdbc:derby:/handy/sap-sync/handy-sync-engine-derby-db-local;create=true;"
        }
        dataSource_erp {
            dbCreate = 'update'
            driverClassName = 'org.apache.derby.jdbc.EmbeddedDriver'
            url = "jdbc:derby:/handy/sap-sync/handy-sync-engine-derby-db-erp;create=true;"
        }
        hibernate{
            format_sql = false
            use_sql_comments = true
        }
        handy {
            server = 'https://www.handy-app.net'
            username = ''
            password = ''
        }
    }

    test {
        log4j = {
            appenders {
                console name: 'stdout', layout: pattern(conversionPattern: '%c{2} %m%n'), threshold: org.apache.log4j.Level.INFO
                rollingFile name: "testFileAppender", maxFileSize: 1024, file: "/handy/sap-sync/handy-sap-sync-test-log.txt", threshold: org.apache.log4j.Level.ERROR
            }

            error  'org.codehaus.groovy.grails.web.servlet',
                    'org.codehaus.groovy.grails.web.pages',
                    'org.codehaus.groovy.grails.web.sitemesh',
                    'org.codehaus.groovy.grails.web.mapping.filter',
                    'org.codehaus.groovy.grails.web.mapping',
                    'org.codehaus.groovy.grails.commons',
                    'org.codehaus.groovy.grails.plugins',
                    'org.codehaus.groovy.grails.orm.hibernate',
                    'org.springframework',
                    'org.hibernate',
                    'net.sf.ehcache.hibernate'

            root {
                error 'stdout', 'testFileAppender'
                warn 'stdout'
                info 'stdout'
                additivity = true
            }
        }
        dataSource_local {
            dbCreate = "create-drop"
            driverClassName = 'org.apache.derby.jdbc.EmbeddedDriver'
            url = "jdbc:derby:/handy/sap-sync/handy-sync-engine-derby-db-local;create=true;"
        }
        dataSource_erp {
            dbCreate = "create-drop"
            driverClassName = 'org.apache.derby.jdbc.EmbeddedDriver'
            url = "jdbc:derby:/handy/sap-sync/handy-sync-engine-derby-db-erp;create=true;"
        }
        handy {
            server = 'https://www.handy-app.net'
            username = ''
            password = ''
        }
    }

    production {
        grails.logging.jul.usebridge = false
        log4j = {
            appenders {
                RollingFileAppender(name: "stacktrace", maxFileSize: 1024 * 1024 * 20)
                appender new com.logentries.log4j.LogentriesAppender(name: 'le', layout: pattern(conversionPattern: "%d{yyyy-MM-dd hh:mm:ss} - %p - %c{2} %m%n"), token: handy.logentriesToken, threshold: Level.INFO)
            }

            error  'org.codehaus.groovy.grails.web.servlet',
                    'org.codehaus.groovy.grails.web.pages',
                    'org.codehaus.groovy.grails.web.sitemesh',
                    'org.codehaus.groovy.grails.web.mapping.filter',
                    'org.codehaus.groovy.grails.web.mapping',
                    'org.codehaus.groovy.grails.commons',
                    'org.codehaus.groovy.grails.plugins',
                    'org.codehaus.groovy.grails.orm.hibernate',
                    'org.springframework',
                    'org.hibernate',
                    'net.sf.ehcache.hibernate'

            root {
                error 'stdout', 'le'
                warn 'stdout', 'le'
                info 'stdout', 'le'
                additivity = true
            }
        }
        dataSource_local {
            dbCreate = 'update'
            driverClassName = 'org.apache.derby.jdbc.EmbeddedDriver'
            url = "jdbc:derby:/handy/handy-sync-engine-derby-db;create=true;territory=es_MX;collation=TERRITORY_BASED"
        }
        dataSource_erp {
            dbCreate = 'validate'
        }
    }
}
