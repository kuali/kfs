

def processFile(infile, outDirectory)
{
    writer = new FileWriter( new File(outDirectory + infile.name))
    infile.eachLine {
         line ->
         newline = (line =~ /failOnError=\'false\'/).replaceAll("failOnError=\'true\'")
         writer << line
    }
    writer.close()
 }     
          
 

def baseDirectory = "./sql/"
def outDirectory = "./xml/"

def folder = args?.size() ? args[0] : baseDirectory
println "reading files from directory '$folder'"
def basedir = new File(folder)
def counter = 1

// result files of the current run
files = basedir.listFiles().grep(~/.*sql$/)

for (currentFile in files) {
    processFile( currentFile, outDirectory ) 
                   
}