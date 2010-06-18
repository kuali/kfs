/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.kuali.kfs.sys.context.Log4jConfigurer;
import org.kuali.rice.kew.batch.XmlPollerServiceImpl;

public class WorkflowImporter {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowImporter.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("ERROR: You must pass the base directory on the command line.");
            System.exit(-1);
        }
        try {
//            File pendingDir = new File( args[0], "pending" );
//            if ( !pendingDir.exists() ) {
//            	throw new IllegalArgumentException( "Pending directory does not exist! - " + pendingDir.getAbsolutePath() );
//            }
//            File completedDir = new File( args[0], "completed" );
//            if ( !completedDir.exists() ) {
//            	completedDir.mkdir();
//            }
//            File failedDir = new File( args[0], "failed" );
//            if ( !failedDir.exists() ) {
//            	failedDir.mkdir();
//            }
            Log4jConfigurer.configureLogging(false);
            SpringContextForWorkflowImporter.initializeApplicationContext();

            XmlPollerServiceImpl parser = new XmlPollerServiceImpl();
            
            File baseDir = new File( args[0] );
            File[] dirs = baseDir.listFiles( new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && !pathname.getName().startsWith(".");
                }
            });     
            Arrays.sort(dirs);
            
            for ( File dir : dirs ) {
                File pendingDir = new File( dir, "pending" );
                if ( !pendingDir.exists() ) {
                    pendingDir.mkdir();
                }
                File completedDir = new File( dir, "completed" );
                if ( !completedDir.exists() ) {
                    completedDir.mkdir();
                }
                File failedDir = new File( dir, "problem" );
                if ( !failedDir.exists() ) {
                    failedDir.mkdir();
                }
                
                File[] xmlFiles = dir.listFiles( new FileFilter() {
                    public boolean accept(File pathname) {
                        return pathname.isFile() && pathname.getName().endsWith( ".xml" );
                    }
                });
                
                Arrays.sort( xmlFiles );

                for ( File xmlFile : xmlFiles ) {
                    LOG.info("Copying to pending: " + xmlFile.getName());
                    copyFile( xmlFile, new File( pendingDir, xmlFile.getName() ) );
                }
                
                parser.setXmlPendingLocation( pendingDir.getAbsolutePath() );
                parser.setXmlCompletedLocation( completedDir.getAbsolutePath() );
                parser.setXmlProblemLocation( failedDir.getAbsolutePath() );

                LOG.info( "Reading XML files from     : " + pendingDir.getAbsolutePath() );
                LOG.info( "Completed Files will go to : " + completedDir.getAbsolutePath() );
                LOG.info( "Failed files wil go to     : " + failedDir.getAbsolutePath() );

                parser.run();
            }            
            
            SpringContextForWorkflowImporter.close();
            System.exit(0);
        }
        catch (Throwable t) {
            System.err.println("ERROR: Exception caught: ");
            t.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    private static void copyFile(File sourceFile, File destFile) throws Exception{
        InputStream in = new FileInputStream(sourceFile);

        OutputStream out = new FileOutputStream(destFile);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
