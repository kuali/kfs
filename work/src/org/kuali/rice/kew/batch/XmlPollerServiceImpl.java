/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.batch;

import org.kuali.rice.kew.service.KEWServiceLocator;

import java.io.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Utility class responsible for polling and ingesting XML data files
 * containing various forms of workflow engine data (e.g. document types
 * and rules).
 * Loaded files and problem files are placed into a subdirectory of a
 * configured 'loaded' and 'problem' directory, respectively.
 * "Problem-ness" is determined by inspecting a 'processed' flag on each <code>XmlDoc</code>
 * in each collection.  If not all <code>XmlDoc</code>s are marked 'processed' an
 * error is assumed, and the collection file (e.g. for a Zip, the Zip file) is moved
 * to the 'problem' directory.
 * As such, it is the <b><code>XmlIngesterService</code>'s responsibility</b> to mark
 * any unknown or otherwise innocuous non-failure non-processed files, as 'processed'.
 * A different mechanism can be developed if this proves to be a problem, but for now
 * it is simple enough for the <code>XmlIngesterService</code> to determine this.
 * @see org.kuali.rice.kew.batch.XmlPollerService
 * @see org.kuali.rice.kew.batch.XmlIngesterServiceImpl
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class XmlPollerServiceImpl implements XmlPollerService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(XmlPollerServiceImpl.class);
    private static final Format DIR_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");

    /**
     * Specifies the polling interval that should be used with this task.
     */
    private int pollIntervalSecs = 5 * 60; // default to 5 minutes
    /**
     * Specifies the initial delay the poller should wait before starting to poll
     */
    private int initialDelaySecs = 30; // default to 30 seconds
    /**
     * Location in which to find XML files to load.
     */
    private String xmlPendingLocation;
    /**
     * Location in which to place successfully loaded XML files.
     */
    private String xmlCompletedLocation;
    /**
     * Location in which to place XML files which have failed to load.
     */
    private String xmlProblemLocation;
    
    private String xmlParentDirectory;
    private static final String PENDING_MOVE_FAILED_ARCHIVE_FILE = "movesfailed";
    private static final String NEW_LINE = "\n";

    public void run() {
        // if(!directoriesWritable()){
        //     LOG.error("Error writing to xml data directories. Stopping xmlLoader ...");
        //     this.cancel();
        // }
        LOG.debug("checking for xml data files...");
        File[] files = getXmlPendingDir().listFiles();
        if (files == null || files.length == 0) {
        	return;
        }
        LOG.info("Found " + files.length + " files to ingest.");
        List<XmlDocCollection> collections = new ArrayList<XmlDocCollection>();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                collections.add(new DirectoryXmlDocCollection(file));
            } else if (file.getName().equals(PENDING_MOVE_FAILED_ARCHIVE_FILE))
            {
                // the movesfailed file...ignore this
                continue;
            } else if (file.getName().toLowerCase().endsWith(".zip"))
            {
                try
                {
                    collections.add(new ZipXmlDocCollection(file));
                } catch (IOException ioe)
                {
                    LOG.error("Unable to load file: " + file);
                }
            } else if (file.getName().endsWith(".xml"))
            {
                collections.add(new FileXmlDocCollection(file));
            } else
            {
                LOG.warn("Ignoring extraneous file in xml pending directory: " + file);
            }
        }

        // Cull any resources which were already processed and whose moves failed
        Iterator collectionsIt = collections.iterator();
        Collection<XmlDocCollection> culled = new ArrayList<XmlDocCollection>();
        while (collectionsIt.hasNext()) {
            XmlDocCollection container = (XmlDocCollection) collectionsIt.next();
            // if a move has already failed for this archive, ignore it
            if (inPendingMoveFailedArchive(container.getFile())) {
                LOG.info("Ignoring previously processed resource: " + container);
                culled.add(container);
            }
        }
        collections.removeAll(culled);

        if (collections.size() == 0) {
            LOG.debug("No valid new resources found to ingest");
            return;
        }

        Date LOAD_TIME = Calendar.getInstance().getTime();
        // synchronization around date format should not be an issue as this code is single-threaded
        File completeDir = new File(getXmlCompleteDir(), DIR_FORMAT.format(LOAD_TIME));
        File failedDir = new File(getXmlProblemDir(), DIR_FORMAT.format(LOAD_TIME));

        // now ingest the containers
        Collection failed = null;
        try {
            failed = KEWServiceLocator.getXmlIngesterService().ingest(collections);
        } catch (Exception e) {
            LOG.error("Error ingesting data", e);
            //throw new RuntimeException(e);
        }
    
        // now iterate through all containers again, and move containers to approprate dir
        LOG.info("Moving files...");
        collectionsIt = collections.iterator();
        while (collectionsIt.hasNext()) {
            XmlDocCollection container = (XmlDocCollection) collectionsIt.next();
            LOG.debug("container: " + container);
            try {
                // "close" the container
                // this only matters for ZipFiles for now
                container.close();
            } catch (IOException ioe) {
                LOG.warn("Error closing " + container, ioe);
            }
            if (failed.contains(container)) {
                // some docs must have failed, move the whole
                // container to the failed dir
                if (container.getFile() != null) {
                    LOG.error("Moving " + container.getFile() + " to problem dir.");
                    if ((!failedDir.isDirectory() && !failedDir.mkdirs())
                        || !moveFile(failedDir, container.getFile())) {
                        LOG.error("Could not move: " + container.getFile());
                        recordUnmovablePendingFile(container.getFile(), LOAD_TIME);         
                    }
                }
            } else {
                if (container.getFile() != null) {
                    LOG.info("Moving " + container.getFile() + " to loaded dir.");
                    if((!completeDir.isDirectory() && !completeDir.mkdirs())
                        || !moveFile(completeDir, container.getFile())){
                        LOG.error("Could not move: " + container.getFile());
                        recordUnmovablePendingFile(container.getFile(), LOAD_TIME);         
                    }
                }
            }
        }
    }

    private boolean inPendingMoveFailedArchive(File xmlDataFile){
        if (xmlDataFile == null) return false;
        BufferedReader inFile = null;
        File movesFailedFile = new File(getXmlPendingDir(), PENDING_MOVE_FAILED_ARCHIVE_FILE);
        if (!movesFailedFile.isFile()) return false;
        try {
            inFile = new BufferedReader(new FileReader(movesFailedFile));
            String line;
            
            while((line = inFile.readLine()) != null){
                String trimmedLine = line.trim();
                if(trimmedLine.equals(xmlDataFile.getName()) ||
                   trimmedLine.startsWith(xmlDataFile.getName() + "=")) { 
                    return true;
                }
            }
        } catch (IOException e){
            LOG.warn("Error reading file " + movesFailedFile);
            //TODO try reading the pending file or stop?
        } finally {
            if (inFile != null) try {
                inFile.close();
            } catch (Exception e) {
                LOG.warn("Error closing buffered reader for " + movesFailedFile);
            }
        }
      
        return false;
    }

    private boolean recordUnmovablePendingFile(File unMovablePendingFile, Date dateLoaded){
        boolean recorded = false;
        FileWriter archiveFile = null;
        try{
            archiveFile = new FileWriter(new File(getXmlPendingDir(), PENDING_MOVE_FAILED_ARCHIVE_FILE), true);  
            archiveFile.write(unMovablePendingFile.getName() + "=" + dateLoaded.getTime() + NEW_LINE);
            recorded = true;
        } catch (IOException e){
            LOG.error("Unable to record unmovable pending file " + unMovablePendingFile.getName() + "in the archive file " + PENDING_MOVE_FAILED_ARCHIVE_FILE);
        } finally {
            if (archiveFile != null) {
                try {
                    archiveFile.close();
                } catch (IOException ioe) {
                    LOG.error("Error closing unmovable pending file", ioe);
                }
            }
        }
        return recorded;       
    }

    private boolean moveFile(File toDirectory, File fileToMove){
        boolean moved = true;
        if (!fileToMove.renameTo(new File(toDirectory.getPath(), fileToMove.getName()))){
            LOG.error("Unable to move file " + fileToMove.getName() + " to directory " + toDirectory.getPath());
            moved = false;
        }
        return moved;
    }

    private File getXmlPendingDir() {
        return new File(getXmlPendingLocation());
    }

    private File getXmlCompleteDir() {
        return new File(getXmlCompletedLocation());
    }

    private File getXmlProblemDir() {
        return new File(getXmlProblemLocation());
    }

    public String getXmlCompletedLocation() {
        return xmlCompletedLocation;
    }

    public void setXmlCompletedLocation(String xmlCompletedLocation) {
        this.xmlCompletedLocation = xmlCompletedLocation;
    }

    public String getXmlPendingLocation() {
        return xmlPendingLocation;
    }

    /*public boolean validate(File uploadedFile) {
        XmlDataLoaderFileFilter filter = new XmlDataLoaderFileFilter();
        return filter.accept(uploadedFile);
    }*/

    public void setXmlPendingLocation(String xmlPendingLocation) {
        this.xmlPendingLocation = xmlPendingLocation;
    }

    public String getXmlProblemLocation() {
        return xmlProblemLocation;
    }

    public void setXmlProblemLocation(String xmlProblemLocation) {
        this.xmlProblemLocation = xmlProblemLocation;
    }
    public String getXmlParentDirectory() {
        return xmlParentDirectory;
    }
    public void setXmlParentDirectory(String xmlDataParentDirectory) {
        this.xmlParentDirectory = xmlDataParentDirectory;
    }

    /**
     * Sets the polling interval time in seconds
     * @param seconds the polling interval time in seconds
     */
    public void setPollIntervalSecs(int seconds) {
        this.pollIntervalSecs = seconds;
    }

    /**
     * Gets the polling interval time in seconds
     * @return the polling interval time in seconds
     */
    public int getPollIntervalSecs() {
        return this.pollIntervalSecs;
    }

    /**
     * Sets the initial delay time in seconds
     * @param seconds the initial delay time in seconds
     */
    public void setInitialDelaySecs(int seconds) {
        this.initialDelaySecs = seconds;
    }

    /**
     * Gets the initial delay time in seconds
     * @return the initial delay time in seconds
     */
    public int getInitialDelaySecs() {
        return this.initialDelaySecs;
    }
}
