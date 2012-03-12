/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * A directory walker which finds files to purge; it's relatively simple, simply adding a file to
 * the given results if the IOFileMatcher has matched it
 */
public class FilePurgeDirectoryWalker extends DirectoryWalker {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(this.getClass());
    
    /**
     * Constructs a FilePurgeDirectoryWalker
     */
    public FilePurgeDirectoryWalker(IOFileFilter fileFilter) {
        super(fileFilter, fileFilter, -1);
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.FilePurgeDirectoryWalker#getFilesToPurge(java.lang.String, java.util.List)
     */
    public List<File> getFilesToPurge(String directory) {
        List<File> results = new ArrayList<File>();
        
        try {
            walk(new File(directory), results);
        }
        catch (IOException ioe) {
            throw new RuntimeException("Could not walk directory "+directory, ioe);
        }
        
        return results;
    }

    /**
     * @see org.apache.commons.io.DirectoryWalker#handleDirectory(java.io.File, int, java.util.Collection)
     */
    @Override
    protected boolean handleDirectory(File directory, int depth, Collection results) throws IOException {
        if (getLastSubDirectoryName(directory.getName()).startsWith(".")) return false; // don't follow hidden directories
        return true;
    }
    
    /**
     * Finds the last subdirectory name of the given directory name and returns it
     * @param directoryName a directory name with a sub directory
     * @return the last subdirectory name
     */
    protected String getLastSubDirectoryName(String directoryName) {
        final int lastIndex = directoryName.lastIndexOf(File.separator);
        if (lastIndex > -1) {
            return directoryName.substring(lastIndex+1);
        }
        return directoryName; // no directory separator...so just return the whole thing
    }

    /**
     * @see org.apache.commons.io.DirectoryWalker#handleDirectoryEnd(java.io.File, int, java.util.Collection)
     */
    @Override
    protected void handleDirectoryEnd(File directory, int depth, Collection results) throws IOException {
        LOG.debug("Leaving directory "+directory.getName());
        super.handleDirectoryEnd(directory, depth, results);
    }

    /**
     * @see org.apache.commons.io.DirectoryWalker#handleDirectoryStart(java.io.File, int, java.util.Collection)
     */
    @Override
    protected void handleDirectoryStart(File directory, int depth, Collection results) throws IOException {
        LOG.debug("Entering directory "+directory.getName());
        super.handleDirectoryStart(directory, depth, results);
    }

    /**
     * @see org.apache.commons.io.DirectoryWalker#handleEnd(java.util.Collection)
     */
    @Override
    protected void handleEnd(Collection results) throws IOException {
        LOG.debug("ending process");
        super.handleEnd(results);
    }

    /**
     * @see org.apache.commons.io.DirectoryWalker#handleFile(java.io.File, int, java.util.Collection)
     */
    @Override
    protected void handleFile(File file, int depth, Collection results) throws IOException {
        results.add(file);
    }

    /**
     * @see org.apache.commons.io.DirectoryWalker#handleStart(java.io.File, java.util.Collection)
     */
    @Override
    protected void handleStart(File startDirectory, Collection results) throws IOException {
        LOG.debug("starting process");
        super.handleStart(startDirectory, results);
    }
}
