import groovy.json.JsonSlurper


class Configurations {

    static load(){
        def inputFile = new File("//configurations.txt")
        def InputJSON = new JsonSlurper().parseText(inputFile.text)
        InputJSON.each{ println it }
    }
}
