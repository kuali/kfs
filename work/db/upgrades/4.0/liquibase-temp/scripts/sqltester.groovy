import groovy.sql.Sql
import groovy.swing.SwingBuilder
import javax.swing.*

def askQuestion(question) {
    swingBuilder = new SwingBuilder()
    lastPane = swingBuilder.optionPane()
    choice = lastPane.showInputDialog(  null,
          question,
          "DATA QUESTION",
          JOptionPane.QUESTION_MESSAGE)
                       
    
}
    
def askChoice( options )
{
    swingBuilder = new SwingBuilder()
    lastPane = swingBuilder.optionPane()
    choice = lastPane.showInputDialog(     null,
                     'Choose an inputfile',
                     'Last chance : Groovy starts in ',
                     JOptionPane.QUESTION_MESSAGE,
                     null,
                     options as Object[],
                     lastPane.getInputValue())  
                                          
}

def createOutName(baseDirectory, infilename,jira)
{
  //outfilename = infilename.replaceAll(".sql", ".xml")  // Groovy extension to String. 
    // Replace found String with result of closure.
    if (jira != null)  return baseDirectory + jira.trim() + "-" + infilename
    return baseDirectory + "out-" + infilename
}



def processFile(fileName, outFileName, sql, mysqlflag, jira)
{
    comments = [];
    sqlcommand = []
    String execCommandStr
    counter = 0
    errorCounter = 0
    foundset = false
    writer = new FileWriter( new File(outFileName))
    fileName.eachLine {
         line ->
         if (line.startsWith("--") == true) {
             comments << line  // copy comment
         } else if (line ==~ /.*DATABASECHANGELOG.*/ ) {
                     //do nothing drop line  
         } else if (line ==~ / */) {
                 // do nothing drop line  blank space
         } else {
                 sqlcommand << line
         }
         
         
         if ( line ==~ /.*;.*/) {
            // writer << (sqlcommand.join("") + "\n")
             
             execCommandStr = sqlcommand.join("")
             if (mysqlflag == false)  execCommandStr = (execCommandStr =~ /;/).replaceAll("")
             
             if ((sqlcommand.size() > 0) && (!( execCommandStr ==~ / */))) {
                 counter++
                 try {
                    
                    
                     if (jira != null) {               
                         if ((comments.size() > 0) && (comments[0].find("Changeset") != null)) {
                             foundset = (comments[0].find( ":" + jira ) != null)
                         }
                         if (foundset == true) sql.execute(  execCommandStr )
                     } else {
                         sql.execute(  execCommandStr )
                     }
                     
                     if ( execCommandStr.startsWith("(?i)INSERT INTO")) {
                         println "INSERTING DATA " +  execCommandStr
                         writer << "*** INSERTING DATA " +  execCommandStr + "\n"
                     }
                 } catch (Exception e) {
                     String errorString = e.toString().trim()
                     //println errorString
                     // mysql stuff
                     if (errorString ==~/.*Duplicate .*/) {
                     } else if (errorString ==~ /.*already exists.*/) {
                     } else if (errorString ==~ /.*Multiple primary key defined.*/) {
                     } else if (errorString ==~ /.*unique constraint.*/) { 
                     } else if (errorString ==~ /.*already used.*/) {  
                     } else if (errorString ==~ /.*only one primary.*/) {                      
                     } else {
                         errorCounter++
                         writer << (comments.join("\n") + "\n")
                         println( "*" +  execCommandStr + "*")
                         println("Exception " + e)
                         writer << (execCommandStr + "\n")
                         writer << ("-- **" + e + "\n\n")
                    }
                 }
                 if (foundset == true) {
                     writer << (comments.join("\n") + "\n")
                     println execCommandStr
                     writer << (execCommandStr + "\n\n")
                 }

                 comments.clear()
                 sqlcommand.clear()
            }
         }
    }
    if (jira == null) writer << "lines processed " + counter + " errors " + errorCounter + "\n"
    println "lines processed " + counter + " errors " + errorCounter + "\n"
    writer.close()
 }     
          
 

 // pass a directory or use the current directory
def baseDirectory = "./testDir/"
def baseDir = new File(baseDirectory) 
def outDirectory = "./"
def mysqlflag = true
def outFileName
def sql
def jira

jira = askQuestion("What is the Jira")

if (mysqlflag) {
    this.class.classLoader.rootLoader.addURL( new URL( "file:///java/drivers/mysql-connector-java-5.0.5-bin.jar"))
    // local database
    sql= Sql.newInstance("jdbc:mysql://localhost:3306/kuldemo", "kuldemo", "kuldemo", "com.mysql.jdbc.Driver")
} else {
    this.class.classLoader.rootLoader.addURL( new URL( "/java/drivers/ojbc14.jar"))
    // kuldev database
    sql= Sql.newInstance("jdbc:oracle:thin:@esdbk02.uits.indiana.edu:1521:KUALI", "kuldev", "dev174kul", "oracle.jdbc.OracleDriver")
}

// result files of the current run
files = baseDir.listFiles().grep(~/.*\.sql$/)
def options = []
files.each {
    file ->
    if (file.isDirectory() == false) options << file
}
inFileName = askChoice( options )

choice = askChoice(["MySQL", "Oracle"])
if (choice == 1) mysqlflag = false


outFileName = createOutName(baseDirectory, inFileName.name, jira)
println inFileName.name + "->" + outFileName + " mySql=" + mysqlflag + " jira=" + jira


processFile( inFileName ,  outFileName, sql, mysqlflag,jira) 
                   
if (jira != null) {
    execCommandStr = "DELETE FROM DATABASECHANGELOG WHERE ID LIKE\'" + jira + "%\'"
    println execCommandStr
    sql.execute(  execCommandStr )
}
println "DONE"

    
       