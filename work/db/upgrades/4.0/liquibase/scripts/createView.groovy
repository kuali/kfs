class Table {
  Map attributes = [:]
  List columns = []
  Table(Map attribs) {
    attributes = attribs
  }
}


class ChangelogCreateTable {
  String author
  String identifier
  String failOnErr = "true"
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
        changeSet(author: author, id : (identifier << changesetId++), failOnError: failOnErr) {
          createView( table.getAttributes(), (table.getColumns().join()) ) {
          }
        }
      }
      println("total records = " + (changesetId - 1)  )
    }
  }
}

def checkTable(line)
{
    matcher = (line.toUpperCase() =~ /^[ ]*CREATE[ ]+VIEW[ ]+([a-zA-Z1-9_-]+).*/)
     if (matcher.matches()) {
         //println( matcher.getCount() + "occurences " + matcher[0][1])
         return matcher[0][1]
         
    }
    return false;
}


def istable = false;
def tables =[]
def dept = new Table(tableName:'departments')
def writer = new FileWriter('sample2.xml')
new File("infile.sql").eachLine {
     line ->
    

     liquibaseTableName = checkTable(line.trim())
     if ( liquibaseTableName != false) {
         println("new table " + liquibaseTableName)
         dept = new Table(viewName: liquibaseTableName)
         istable = true;\
         tables << dept
      } else {
         dept.columns << line
      }
     
 }
  def cct = new ChangelogCreateTable(author: "Bonnie", identifier : "5476d-")
          cct.generate(writer, tables)
          