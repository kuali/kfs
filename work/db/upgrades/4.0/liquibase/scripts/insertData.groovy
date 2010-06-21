import groovy.swing.SwingBuilder
import javax.swing.*

//-----------------------------------------------------------------------------------------
class Table {
  Map attributes = [:]
  Boolean failOnErr = true
  List changes = []
 
  String whereClause
  List modSqls = []
  Table(Map attribs) {
    attributes = attribs
  }
  
  void modification(xml) {
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

//-----------------------------------------------------------------------------------------
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

//-----------------------------------------------------------------------------------------
class DropTable implements LiquibaseCommonTableInterface {
    void modification( table, xml ) {
         xml.dropTable( table.getAttributes() )
    }
}

class CreateTable implements LiquibaseCommonTableInterface {
  Map attributes = [:]
  Map constraintsAttributes = [:]

 CreateTable(Map attribs) {
    attributes = attribs.findAll{it.key in ['name', 'type', 'defaultValue']}
    constraintsAttributes = attribs.findAll{it.key in ['nullable', 'primaryKey']}
  }
  void modification(table, xml) {
      xml.createTable( table.getAttributes() ) {
          table.getChanges().each { change ->
              xml.column(change.getAttributes() ) {
                  if(change.getConstraintsAttributes()) {
                      xml.constraints(change.getConstraintsAttributes())
                  }
              }
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

class DropColumn implements LiquibaseCommonTableInterface {
    Map attributes = [:]
    DropColumn(Map attribs) {
        attributes = attribs
    }
    void modification( table, xml) {
        xml.dropColumn( this.getAttributes())
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

class SequenceCreate  implements LiquibaseCommonTableInterface {
  Map attributes = [:]
  Map constraintsAttributes = [:]
  SequenceCreate(Map attribs) {
    attributes = attribs.findAll{it.key in ['incrementBy', 'minValue', 'maxValue', 'ordered','startValue']}
    constraintsAttributes = attribs.findAll{it.key in ['nullable']}
  }

  void modification(table, xml) {
      if (table.getAttributes().find("dbms") == "oracle") {
        xml.createSequence(table.attributes['sequenceName'] ) {
            table.getChanges().each { change ->
                 xml.column(change.getAttributes()) {
                     if(change.getConstraintsAttributes()) {
                         xml.constraints(change.getConstraintsAttributes())
                     }
                 }
             }
        }
    } else {
        xml.createTable( tableName :  table.attributes['sequenceName'] ) {
            xml.column(name : "id", type :"bigint(19)", autoIncrement : true) {
                xml.constraints(primaryKey : true, primaryKeyName: "id", nullable : false)
            }
        }
        xml.sql("ALTER TABLE " +  table.attributes['sequenceName'] + " auto_increment=" + this.attributes['incrementBy'] )
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

//-----------------------------------------------------------------------------------------
//     def cct = new ChangelogCreateTable(author: authorName, identifier : (jira + "-" + counter + "-"), comments : commentary )

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
            if (table.getAttributes().find{"dbms"} != null) {
                changeSet(author: author, id : (identifier << changesetId++), failOnError : table.getFailOnErr(), dbms : table.attributes["dbms"]) {              
                    comment(comments)           
                    table.modification(xml)
                }
            } else {
                changeSet(author: author, id : (identifier << changesetId++), failOnError : table.getFailOnErr()) {              
                    comment(comments)           
                    table.modification(xml)
                }
            }
            println("total records = " + (changesetId - 1)  )
        }
    }
  }
}
//-----------------------------------------------------------------------------------------
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
         
     matcher = (line =~ /(?i)[ ]*ALTER TABLE ([a-zA-Z1-9_-]+)[ ]+DROP COLUMN[ ]+([a-zA-Z0-9_-]+).*/)
     if (matcher.matches()) {
         //println( matcher.getCount() + "occurences " + matcher[0][1])    
         println(" Drop Column " + matcher[0][1] + "  " + matcher[0][2])
         table = new Table()
         table.setFailOnErr(false)
         table.getChanges() << new DropColumn(tableName: matcher[0][1], columnName : matcher[0][2])
         tables << table
         return true
     }
         
         
     matcher = (line =~ /(?i)[ ]*ALTER TABLE ([a-zA-Z1-9_-]+)[ ]+MODIFY[ \(]*([a-zA-Z0-9_-]+)[ ]+([^ "']+\))[ ]*NOT NULL.*;/)
     if (matcher.matches()) {
         println(" alter Modify " + matcher[0][1] + " key " + matcher[0][2] + " on " + matcher[0][3].toUpperCase()) 
         liquibaseTableName = matcher[0][1]        
         table = new Table(tableName: liquibaseTableName)
         tables << table
         println "modify " + matcher[0][4]
         if (matcher[0][4] != null) {
              table.getChanges() << new AlterColumn( name : matcher[0][2], type : matcher[0][3].toUpperCase(), defaultValue : matcher[0][4],  nullable : false)
         } else {
             table.getChanges() << new AlterColumn( name : matcher[0][2], type : matcher[0][3].toUpperCase(),  nullable : false)
         }
         table.getModSqls() << new ModSql(dbms: "mysql", replace : "VARCHAR2", with : "VARCHAR")
         table.getModSqls() << new ModSql(dbms: "mysql" ,replace : "NUMBER", with : "DECIMAL")
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
         table.getChanges() << new AddColumn( name : matcher[0][2], type : matcher[0][3].toUpperCase(), nullable : false)
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
    matcher = (line =~ /(?i).*UPDATE ([^ ]+) SET[ ]+(.+)[ ]+where (.+\').*/)
     if (matcher.matches()) {
         println( matcher.getCount() + "table " + matcher[0][1])
         println( matcher.getCount() + "cols " + matcher[0][2])
         println( matcher.getCount() + "where " + matcher[0][3])
         table = new Table(tableName : matcher[0][1])
         table.setWhereClause(matcher[0][3])
         List columnPairs = matcher[0][2].tokenize(',')     
         columnPairs.each { columnPair ->
             columnMatch = (columnPair=~/(?i)([^= ]+)[= ]+\'(.+)\'.*/)
             if (columnMatch.matches()) {
                 if (columnMatch[0][2].isNumber()) {
                     col = new UpdateColumn(name : columnMatch[0][1], valueNumeric : columnMatch[0][2])
                 } else {
                     col = new UpdateColumn(name : columnMatch[0][1], value : columnMatch[0][2])
                 }
                 table.getChanges() << col
             } else {
                 println "*** Invalid column match " + columnPair
             }
         }
          
         tables << table
         return true
         
     }
     matcher = (line =~ /(?i).*UPDATE ([^ ]+) SET[ ]+([^ ]+)[= ]+\'(.+)\'.*/)
     if (matcher.matches()) {
         println( matcher.getCount() + "table " + matcher[0][1])
         println( matcher.getCount() + "col " + matcher[0][2])
         println( matcher.getCount() + "val " + matcher[0][3])
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
        table.setFailOnErr(false)
        table.getChanges() << new DropTable()
        tables << table
        println (" Drop table " + matcher[0][1])
        return true
    }
    return false;
}

def checkCreateTableLines(line, table)
{
        matcher = (line =~ /(?i)^CREATE TABLE ([a-zA-Z1-9_-]+).*/)
        if (matcher.matches() == true) return
     
        matcher = (line =~ /(?i)^[^a-zA-Z]*([a-zA-Z0-9_-]+)[ \t]+([^ ]+\(.+\))(.*)/)  //  <name>  <type>(<size>)
        if (matcher.matches()) {
             println( matcher.getCount() + "cols " + matcher[0][1] + "val " + matcher[0][2])
             liquibaseColumnName = matcher[0][1]
             liquibaseColumnType = matcher[0][2]
             liquibaseColumnNotNull = !(matcher[0][3] ==~ /(?i).*NOT NULL.*/)
             liquibaseDefaultVal = false
             defaultMatch = (matcher[0][3] =~ /(?i).*DEFAULT[ ']+([^ ']*).*/)
             if (defaultMatch.matches()) {
                 liquibaseDefaultValue = defaultMatch[0][1]
                 println "default " + defaultMatch[0][1]
                 table.changes << new CreateTable(name: liquibaseColumnName, type: liquibaseColumnType, defaultValue : liquibaseDefaultValue, nullable: liquibaseColumnNotNull )
             } else {
                 table.changes << new CreateTable(name: liquibaseColumnName, type: liquibaseColumnType, nullable: liquibaseColumnNotNull )
             }
             return
    
        }
        matcher = (line =~ /(?i)^[^a-zA-Z]*([a-zA-Z0-9_-]+)[ \t]+([^ ,]+)(.*)/)  
        if (matcher.matches()) {
            println( matcher.getCount() + "cols " + matcher[0][1] + "val " + matcher[0][2])
             liquibaseColumnName = matcher[0][1]
             liquibaseColumnType = matcher[0][2]
             liquibaseColumnNotNull = !(matcher[0][3] ==~ /(?i).*NOT NULL.*/)
             liquibaseDefaultVal = false
             defaultMatch = (matcher[0][3] =~ /(?i).*DEFAULT[ ']+([^ ']*).*/)
         
             if (defaultMatch.matches()) {
                 liquibaseDefaultValue = defaultMatch[0][1]
                 println "default " + defaultMatch[0][1]
                 table.changes << new CreateTable(name: liquibaseColumnName, type: liquibaseColumnType, defaultValue : liquibaseDefaultValue, nullable: liquibaseColumnNotNull )
             } else {
                 table.changes << new CreateTable(name: liquibaseColumnName, type: liquibaseColumnType, nullable: liquibaseColumnNotNull )
             }
             return
         }
         println "*** cannot decode " + line
}

def checkCreateTable(rawlines, tables)
{
    matcher = (rawlines[0] =~ /(?i)^CREATE TABLE ([a-zA-Z1-9_-]+).*/)
     if (matcher.matches() == false) return false
     
     println "create table " + matcher[0][1]
     table = new Table( tableName : matcher[0][1])
     tables << table
     rawlines.each() { line ->
         matcher = (line =~ /(?i)[^a-zA-Z]*PRIMARY KEY[ ]+\((.+)\).*/)
        if (matcher.matches()) {       
            table.changes.each() { change ->
                if (matcher[0][1].find(change.attributes["name"]) != null) {
                    println "primary key match " + matcher[0][1]
                    change.getConstraintsAttributes().put("primaryKey" , true) // <<  [primaryKey  : matcher[0][1]]
                }
           }  
        } else {
            checkCreateTableLines(line, table)
        }
    }
    table.getModSqls() << new ModSql(dbms: "mysql", replace : "VARCHAR2", with : "VARCHAR")
    table.getModSqls() << new ModSql(dbms: "mysql", replace : "NUMBER", with : "DECIMAL")
    return true
}


def checkSequence(rawlines, tables)
{
    matcher = (rawlines[0] =~ /^[ ]*CREATE[ ]+SEQUENCE[ ]+([a-zA-Z1-9_-]+).*/)
    if (matcher.matches() == false) return false
     println "create Sequence " + matcher[0][1]
     table = new Table( sequenceName : matcher[0][1], dbms: oracle)
     tables << table
     sequenceCreate = new SequenceCreate()
     table.changes << sequenceCreate
     rawlines.each() { line ->
         checkSequenceColumns( line, sequenceCreate)
     }

     table = new Table( sequenceName : matcher[0][1], dbms: mysql)
     tables << table
     sequenceCreate = new SequenceCreate()
     table.changes << sequenceCreate
     rawlines.each() { line ->
        checkSequenceColumns( line, sequenceCreate)        
     }
}

def checkSequenceColumns(line, sequenceCreate)
{
    line = line.replaceFirst( /;/,"")
    tokens = line.tokenize()
     if (tokens[0] == "MINVALUE") {
       sequenceCreate.getAttributes().put( "minValue" ,tokens[1])
    } else if (tokens[0] == "START") {
       sequenceCreate.getAttributes().put( "startValue" , tokens[2])
    } else if (tokens[0] == "INCREMENT") {
         sequenceCreate.getAttributes().put( "incrementBy" , tokens[2])
    } else if (tokens[0] == "NOCACHE") {
       // return new Column(nocache : "")
    } else if (tokens[0] == "ORDER") {
        if (tokens[1] == "NOCYCLE" ) {
             sequenceCreate.getAttributes().put("ordered" , "true")
        } else {
             sequenceCreate.getAttributes().put( "ordered" , "false")
        }
    }
}
//-----------------------------------------------------------------------------------------
def createOutName(String infilename,String datePrefix, counter)
{
  outfilename = infilename.replaceAll(".sql", ".xml")  // Groovy extension to String. 
    // Replace found String with result of closure.
  return datePrefix + "-" + counter + "-" + outfilename

}



def tables =[]

def processLine(rawlines) 
{
    //println "processLine " + line   
    if (checkInsertion(rawlines.join(" "), tables) == true) return
    if (checkDeletion(rawlines.join(" "), tables) == true) return
    if (checkUpdates(rawlines.join(" "), tables) == true) return
    if (checkAlters(rawlines.join(" "),tables) == true) return
    if (checkDropTable(rawlines.join(" "), tables) == true) return
    if (checkCreateTable(rawlines, tables) == true) return
    if (checkSequence(rawlines, tables) == true) return
     println "***Invalid line " + rawlines.join(" ")
    
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
                 println rawlines.join(" ")
                 processLine( rawlines)
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



    
       