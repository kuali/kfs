import groovy.swing.SwingBuilder
import javax.swing.*


class Table {
  Map attributes = [:]
  Boolean dropTable = false
  List changes = []
 
  String whereClause
  List modSqls = []
  Table(Map attribs) {
    attributes = attribs
  }
  
  void modification(xml) {
      if (this.dropTable == true) {
          dropTable( this.getAttributes() )
      }
              
      if (this.changes.size() > 0) changes.get(0).modification(this, xml)
          
      this.getModSqls().each { modSql ->
          if (modSql.getReplaceAttributes().size() > 0) {
              xml.modifySql( modSql.getAttributes() ) {
                  xml.replace( modSql.getReplaceAttributes())
              }
          }
      }         
      this.getModSqls().each { modSql ->
          if (modSql.getAppendAttributes().size() > 0) {
              xml.modifySql( modSql.getAttributes() ) {
                  xml.append( modSql.getAppendAttributes())
              }
          }
       }
   }
}

interface LiquibaseCommonTableInterface 
{
   void modification(table, xml)
}

class Column implements LiquibaseCommonTableInterface {
  Map attributes = [:]
  Column(Map attribs) {
    attributes = attribs.findAll{it.key in ['name', 'type', 'value', 'valueDate' , 'valueNumeric']}
  }
  void modification(table, xml) {
      xml.insert( table.getAttributes()) {
          table.getChanges().each { change ->
              xml.column(change.getAttributes())
          }
      }
  } 
}

class UpdateColumn implements LiquibaseCommonTableInterface {
  Map attributes = [:]
  UpdateColumn( Map attribs) {
    attributes = attribs.findAll{it.key in ['name', 'type', 'value' , 'valueNumeric', 'where']}
  }
   void modification(table, xml) {
      xml.update( table.getAttributes()) {
          table.getChanges().each { change ->
              xml.column(change.getAttributes())
          } 
          if (table.getWhereClause() != null) xml.where(table.getWhereClause())        
      }
  }
}

class DeleteData implements LiquibaseCommonTableInterface {
    String where
    DeleteData(wh) {
        where = wh.trim()
   } 
    void modification(table,xml) {
       xml.delete( table.getAttributes()) {
           if (this.where != '') xml.where(this.where )
       } 
   }
}

class PrimaryKey implements LiquibaseCommonTableInterface {
  Map attributes = [:]
  PrimaryKey(Map attribs) {
    attributes = attribs
  }
  void modification(table, xml) {
      xml.addPrimaryKey( this.attributes ) 
  }
}

class DropConstraints extends PrimaryKey {
    DropConstraints(Map attribs) {
        super(attribs)
    }
    
    void modification(table, xml) {
        xml.dropForeignKeyConstraint( this.attributes)
    }
}

class ForeignKeys extends PrimaryKey {
    ForeignKeys(Map attribs) {
        super(attribs)
    }
     void modification(table, xml) {
        xml.addForeignKeyConstraint(this.attributes)
    }
}

class AlterColumn implements LiquibaseCommonTableInterface {
  Map attributes = [:]
  Map constraintsAttributes = [:]
  AlterColumn(Map attribs) {
    attributes = attribs.findAll{it.key in ['name', 'type', 'defaultValue']}
    constraintsAttributes = attribs.findAll{it.key in ['nullable']}    
    //tableName = attribs.findAll{it.key in ['tableName']}
  }
  
  void modification(table, xml) {
    xml.modifyColumn( table.getAttributes()) {
        table.getChanges().each { change ->
            xml.column(change.getAttributes() ) {
                if (change.getConstraintsAttributes()) {
                    xml.constraints(change.getConstraintsAttributes())
                }
            }
        }
    }
  }
}


class AddColumn extends AlterColumn {
    AddColumn(Map attribs) {
        super(attribs)
    }
    void modification(table, xml) {
        xml.addColumn( table.getAttributes()) {
            table.getChanges().each { change ->
                 xml.column(change.getAttributes()) {
                     if(change.getConstraintsAttributes()) {
                         xml.constraints(change.getConstraintsAttributes())
                     }
                 }
             }
        }
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
  String comments = "insert Endowment sample data to KULDBA"
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
        changeSet(author: author, id : (identifier << changesetId++), failOnError : failOnErr) {
          comment(comments)
          table.modification(xml)
        }
        println("total records = " + (changesetId - 1)  )
      }
    }
  }
}

def checkEOL(line)
{
    if (line ==~ /;/) {
        return true
    }
    return false
}

