/*
 * Copyright 2009 Arizona Board of Regents
 * 
 */
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.kuali.kfs.sys.batch.AbstractStep;

import org.kuali.rice.kew.batch.XmlIngesterService;
import org.kuali.rice.kew.batch.CompositeXmlDocCollection;
import org.kuali.rice.kew.batch.FileXmlDocCollection;
import org.kuali.rice.kew.batch.XmlDoc;
import org.kuali.rice.kew.batch.XmlDocCollection;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.web.UserLoginFilter;

import org.kuali.kfs.sys.KFSKeyConstants;
import static org.kuali.kfs.sys.batch.BufferedLogger.*;

/**
 * Main {@link Step} that is executed automatically during the start of the application. This
 * {@link Step} ingests XML automatically into the workflow at startup.
 * 
 * @author $Author$
 * @version $Revision$
 */
public class WorkflowXmlIngestionStep extends AbstractStep {
    
    private KualiConfigurationService kualiConfigurationService;
    private XmlIngesterService xmlIngesterService;
    
    /**
     *  
     *
     * @param jobName the name used for the job.
     * @return true to continue onto other steps or false to stop
     * @throws {@link InterruptedException} if there is an error or failure in the {@link Step}
     * @see AbstractStep#(String)
     */
    public boolean execute(String jobName, Date date) throws InterruptedException {
        boolean retval = false;
        String ingestionPath = getKualiConfigurationService().getPropertyString(KFSKeyConstants.WORKFLOW_DIRECTORY) 
            + File.separator + KFSKeyConstants.INGESTION_DIRECTORY;
        info("Ingesting path ", ingestionPath);
        File ingestionDirectory = new File(ingestionPath);

        if (!ingestionDirectory.exists()) {
            return retval;
        }
        
        try {
            getXmlIngesterService().ingest(getCollectionOfXmlToIngest(ingestionDirectory));
            cleanup(ingestionDirectory);
        }
        catch (Exception e) {
            InterruptedException throwback = new InterruptedException("There was an error trying to ingest from path " 
                                                                      + ingestionDirectory);
            throwback.initCause(e);
            throw throwback;
        }
        
        info("Finished ingesting");
        return retval;
    }

    /**
     * List all workflow XML files for ingestion.
     *
     * @param path to search for XML in without traversal
     * @return {@link Collection} of {@link File} instances
     */
    private Collection<File> listXmlFiles(File path) {
        Collection<File> retval = Arrays.asList(path.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.toLowerCase().endsWith(".xml")) {
                        info("Adding ", name, " to ingest");
                        return true;
                    }
                    return false;
                }
            }));
        return retval;
    }

    /**
     * Converts normal {@link File} to a {@link FileXmlDocCollection}
     *
     *
     * @param file to convert
     * @return FileXmlDocCollection 
     */
    private FileXmlDocCollection getXmlDocCollection(File file) {
        return new FileXmlDocCollection(file, file.getName());
    }
    
    /**
     * Takes a directory path and locates XML files in the path. Then, adds files to
     * a {@link CompositeXmlDocCollection} suitable for ingestion
     *
     * @param path to look for XML files in
     * @return a {@link CompositeXmlDocCollection} suitable for ingestion
     */
    private List getCollectionOfXmlToIngest(File path) {
        Collection<XmlDocCollection> xmlDocs = new LinkedList();
        for (File xmlFile : listXmlFiles(path)) {
            xmlDocs.add(getXmlDocCollection(xmlFile));
        }
        return Arrays.asList(new CompositeXmlDocCollection(xmlDocs));
    }
    
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
    
    public KualiConfigurationService getKualiConfigurationService() {
        return this.kualiConfigurationService;
    }

    public XmlIngesterService getXmlIngesterService() {
        return this.xmlIngesterService;
    }

    public void setXmlIngesterService(XmlIngesterService xmlIngesterService) {
        this.xmlIngesterService = xmlIngesterService;
    }

    private void cleanup(File path) {
        for (File xmlFile : listXmlFiles(path)) {
            try {
                xmlFile.delete();
            }
            catch (Exception e) {
                error("Cleanup after ingestion failed. ", 
                      xmlFile.getPath(), " cannot be deleted.");
                error(e.getClass().getName());
                error(e.getMessage());
            } 
        }
    }
}
