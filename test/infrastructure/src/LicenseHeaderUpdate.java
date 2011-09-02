/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class LicenseHeaderUpdate {

    public static final String ECL_20_LICENSE_TEXT = "Copyright " + Calendar.getInstance().get(Calendar.YEAR) + " The Kuali Foundation.\n" + 
    		"\n" + 
    		"Licensed under the Educational Community License, Version 2.0 (the \"License\");\n" + 
    		"you may not use this file except in compliance with the License.\n" + 
    		"You may obtain a copy of the License at\n" + 
    		"\n" + 
    		"http://www.opensource.org/licenses/ecl2.php\n" + 
    		"\n" + 
    		"Unless required by applicable law or agreed to in writing, software\n" + 
    		"distributed under the License is distributed on an \"AS IS\" BASIS,\n" + 
    		"WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" + 
    		"See the License for the specific language governing permissions and\n" + 
    		"limitations under the License.";
    
    static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {        
        handleJavaStyleComments( args[0] );
        handleJSPStyleComments( args[0] );
        handleXMLStyleComments( args[0] );
        handlePropertyStyleComments( args[0] );
    }

    public static void handleJavaStyleComments( String baseDir ) throws Exception {
        IOFileFilter sourceFileFilter = FileFilterUtils.orFileFilter( 
                FileFilterUtils.suffixFileFilter("java"), 
                FileFilterUtils.suffixFileFilter("js") );
        sourceFileFilter = FileFilterUtils.orFileFilter( 
                sourceFileFilter, 
                FileFilterUtils.suffixFileFilter("css") );
        sourceFileFilter = FileFilterUtils.makeSVNAware(sourceFileFilter);
        sourceFileFilter = FileFilterUtils.makeFileOnly(sourceFileFilter);
        
        LicensableFileDirectoryWalker dw = new LicensableFileDirectoryWalker(sourceFileFilter, "/*", " * ", " */");
        Collection<String> results = dw.run( baseDir );
        System.out.println( results );
    }
    
    public static void handleJSPStyleComments( String baseDir ) throws Exception {
        IOFileFilter sourceFileFilter = FileFilterUtils.orFileFilter( 
                FileFilterUtils.suffixFileFilter("jsp"), 
                FileFilterUtils.suffixFileFilter("tag") );
        sourceFileFilter = FileFilterUtils.orFileFilter( 
                sourceFileFilter, 
                FileFilterUtils.suffixFileFilter("inc") );
        sourceFileFilter = FileFilterUtils.makeSVNAware(sourceFileFilter);
        sourceFileFilter = FileFilterUtils.makeFileOnly(sourceFileFilter);
        
        LicensableFileDirectoryWalker dw = new LicensableFileDirectoryWalker(sourceFileFilter, "<%--", "   - ", "--%>");
        Collection<String> results = dw.run( baseDir );
        System.out.println( results );
    }

    public static void handlePropertyStyleComments( String baseDir ) throws Exception {
        IOFileFilter sourceFileFilter = FileFilterUtils.suffixFileFilter("properties"); 
        sourceFileFilter = FileFilterUtils.makeSVNAware(sourceFileFilter);
        sourceFileFilter = FileFilterUtils.makeFileOnly(sourceFileFilter);
        
        LicensableFileDirectoryWalker dw = new LicensableFileDirectoryWalker(sourceFileFilter, "########################################", "# ", "########################################");
        Collection<String> results = dw.run( baseDir );
        System.out.println( results );
    }
    
    public static void handleXMLStyleComments( String baseDir ) throws Exception {
        IOFileFilter sourceFileFilter = FileFilterUtils.orFileFilter( 
                FileFilterUtils.suffixFileFilter("xml"), 
                FileFilterUtils.suffixFileFilter("jrxml") );
        sourceFileFilter = FileFilterUtils.orFileFilter( 
                sourceFileFilter, 
                FileFilterUtils.suffixFileFilter("html") );
        sourceFileFilter = FileFilterUtils.orFileFilter( 
                sourceFileFilter, 
                FileFilterUtils.suffixFileFilter("htm") );
        sourceFileFilter = FileFilterUtils.orFileFilter( 
                sourceFileFilter, 
                FileFilterUtils.suffixFileFilter("xsd") );
        sourceFileFilter = FileFilterUtils.makeSVNAware(sourceFileFilter);
        sourceFileFilter = FileFilterUtils.makeFileOnly(sourceFileFilter);
        
        LicensableFileDirectoryWalker dw = new LicensableFileDirectoryWalker(sourceFileFilter, "<!--", "   - ", " -->");
        Collection<String> results = dw.run( baseDir );
        System.out.println( results );
    }
    
    public static class LicensableFileDirectoryWalker extends DirectoryWalker {
        
        String firstLine;
        String lastLine;
        String linePrefix;
        
        public LicensableFileDirectoryWalker( IOFileFilter fileFilter, String firstLine, String linePrefix, String lastLine ) {
            super(HiddenFileFilter.VISIBLE,fileFilter,100);
            this.firstLine = firstLine;
            this.linePrefix = linePrefix;
            this.lastLine = lastLine;
        }
        @Override
        protected void handleDirectoryStart(File directory, int depth, @SuppressWarnings("rawtypes") Collection results) throws IOException {
            System.out.println( "Directory: " + directory.getAbsolutePath() );
        }
        @Override
        protected boolean handleDirectory(File directory, int depth, Collection results) throws IOException {
            if ( directory.getAbsolutePath().endsWith("WEB-INF"+File.separator+"classes") ) {
                return false;
            }
            if ( directory.getAbsolutePath().endsWith("test"+File.separator+"classes") ) {
                return false;
            }
            if ( directory.getAbsolutePath().endsWith("build"+File.separator+"tomcat") ) {
                return false;
            }
            if ( directory.getAbsolutePath().endsWith("build"+File.separator+"rice-datadictionary") ) {
                return false;
            }
            if ( directory.getAbsolutePath().endsWith("kr"+File.separator+"static"+File.separator+"help") ) {
                return false;
            }
            if ( directory.getName().equals("META-INF") ) {
                return false;
            }
            return true;
        }
        @Override
        protected void handleFile(File file, int depth, @SuppressWarnings("rawtypes") Collection results) throws IOException {
            System.out.println( "Handing File: " + file.getAbsolutePath() );
            BufferedReader r = new BufferedReader(new FileReader( file ) );
            String currentLine = null;
            String savedFirstLine = null;
            // check the initial line of the file
            String line1 = r.readLine();
            // if the file is empty, just skip
            if ( line1 == null ) {
                results.add("FILE SKIPPED - EMPTY: " + file.getAbsolutePath() );
                return;
            }
            if ( line1.startsWith("<?") ) { // special hack for XML files
                savedFirstLine = line1;
                line1 = r.readLine();
            }
            if ( line1.trim().equals(firstLine.trim()) ) {
                // if found, skip until find a line containing the lastLine
                while ( (currentLine = r.readLine()) != null ) {
                    // throw away the lines
                    if ( currentLine.trim().equals(lastLine.trim()) ) {
                        break;
                    }
                }
                if ( currentLine == null ) {
                    // we reached the end of the file before finding the end of the comment, ABORT!
                    System.err.println( "Unable to find end of existing header section, skipping file: " + file.getAbsolutePath() );
                    results.add("FILE SKIPPED - UNABLE TO FIND END OF HEADER: " + file.getAbsolutePath() );
                    return;
                }
            }
            File outputFile = new File( file.getAbsolutePath() + "-out" );
            BufferedWriter w = new BufferedWriter( new FileWriter( outputFile ) );
            if ( savedFirstLine != null ) {
                w.write( savedFirstLine );
                w.write( LINE_SEPARATOR );
            }
            // now, write the new header file
            w.write( firstLine );
            w.write( LINE_SEPARATOR );
            BufferedReader headerReader = new BufferedReader( new StringReader( ECL_20_LICENSE_TEXT ) );
            while ( (currentLine = headerReader.readLine()) != null ) {
                w.write( linePrefix ); 
                w.write( currentLine );
                w.write( LINE_SEPARATOR );
            }
            w.write( lastLine );
            w.write( LINE_SEPARATOR );
            headerReader.close();
            if ( !line1.trim().equals(firstLine.trim()) ) {
                w.write( line1 );
                w.write( LINE_SEPARATOR );
            }
            
            while ( (currentLine = r.readLine()) != null ) {
                w.write(currentLine);
                w.write(LINE_SEPARATOR);
            }
            // delete the original file and replace with the completed output file
            w.close();
            r.close();
            FileUtils.deleteQuietly(file);
            FileUtils.moveFile(outputFile, file);            
        }
        public Collection<String> run( String projectDir ) throws IOException {
            Collection<String> results = new ArrayList<String>();
            walk( new File( projectDir ), results );
            return results;
        }
    }
}
