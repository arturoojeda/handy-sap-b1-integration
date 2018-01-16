grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
def spockVersion = '0.7'

grails.project.dependency.resolution = {
    inherits("global") {}
    log "warn"
    checksums true
    legacyResolve false

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenLocal()
        mavenCentral()
        mavenRepo "http://repo.grails.org/grails/core"
        mavenRepo "http://repo.grails.org/grails/plugins"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "http://maven.springframework.org/milestone/"
        mavenRepo "http://mvnrepository.com/artifact/" //Add this Repo to download Logentries log4j Library
        mavenRepo 'http://repo.smokejumperit.com'
        mavenRepo "http://repository.appvisor.com/info/app-da00d326ca32/DHTMLX_JavaPlanner_pad.xml"
        mavenRepo 'http://repo.grails.org/grails/repo/'
        mavenRepo "https://mvnrepository.com/artifact/com.itextpdf/itextpdf"
        ebr()
    }

    dependencies {
        compile "com.logentries:logentries-appender:latest.integration"
        runtime 'mysql:mysql-connector-java:5.1.22'
        compile('org.codehaus.groovy.modules.http-builder:http-builder:0.6') { excludes "groovy" }
        runtime 'org.apache.derby:derby:10.12.1.1'
        compile 'com.google.guava:guava:19.0'
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
        compile "org.jadira.usertype:usertype.jodatime:1.9"
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.8.3"
        runtime ":resources:1.1.6"
        build ":tomcat:$grailsVersion"
        runtime ":database-migration:1.3.2"
        compile ':cache:1.0.1'
        compile ":quartz:1.0.2"
        compile ":quartz-monitor:1.0"
        compile ":console:1.5.8"
        compile ":csv:0.3.1"
        test(":spock:0.7") { exclude "spock-grails-support" }
        compile ":profiler:0.5"
        compile ":runtime-logging:0.4"
        runtime ":aws-sdk:1.11.11"
        compile ":joda-time:1.4"
    }

}