def removeQuotes(token)
{
    valInsert = (token =~ /[^a-zA-Z0-9; !@#$%^&\*\+,=:\/_-]/).replaceAll("")
    return valInsert.trim()
}
    

def evaluateList(table, cols, vals) 
{
    int j = 0;
    monthList = ["JAN" : "01","FEB" : "02" ,"MAR" : "03" ,"APR" : "04" ,"MAY" : "05" ,"JUN" : "06" ,"JUL" : "07" ,"AUG" : "08" ,"SEP" : "09" ,"OCT" : "10" ,"NOV" : "11" ,"DEC" : "12"]
    for (int i = 0; i < vals.size(); i++ ) {
        tokenType = cols[j++].trim()
     
        token = vals[i].trim()
        if (token.toUpperCase().startsWith("TO_DATE") == true) {
            newDate = ""
            dateArray = (token =~ /(?i)TO_DATE[^0-9]+([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+).*/)
            if (dateArray.matches()) {
                month = (dateArray[0][1].size() == 2) ? dateArray[0][1] : "0" + dateArray[0][1]
                day = (dateArray[0][2].size() == 2) ? dateArray[0][2] : "0" + dateArray[0][2]
                newDate = dateArray[0][3] + '-' + month + "-" + day
                table.getChanges() << new Column(name : tokenType, valueDate : newDate )
            } else {
                // TO_DATE('', 'mm/dd/yyyy'),
                newDate = ""
               // table.getColumns() << new Column(name : tokenType, valueNumeric : 'SYSDATE' )
            }
            
            i++
            //println "date " + newDate
        } else if (token.toUpperCase().startsWith("TO_TIMESTAMP") == true) {
            newDate = ""
            dateArray = (token =~ /(?i)TO_TIMESTAMP[^0-9]+([0-9]+)-([a-zA-Z]+)-([0-9]+) ([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+) ([A-Z]*).*/)
           
            if (dateArray.matches()) {
                day = (dateArray[0][1].size() == 2) ? dateArray[0][1] : "0" + dateArray[0][1]
                month = monthList [ dateArray[0][2] ]
                year = (dateArray[0][3].size() == 2) ? ("20" + dateArray[0][3]) : dateArray[0][3]
                hour = (dateArray[0][8] == "PM") ? (dateArray[0][4].toInteger() + 12) : dateArray[0][4]
                newDate = year + '-' + month + "-" + day + "T" + hour + ":" + dateArray[0][5] + ":" + dateArray[0][6]
                println "NEW DATE " + newDate
                table.getChanges() << new Column(name : tokenType, valueDate : newDate )
            } else {
                dateArray = (token =~ /(?i)TO_TIMESTAMP[^0-9]+([0-9]+)-([a-zA-Z]+)-([0-9]+).*/)
                if (dateArray.matches()) {
                    day = (dateArray[0][1].size() == 2) ? dateArray[0][1] : "0" + dateArray[0][1]
                    month = monthList [ dateArray[0][2] ]
                    year = (dateArray[0][3].size() == 2) ? ("20" + dateArray[0][3]) : dateArray[0][3]
                    newDate = year + '-' + month + "-" + day
                    println "NEW DATE: " + newDate
                    table.getChanges() << new Column(name : tokenType, valueDate : newDate )
                 }
            }
            i++
        } else if (token.toUpperCase() == ("SYS_GUID()")) {
            table.getChanges() << new Column(name : tokenType, type: "UUID",  valueNumeric : removeQuotes( token.toUpperCase()) )
        } else if (token.toUpperCase() == "NULL") {
            // do nothing
        } else if ( (token.isNumber()) || (token.startsWith("\'") == false) && (token.endsWith("\'") == false)) {
            //println "numeric " + token
            table.getChanges() << new Column(name : tokenType, valueNumeric : removeQuotes( token))
        } else {
            // need to check val after if a quote follows otherwise concatenate it
             while ((i + 1) < vals.size()) {
                if (token.endsWith("\'")) break
                if (vals[i+1] == null) break
                if (vals[i+1].startsWith(" \'")) break
                token = token + "," +  vals[++i]
                println "incrementing " + token
             }
            table.getChanges() << new Column(name : tokenType, value : removeQuotes( token))
        }
        
     }     
     if (j != cols.size()) println "*** ERROR  TOKEN MISMATCH " + cols.join(":")     
}

def checkInsertion(line, tables)
{
   matcher = (line.toUpperCase() =~ /(?i) *INSERT INTO ([a-zA-Z1-9_-]+).*/)
   if ((matcher.matches()) == false) return false
   
   liquibaseTableName = matcher[0][1]
   table = new Table(tableName: liquibaseTableName)
   println("new Insertion " + liquibaseTableName)
   table.getModSqls() << new ModSql(dbms : "mysql", replace : "SYS_GUID", with : "uuid()")
   table.getModSqls() << new ModSql(dbms : "mysql", replace : "SYSDATE", with : "now()")
   table.getModSqls() << new ModSql(dbms : "oracle", replace : "SYS_GUID", with : "sys_guid()")
   tables << table
        
        
    matcher = (line =~ /(?i).+COLUMNS[ ]*\(([^\)]+)\).+VALUES *\((.+)\).*/)
     if (matcher.matches()) {
        cols = matcher[0][1].tokenize(',')
        vals = matcher[0][2].tokenize(',')
        evaluateList(table, cols, vals);

        return true
     }
         
    matcher = (line =~ /(?i).*INSERT INTO.+\(([^\)]+)\).+VALUES *\((.+)\).*/)
     if (matcher.matches()) {
        cols = matcher[0][1].tokenize(',')
        vals = matcher[0][2].tokenize(',')
        evaluateList(table, cols, vals)
        return true
     }
     return false
}

def checkDeletion(line, tables)
{
   matcher = (line =~ /(?i) *DELETE FROM ([a-zA-Z1-9_-]+).*/)
   if ((matcher.matches()) == false) return false
   
   liquibaseTableName = matcher[0][1]
   table = new Table(tableName: liquibaseTableName)
   tables << table
        
        
    matcher = (line =~ /(?i).+WHERE ([^;]+);.*/)
     if (matcher.matches()) {
        println("new deletion " + liquibaseTableName)
        table.getChanges() << new DeleteData(matcher[0][1])
     } else {
         println('delete all data ' + liquibaseTableName)
         table.getChanges() << new DeleteData('')
     }
     return true;
}

def checkAlters(line,tables) 
{
     matcher = (line =~ /(?i)[ ]*ALTER TABLE ([a-zA-Z1-9_-]+)[ ]+DROP CONSTRAINT[ ]+([a-zA-Z0-9_-]+).*/)
     if (matcher.matches()) {
         //println( matcher.getCount() + "occurences " + matcher[0][1])    
         println(" Drop Constraint " + matcher[0][1] + " key " + matcher[0][2])
             liquibaseTableName = matcher[0][1]
             liquibasePrimeKey = matcher[0][2]
             table = new Table()
             pk = new DropConstraints( baseTableName: liquibaseTableName , constraintName : liquibasePrimeKey)
             table.getChanges() << pk
             tables << table
             return true
            
         }

    matcher = (line =~ /(?i)[ ]*ALTER TABLE ([a-zA-Z1-9_-]+)[ ]+MODIFY[ \(]*([a-zA-Z0-9_-]+)[ ]+([^ "']+\))[ ]*([^ '"]*).*;/)
     if (matcher.matches()) {
         println(" alter Modify " + matcher[0][1] + " key " + matcher[0][2] + " on " + matcher[0][3].toUpperCase()) 
         liquibaseTableName = matcher[0][1]        
         table = new Table(tableName: liquibaseTableName)
         tables << table
         println "modify " + matcher[0][4]
         if (matcher[0][4] != null) {
              table.getChanges() << new AlterColumn( name : matcher[0][2], type : matcher[0][3].toUpperCase(), defaultValue : matcher[0][4])
         } else {
             table.getChanges() << new AlterColumn( name : matcher[0][2], type : matcher[0][3].toUpperCase())
         }
         table.getModSqls() << new ModSql(dbms: "mysql", replace : "VARCHAR2", with : "VARCHAR")
         table.getModSqls() << new ModSql(dbms: "mysql" ,replace : "NUMBER", with : "DECIMAL")
         return true
     }
     matcher = (line =~ /(?i)[ ]*ALTER TABLE ([a-zA-Z1-9_-]+)[ ]+MODIFY[ \(]*([a-zA-Z0-9_-]+)[ ]+([^ "']+\))[ ]*DEFAULT[ '"]+([^ '"]*).*;/)
     if (matcher.matches()) {
         println(" alter Modify " + matcher[0][1] + " key " + matcher[0][2] + " default " + matcher[0][4].toUpperCase()) 
         liquibaseTableName = matcher[0][1]        
         table = new Table(tableName: liquibaseTableName)
         tables << table
         table.getChanges() << new AlterColumn( name : matcher[0][2], type : matcher[0][3].toUpperCase(), defaultValue : matcher[0][4].toUpperCase())
         table.getModSqls() << new ModSql(dbms: "mysql", replace : "VARCHAR2", with : "VARCHAR")
         table.getModSqls() << new ModSql(dbms: "mysql" ,replace : "NUMBER", with : "DECIMAL")
         return true
     }

 matcher = (line =~ /(?i)[ ]*ALTER TABLE ([a-zA-Z1-9_-]+)[ ]+ADD CONSTRAINT[ ]+([a-zA-Z0-9_-]+)[ ]+(.+)/)
     if (matcher.matches()) {
         println( matcher.getCount() + "occurences " + matcher[0][1])    
         defaultMatch = (matcher[0][3] =~ /(?i)[ ]*PRIMARY KEY[ ]+\((.+)\).*/)
         if (defaultMatch.matches()) {
             println(" alter Primary " + matcher[0][1] + " key " + matcher[0][2] + " on " + defaultMatch[0][1])
             liquibaseColumnNames = defaultMatch[0][1]
             liquibaseTableName = matcher[0][1]
             liquibasePrimeKey = matcher[0][2]
             table = new Table()
             pk = new PrimaryKey( tableName: liquibaseTableName , constraintName : liquibasePrimeKey, columnNames :liquibaseColumnNames)
             table.getChanges() << pk
             tables << table
             return true
            
         }
         defaultMatch = (matcher[0][3] =~ /(?i)[ ]*FOREIGN KEY[ ]+\((.+)\)[ ]+REFERENCES[ ]+([a-zA-Z1-9_-]+)[ ]+[(](.+)\).*/)
         if (defaultMatch.matches()) {
             println(" alter Foreign " + matcher[0][1] + " key " + matcher[0][2] + " on " + defaultMatch[0][1])
             liquibaseBaseColumnNames = defaultMatch[0][1]
             liquibaseTableName = matcher[0][1]
             liquibaseConstraintName = matcher[0][2]
             liquibaseReferenceTableName = defaultMatch[0][2]
             liquibaseReferencedColumnNames = defaultMatch[0][3]
             table = new Table()
             pk = new ForeignKeys(constraintName: liquibaseConstraintName, baseTableName: liquibaseTableName , baseColumnNames :liquibaseBaseColumnNames, referencedTableName :liquibaseReferenceTableName, referencedColumnNames : liquibaseReferencedColumnNames )
             table.getChanges() << pk
             tables << table
             return true
         }
    }
    matcher = (line =~ /(?i)[ ]*ALTER TABLE ([^ ]+) ADD ([^ ]+) ([^ ;]+) NOT NULL.*/)
    if (matcher.matches()) {
         println "add line " + matcher[0][1] + ":" + matcher[0][2] + " NOT NULL"
         table = new Table( tableName: matcher[0][1] )
         table.getChanges() << new AddColumn( name : matcher[0][2], type : matcher[0][3].toUpperCase(), nullable : 'F')
         table.getModSqls() << new ModSql(dbms: "mysql", replace : "VARCHAR2", with : "VARCHAR")
         table.getModSqls() << new ModSql(dbms: "mysql", replace : "varchar2", with : "VARCHAR")
         table.getModSqls() << new ModSql(dbms: "mysql" ,replace : "NUMBER", with : "DECIMAL")
         tables << table
         return true              
    }
    matcher = (line =~ /(?i)[ ]*ALTER TABLE ([^ ]+) ADD ([^ ]+) ([^ ;]+).*/)
    if (matcher.matches()) {
         println "add line " + matcher[0][1] + ":" + matcher[0][2] + ":"  + matcher[0][3]
         table = new Table( tableName: matcher[0][1] )
         table.getChanges() << new AddColumn( name : matcher[0][2], type : matcher[0][3].toUpperCase())
         table.getModSqls() << new ModSql(dbms: "mysql", replace : "VARCHAR2", with : "VARCHAR")
         table.getModSqls() << new ModSql(dbms: "mysql", replace : "varchar2", with : "VARCHAR")
         table.getModSqls() << new ModSql(dbms: "mysql" ,replace : "NUMBER", with : "DECIMAL")
         tables << table
         return true              
    }


    return false
}
    
def checkUpdates(line, tables)
{
    matcher = (line =~ /(?i).*UPDATE ([^ ]+) SET[ ]+([^ ]+)[= ]+\'(.+)\' +where (.+\').*/)
     if (matcher.matches()) {
         println( matcher.getCount() + "cols " + matcher[0][1])
         println( matcher.getCount() + "vals " + matcher[0][2])
         println( matcher.getCount() + "cols " + matcher[0][3])
         println( matcher.getCount() + "vals " + matcher[0][4])
         if (matcher[0][3].isNumber()) {
             col =  new UpdateColumn( name : matcher[0][2], valueNumeric : matcher[0][3])
         } else {
             col =  new UpdateColumn( name : matcher[0][2], value : matcher[0][3])
         }
        
         table = new Table(tableName : matcher[0][1])
         table.setWhereClause(matcher[0][4])
         table.getChanges() << col
         tables << table
         return true
         
     }
     matcher = (line =~ /(?i).*UPDATE ([^ ]+) SET[ ]+([^ ]+)[= ]+\'(.+)\'.*/)
     if (matcher.matches()) {
         println( matcher.getCount() + "cols " + matcher[0][1])
         println( matcher.getCount() + "vals " + matcher[0][2])
         println( matcher.getCount() + "cols " + matcher[0][3])
         println( matcher.getCount() + "vals " + matcher[0][4])
         if (matcher[0][3].isNumber()) {
             col =  new UpdateColumn( name : matcher[0][2], valueNumeric : matcher[0][3])
         } else {
             col =  new UpdateColumn( name : matcher[0][2], value : matcher[0][3])
         }        
         table = new Table(tableName : matcher[0][1])
          table.getChanges() << col
          tables << table
         return true
         
     }
     return false
}

def checkDropTable(line, tables)
{
    matcher = (line =~ /(?i)^DROP TABLE ([a-zA-Z1-9_-]+).*/)
     if (matcher.matches()) {
        table = new Table( tableName : matcher[0][1])
        table.dropTable = true
        tables << table
        println (" Drop table " + matcher[0][1])
        return true
    }
    return false;
}

def createOutName(String infilename,String datePrefix, counter)
{
  outfilename = infilename.replaceAll(".sql", ".xml")  // Groovy extension to String. 
    // Replace found String with result of closure.
  return datePrefix + "-" + counter + "-" + outfilename

}



def tables =[]

def processLine(line) 
{
    //println "processLine " + line   
    if (checkInsertion(line, tables) == true) return
    if (checkDeletion(line, tables) == true) return
    if (checkUpdates(line, tables) == true) return
    if (checkAlters(line,tables) == true) return
    if (checkDropTable(line, tables) == true) return
     println "***Invalid line " + line
    
}


def processFile(infileName, outfileName,counter, jira, commentary, authorName)
{
    tables =[]
    rawlines = []
    
    def writer = new FileWriter(outfileName)
    new File(infileName).eachLine {
         line ->
        
         if (line.startsWith("--")) {
         } else if (line.trim() ==~ / */) {
         } else {
             rawlines << line.trim()
             if (line.trim() ==~ /.*;$/) {
                 println rawlines.join(" ").trim()
                 processLine( rawlines.join(" ").trim())
                 rawlines.clear()
             }
         }
     }
     def cct = new ChangelogCreateTable(author: authorName, identifier : (jira + "-" + counter + "-"), comments : commentary )
          cct.generate(writer, tables)       
 }
 // pass a directory or use the current directory
def jira = "5477"
def dateExecution
def commentary
def baseDirectory = "./sql/"
def folder = args?.size() ? args[0] : baseDirectory
println "reading files from directory '$folder'"
def basedir = new File(folder)
def counter = 1
def outFilename
// result files of the current run
files = basedir.listFiles().grep(~/.*sql$/)



def askQuestion(question) {
    swingBuilder = new SwingBuilder()
    lastPane = swingBuilder.optionPane()
    choice = lastPane.showInputDialog(  null,
          question,
          "DATA QUESTION",
          JOptionPane.QUESTION_MESSAGE)
                       
    
}

author = askQuestion("Enter the author")
if (author == null) exit()
jira = askQuestion("Enter the Jira")
if (jira == null) exit()
dateExecution = askQuestion("Enter the date of Execution")
if (dateExecution == null) exit()
commentary = askQuestion("Enter comments")

println "JIRA=" + jira + " date=" + dateExecution + " comment=" + commentary

for (currentFile in files) {

    outFilename = baseDirectory + createOutName(currentFile.name, (dateExecution + "-" + jira), counter)
    println "  processing $currentFile  " + outFilename
    processFile( (baseDirectory + currentFile.name ), outFilename, counter++, jira, commentary, author) 
                   
}



    
       