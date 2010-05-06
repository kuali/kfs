import groovy.swing.SwingBuilder
import javax.swing.*

class Table {
  Map attributes = [:]
  Table(Map attribs) {
    attributes = attribs
  }
}


class ChangelogCreateTable {
  String author
  String identifier
  String failOnErr = "true"
  String contextContents =  "kuldba"
  def changesetId = 1
  def generate(writer, tables) {
    // MarkupBuilder cannot output an XML declaration, so this is a workaround
    def eol = System.properties.'line.separator'
    writer << '<?xml version="1.0" encoding="UTF-8" standalone="no"?>' << eol

    def xml = new groovy.xml.MarkupBuilder(writer)
    xml.databaseChangeLog( xmlns : "http://www.liquibase.org/xml/ns/dbchangelog/1.9"
                         , "xmlns:xsi" : "http://www.w3.org/2001/XMLSchema-instance"
                         , "xsi:schemaLocation" : "http://www.liquibase.org/xml/ns/dbchangelog/1.9 http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.9.xsd"
                         ) {
      tables.each { table ->
        changeSet(author: author, id : (identifier << changesetId++), failOnError : failOnErr, context : table.getAttributes().get('context')) {
          comment(table.getAttributes().get('comments') )
          sql("<![CDATA[" + table.getAttributes().get('content')   + "]]>")

        }
      }
        println("total records = " + (changesetId - 1)  )
    }
  }
}


def tables =[]  
 
 // pass a directory or use the current directory
def jira = "5477"
def dateExecution
def doctype
def commentary
def baseDirectory = "./sql/"
def folder = args?.size() ? args[0] : baseDirectory
println "reading files from directory '$folder'"
def basedir = new File(folder)
def counter = 1
def outFilename
// result files of the current run
files = basedir.listFiles().grep(~/.*xml$/)



def askQuestion(question) {
    swingBuilder = new SwingBuilder()
    lastPane = swingBuilder.optionPane()
    choice = lastPane.showInputDialog(  null,
          question,
          "DATA QUESTION",
          JOptionPane.QUESTION_MESSAGE)
                       
    
}

def fileContent( currentFile) {
    lines = []
    currentFile.eachLine {
         line ->
        lines << line
    }
    println lines.join(" ")
    return lines.join(" ")
}

doctype = askQuestion("Enter the Doc Type")
if (doctype == null) exit()
authorName = askQuestion("Enter the author")
if (authorName == null) exit()
jira = askQuestion("Enter the Jira")
if (jira == null) exit()
dateExecution = askQuestion("Enter the date of Execution")
if (dateExecution == null) exit()
commentary = askQuestion("Enter comments")

println "JIRA=" + jira + " date=" + dateExecution + " comment=" + commentary
outFileName = baseDirectory + "INGEST-" +  doctype + ".xml"
def writer = new FileWriter(outFileName)


        
for (currentFile in files) {

    if (currentFile.name ==~ /[0-9]+.+/ ) {
        println "reject $currentFile"
    } else {

        contents = fileContent( currentFile)
        table = new Table(comments : ("INGEST " + currentFile.name + " " + commentary), context : "type-" + doctype, fileName : currentFile.name, content : contents )
        tables << table
        println "  processing $currentFile  "
    }
}
    def cct = new ChangelogCreateTable(author: authorName, identifier : (jira + "-INGEST-" + dateExecution + "-") )
          cct.generate(writer, tables)



    
       