

def processFile(infile, fileName)
{
    writer = new FileWriter( new File( fileName))
    infile.eachLine {
         line ->
         if (line.find("DATABASECHANGELOG") == null) writer << line + "\n"
    }
    writer.close()
 }     
          
 

 // pass a directory or use the current directory
def baseFile = "infile.sql"
def currentFile = args?.size() ? args[0] : baseFile
def infile = new File(currentFile)
def counter = 1
def outFileName =  infile.name + ".strip"
// result files of the current run


println "  processing " + infile.name
processFile(  infile,  outFileName) 
                   

println "DONE"

    
       