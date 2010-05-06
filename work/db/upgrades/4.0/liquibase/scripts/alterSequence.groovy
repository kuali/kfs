class Table {
  Map attributes = [:]
  List columns = []
  Table(Map attribs) {
    attributes = attribs
  }
}

class Column {
  Map attributes = [:]
  Map constraintsAttributes = [:]
  Column(Map attribs) {
    attributes = attribs.findAll{it.key in ['incrementBy', 'minValue', 'maxValue', 'ordered','startValue']}
    constraintsAttributes = attribs.findAll{it.key in ['nullable']}
  }
}

class ChangelogCreateTable {
  String author
  String identifier
  String mycomments
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
        changeSet(author: author, id : (identifier << changesetId++), failOnError : true, dbms: "oracle") {
          comment(mycomments)
          createSequence( table.getAttributes() ) {
            table.columns.each { col ->
              column(col.getAttributes() ) {
                if(col.getConstraintsAttributes()) {
                  constraints(col.getConstraintsAttributes())
                }
              }
            }
          }
        }
        
      }
      tables.each { table ->
        changeSet(author: author, id : (identifier << changesetId++), failOnError : true, dbms: "mysql") {
          comment(mycomments)
          createTable( tableName :  table.attributes['sequenceName'] ) {
              column(name : "id", type :"bigint(19)", autoIncrement : true) {
                  constraints(primaryKey : true, primaryKeyName: "id", nullable : false)
              }
          }
          sql("ALTER TABLE " +  table.attributes['sequenceName'] + " auto_increment=" + table.attributes['incrementBy'] )
        }
      }

      println("total records = " + (changesetId - 1)  )
    }
  }
}

def checkTable(line)
{
    matcher = (line =~ /^[ ]*CREATE[ ]+SEQUENCE[ ]+([a-zA-Z1-9_-]+).*/)
     if (matcher.matches()) {
         //println( matcher.getCount() + "occurences " + matcher[0][1])
         return matcher[0][1]
         
    }
    return false;
}

def checkCol(line,seqTable)
{
    eolflag = (line ==~ /.+;.*/)
    line = line.replaceFirst( /;/,"")
    
    tokens = line.tokenize()
  
    if (tokens[0] == "MINVALUE") {
           seqTable.getAttributes().put( "minValue" ,tokens[1])
    } else if (tokens[0] == "START") {
       seqTable.getAttributes().put( "startValue" , tokens[2])
    } else if (tokens[0] == "INCREMENT") {
         seqTable.getAttributes().put( "incrementBy" , tokens[2])
    } else if (tokens[0] == "NOCACHE") {
       // return new Column(nocache : "")
    } else if (tokens[0] == "ORDER") {
        if (tokens[1] == "NOCYCLE" ) {
             seqTable.getAttributes().put("ordered" , "true")
        } else {
             seqTable.getAttributes().put( "ordered" , "false")
        }
    }
    return eolflag
}

def istable = false;
def tables =[]
def dept = new Table(tableName:'departments')
def writer = new FileWriter('sample2.xml')
new File("infile.sql").eachLine {
     line ->
    
     if (istable == true) {
         col = checkCol(line, dept)
         if (col == false) {
             println( line)
         } else {
             istable = false
             println("eol")
         }
     }
     else {
         liquibaseTableName = checkTable(line.trim())
         if ( liquibaseTableName != false) {
              println("new table " + liquibaseTableName)
              dept = new Table(sequenceName: liquibaseTableName)
              istable = true;
              tables << dept
         }
     }
     
 }
  def cct = new ChangelogCreateTable(author: "Bonnie", identifier : "5476b-", mycomments : "create endow sequences.sql")
          cct.generate(writer, tables)
          