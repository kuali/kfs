class Table {
  Map attributes = [:]
  List columns = []
  List modSqls = []
  Table(Map attribs) {
    attributes = attribs
  }
}

class Column {
  Map attributes = [:]
  Map constraintsAttributes = [:]
  Column(Map attribs) {
    attributes = attribs.findAll{it.key in ['name', 'type', 'defaultValue']}
    constraintsAttributes = attribs.findAll{it.key in ['nullable', 'primaryKey']}
  }
}

class ModSql {
  Map attributes = [:]
  Map appendAttributes = [:]
  Map replaceAttributes = [:]
  ModSql(Map attribs) {
    attributes = attribs.findAll{it.key in ['dbms', 'context']}
    replaceAttributes = attribs.findAll{it.key in ['replace','with']}
    appendAttributes = attribs.findAll{it.key in ['value']}
  }
}
    

class ChangelogCreateTable {
  String author
  String identifier
  String comments;
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
        changeSet(author: author, id : (identifier << changesetId++), , failOnError: "true") {
          comment( comments)
          createTable( table.getAttributes() ) {
            table.columns.each { col ->
              column(col.getAttributes() ) {
                if(col.getConstraintsAttributes()) {
                  constraints(col.getConstraintsAttributes())
                }
              }
            }   
          }
     
          table.getModSqls().each { modSql ->
            if (modSql.getReplaceAttributes().size() > 0) {
                modifySql( modSql.getAttributes() ) {
                    replace( modSql.getReplaceAttributes())
                }
            }
          }
         }
      }
      println("total records = " + (changesetId - 1)  )
    }
  }
}


def checkTable(line)
{
    matcher = (line =~ /(?i)^CREATE TABLE ([a-zA-Z1-9_-]+).*/)
     if (matcher.matches()) {
         //println( matcher.getCount() + "occurences " + matcher[0][1])
         return matcher[0][1]
    }
    return null    
}

def checkCol(line, table)
{
    if (line ==~ /.*;.*/) {
        println("Endof col" + line)
        return false
    }

   matcher = (line =~ /(?i)[^a-zA-Z]*PRIMARY KEY[ ]+\((.+)\).*/)
    if (matcher.matches()) {
          
        table.columns.each() {
            col ->
            if (matcher[0][1].find(col.attributes["name"]) != null) {
            //if (col.attributes["name"] == matcher[0][1]) {
                 println "primary key match " + matcher[0][1]
                 col.getConstraintsAttributes().put("primaryKey" , true) // <<  [primaryKey  : matcher[0][1]]
            }
        }
        return true
    }

    matcher = (line =~ /^[^a-zA-Z]*([a-zA-Z0-9_-]+)[ ]+([^ ]+\(.+\))(.*)/)  //  <name>  <type>(<size>)
     if (matcher.matches()) {
        // println( matcher.getCount() + "cols " + matcher[0][1] + "val " + matcher[0][2])
         liquibaseColumnName = matcher[0][1]
         liquibaseColumnType = matcher[0][2]
         liquibaseColumnNotNull = !(matcher[0][3] ==~ /(?i).*NOT NULL.*/)
         liquibaseDefaultVal = false
         defaultMatch = (matcher[0][3] =~ /(?i).*DEFAULT[ ']+([^ ']*).*/)
         if (defaultMatch.matches()) {
             liquibaseDefaultValue = defaultMatch[0][1]
             println "default " + defaultMatch[0][1]
             table.columns << new Column(name: liquibaseColumnName, type: liquibaseColumnType, defaultValue : liquibaseDefaultValue, nullable: liquibaseColumnNotNull )
         } else {
             table.columns << new Column(name: liquibaseColumnName, type: liquibaseColumnType, nullable: liquibaseColumnNotNull )
         }
         return true
    }
    matcher = (line =~ /^[^a-zA-Z]*([a-zA-Z0-9_-]+)[ ]+([^ ,]+)(.*)/)  
    if (matcher.matches()) {
        // println( matcher.getCount() + "cols " + matcher[0][1] + "val " + matcher[0][2])
         liquibaseColumnName = matcher[0][1]
         liquibaseColumnType = matcher[0][2]
         liquibaseColumnNotNull = !(matcher[0][3] ==~ /(?i).*NOT NULL.*/)
         liquibaseDefaultVal = false
         defaultMatch = (matcher[0][3] =~ /(?i).*DEFAULT[ ']+([^ ']*).*/)
         
         if (defaultMatch.matches()) {
             liquibaseDefaultValue = defaultMatch[0][1]
             println "default " + defaultMatch[0][1]
             table.columns << new Column(name: liquibaseColumnName, type: liquibaseColumnType, defaultValue : liquibaseDefaultValue, nullable: liquibaseColumnNotNull )
         } else {
             table.columns << new Column(name: liquibaseColumnName, type: liquibaseColumnType, nullable: liquibaseColumnNotNull )
         }
         return true
    }
    return false
}

def isTable = false
def tables =[]
def dept = new Table(tableName:'departments')
def writer = new FileWriter('sample2.xml')

new File("infile.sql").eachLine {
     line ->

 if (isTable == true) {
    isTable = checkCol(line, dept)
     if (isTable == true) {
         println("cols " + liquibaseColumnName + "val " + liquibaseColumnType)    
     } else {
             tables << dept         
     } 
 } else {
     liquibaseTableName = checkTable(line)
     if (liquibaseTableName != null) {
          println("new table " + liquibaseTableName)
          dept = new Table(tableName: liquibaseTableName )
          dept.getModSqls() << new ModSql(dbms: "mysql", replace : "VARCHAR2", with : "VARCHAR")
           dept.getModSqls() << new ModSql(dbms: "mysql", replace : "NUMBER", with : "DECIMAL")
          isTable = true
      }
   }
 }
  def cct = new ChangelogCreateTable(author: "Bonnie", identifier : "2010-04-26-5615-3-", comments : " update endowment tables - create Tables")
          cct.generate(writer, tables)