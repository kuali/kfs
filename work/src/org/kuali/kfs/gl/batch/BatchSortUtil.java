/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.gl.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * This class...
 */
public class BatchSortUtil {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchSortUtil.class);

    private static File tempDir;

    private static File getTempDirectory() {
        if ( tempDir == null ) {
            tempDir = new File( SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.TEMP_DIRECTORY_KEY) );
        }
        return tempDir;
    }

    static public void sortTextFileWithFields(String inputFileName, String outputFileName, @SuppressWarnings("rawtypes") Comparator comparator){
        // create a directory for the interim files
        String tempSortDirName = UUID.randomUUID().toString();
        File tempSortDir = new File( getTempDirectory(), tempSortDirName );
        // ensure the directory is empty
        FileUtils.deleteQuietly(tempSortDir);
        try {
            FileUtils.forceMkdir(tempSortDir);
        } catch (IOException ex) {
            LOG.fatal( "Unable to create temporary sort directory", ex );
            throw new RuntimeException( "Unable to create temporary sort directory", ex );
        }

        //LOG.info("Sorting input file " + inputFileName + " into temp directory " + tempSortDir);
        int numFiles = sortToTempFiles( inputFileName, tempSortDir, comparator );
        //LOG.info("Merging " + numFiles + " temp files from temp directory into output file " + outputFileName);

        // now that the sort is complete - merge the sorted files
        mergeFiles(tempSortDir, numFiles, outputFileName, comparator);

        File sortedFile = new File(outputFileName);
        /*
        if (sortedFile.exists()) {
            LOG.info("Successfully merged input file " + inputFileName + " to output file " + outputFileName);
        }
        */

        // remove the temporary sort directory
        FileUtils.deleteQuietly(tempSortDir);

        /*
        LOG.info("Successfully deleted temp directory " + tempSortDir);
        if (sortedFile.canRead()) {
            LOG.info("Sorted file " + outputFileName + " is readable upon completion of flat file sorting.");
        }
        else {
            LOG.error("Sorted file " + outputFileName + " can't be read upon completion of flat file sorting.");
        }
        */
    }

    static int linesPerFile = 10000;

    /* Code below derived from code originally written by Sammy Larbi and
     * downloaded from www.codeodor.com.
     *
     * http://www.codeodor.com/index.cfm/2007/5/14/Re-Sorting-really-BIG-files---the-Java-source-code/1208
     */
    private static int sortToTempFiles(String inputFileName, File tempSortDir, Comparator<String> comparator) {
        BufferedReader inputFile;
         try {
             inputFile = new BufferedReader(new FileReader(inputFileName));
             //LOG.info("Successfully opened input file " + inputFileName);
         } catch ( FileNotFoundException ex ) {
             LOG.fatal( "Unable to find input file: " + inputFileName, ex );
             throw new RuntimeException( "Unable to find input file: " + inputFileName, ex );
         }
         try {
             String line = "";
             ArrayList<String> batchLines = new ArrayList<String>( linesPerFile );

             int numFiles = 0;
             while ( line !=null ) {
                 // get 10k rows
                 for ( int i = 0; i < linesPerFile; i++ ) {
                     line = inputFile.readLine();
                     if ( line != null ) {
                         batchLines.add(line);
                     }
                 }
                 // sort the rows
//                 batchLines = mergeSort(batchLines, comparator);
                 Collections.sort(batchLines, comparator);

                 // write to disk
                 BufferedWriter bw = new BufferedWriter(new FileWriter( new File( tempSortDir,  "chunk_" + numFiles ) ));
                 for( int i = 0; i < batchLines.size(); i++) {
                     bw.append(batchLines.get(i)).append('\n');
                     //LOG.info("Writing temp sort file chunk_" + numFiles + " to tempSortDir " + tempSortDir);
                 }
                 bw.close();
                 //LOG.info("Closed temp sort file chunk_" + numFiles);
                 numFiles++;
                 batchLines.clear(); // empty the array for the next pass
             }
             inputFile.close();
             //LOG.info("Successfully closed input file " + inputFileName);
             return numFiles;
         } catch (Exception ex) {
             LOG.fatal( "Exception processing sort to temp files.", ex );
             throw new RuntimeException( ex );
         }
    }

    private static void mergeFiles(File tempSortDir, int numFiles, String outputFileName, Comparator<String> comparator ) {
        try {
            ArrayList<FileReader> mergefr = new ArrayList<FileReader>( numFiles );
            ArrayList<BufferedReader> mergefbr = new ArrayList<BufferedReader>( numFiles );
            // temp buffer for writing - contains the minimum record from each file
            ArrayList<String> fileRows = new ArrayList<String>( numFiles );

            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName));
            //LOG.info("Successfully opened output file " + outputFileName);

            boolean someFileStillHasRows = false;

            // Iterate over all the files, getting the first line in each file
            for ( int i = 0; i < numFiles; i++) {
                // open a file reader for each file
                mergefr.add(new FileReader(new File( tempSortDir, "chunk_"+i) ) );
                mergefbr.add(new BufferedReader(mergefr.get(i)));

                // get the first row
                String line = mergefbr.get(i).readLine();
                if (line != null) {
                    fileRows.add(line);
                    someFileStillHasRows = true;
                } else  {
                    fileRows.add(null);
                }
            }

            while (someFileStillHasRows) {
                String min = null;
                int minIndex = 0; // index of the file with the minimum record

                // init for later compare - assume the first file has the minimum
                String line = fileRows.get(0);
                if (line!=null) {
                    min = line;
                    minIndex = 0;
                } else {
                    min = null;
                    minIndex = -1;
                }

                // determine the minimum record of the top lines of each file
                // check which one is min
                for( int i = 1; i < fileRows.size(); i++ ) {
                    line = fileRows.get(i);
                    if ( line != null ) {
                        if ( min != null ) {
                            if( comparator.compare(line, min) < 0 ) {
                                minIndex = i;
                                min = line;
                            }
                        } else {
                            min = line;
                            minIndex = i;
                        }
                    }
                }

                if (minIndex < 0) {
                    someFileStillHasRows=false;
                } else {
                    // write to the sorted file
                    bw.append(fileRows.get(minIndex)).append('\n');

                    // get another row from the file that had the min
                    line = mergefbr.get(minIndex).readLine();
                    if (line != null) {
                        fileRows.set(minIndex,line);
                    } else { // file is out of rows, set to null so it is ignored
                        fileRows.set(minIndex,null);
                    }
                }
                // check if one still has rows
                for( int i = 0; i < fileRows.size(); i++) {
                    someFileStillHasRows = false;
                    if(fileRows.get(i)!=null)  {
                        if (minIndex < 0) {
                            throw new RuntimeException( "minIndex < 0 and row found in chunk file " + i + " : " + fileRows.get(i) );
                        }
                        someFileStillHasRows = true;
                        break;
                    }
                }

                // check the actual files one more time
                if (!someFileStillHasRows) {
                    //write the last one not covered above
                    for(int i=0; i<fileRows.size(); i++) {
                        if (fileRows.get(i) == null) {
                            line = mergefbr.get(i).readLine();
                            if (line!=null) {
                                someFileStillHasRows=true;
                                fileRows.set(i,line);
                            }
                        }
                    }
                }
            }

            // close all the files
            bw.close();
            //LOG.info("Successfully closed output file " + outputFileName);

            for(BufferedReader br : mergefbr ) {
                br.close();
            }
            for(FileReader fr : mergefr ) {
                fr.close();
            }
        } catch (Exception ex) {
            LOG.error( "Exception merging the sorted files", ex );
            throw new RuntimeException( "Exception merging the sorted files", ex );
        }
   }

}
