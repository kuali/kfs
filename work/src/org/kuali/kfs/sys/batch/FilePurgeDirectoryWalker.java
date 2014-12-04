/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
