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
package org.kuali.kfs.sys.context;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.batch.XmlPollerServiceImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class WorkflowImporter {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowImporter.class);

    private static final String DEFAULT_SPRING_BEANS_REPLACEMENT_VALUE = "${bootstrap.spring.file}";
    private static final String WEB_BOOTSTRAP_SPRING_FILE = "web.bootstrap.spring.file";

    private static ClassPathXmlApplicationContext context;

    public static void initializeKfs() {
        long startInit = System.currentTimeMillis();
        LOG.info("Initializing Kuali Rice Application...");

        String bootstrapSpringBeans = "";
//        if (!StringUtils.isBlank(System.getProperty(WEB_BOOTSTRAP_SPRING_FILE))) {
//            bootstrapSpringBeans = System.getProperty(WEB_BOOTSTRAP_SPRING_FILE);
//        }
        bootstrapSpringBeans = "kfs-startup.xml";

        Properties baseProps = new Properties();
        baseProps.putAll(System.getProperties());
        JAXBConfigImpl config = new JAXBConfigImpl(baseProps);
        ConfigContext.init(config);
        
        context = new ClassPathXmlApplicationContext(bootstrapSpringBeans);

        try {
            context.refresh();
        } catch (RuntimeException e) {
            LOG.error("problem during context.refresh()", e);

            throw e;
        }

        context.start();
        long endInit = System.currentTimeMillis();
        LOG.info("...Kuali Rice Application successfully initialized, startup took " + (endInit - startInit) + " ms.");
    }
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("ERROR: You must pass the base directory on the command line.");
            System.exit(-1);
        }
        Log4jConfigurer.configureLogging(true);
        try {
            LOG.info( "Initializing Web Context" );
            LOG.info( "Calling KualiInitializeListener.contextInitialized" );
            initializeKfs();
            LOG.info( "Completed KualiInitializeListener.contextInitialized" );
            
            // the super implementation above will handle the loading of Spring
            SpringContext.applicationContext = context;
//            LOG.info( "Loaded Spring Context from the following locations: " + Arrays.asList( context.getConfigLocations() ) );
            
            GlobalResourceLoader.logAllContents();

            XmlPollerServiceImpl parser = new XmlPollerServiceImpl();
            
            File baseDir = new File( args[0] );
            File[] dirs = baseDir.listFiles( new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && !pathname.getName().startsWith(".");
                }
            });     
            if ( dirs == null ) {
                LOG.error( "Unable to find any subdirectories under " + baseDir.getAbsolutePath() + " - ABORTING!" );
                System.err.println( "Unable to find any subdirectories under " + baseDir.getAbsolutePath() + " - ABORTING!" );
                System.exit(-1);
            }
            Arrays.sort(dirs);
            
            for ( File dir : dirs ) {
                LOG.info( "Processing Directory: " + dir.getAbsolutePath() );
                File[] xmlFiles = dir.listFiles( new FileFilter() {
                    public boolean accept(File pathname) {
                        return pathname.isFile() && pathname.getName().endsWith( ".xml" );
                    }
                });
                if ( xmlFiles.length == 0 ) {
                    LOG.info( "Directory was empty - skipping." );
                    continue;
                }
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
                LOG.info( "Failed files will go to    : " + failedDir.getAbsolutePath() );

                parser.run();
            }            
            
            SpringContext.close();
            System.exit(0);
        }
        catch (Throwable t) {
            System.err.println("ERROR: Exception caught: ");
            t.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
//    private static void copyFile(File sourceFile, File destFile) throws Exception{
//        InputStream in = new FileInputStream(sourceFile);
//
//        OutputStream out = new FileOutputStream(destFile);
//
//        byte[] buf = new byte[1024];
//        int len;
//        while ((len = in.read(buf)) > 0) {
//            out.write(buf, 0, len);
//        }
//        in.close();
//        out.close();
//    }
}
