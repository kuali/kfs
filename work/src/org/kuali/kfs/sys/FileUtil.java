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
package org.kuali.kfs.sys;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

/**
 * This class provides a set of facilities that can be used to work with files
 */
public class FileUtil {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FileUtil.class);

    private static Set<String> createdDirectory = new TreeSet<String>();
    
    /**
     * In directory looks for a pattern matching filenameFilter and returns the filename with the highest lastModified()
     * @param directory
     * @param filenameFilter to filter filenames in batchFileDirectoryName for
     * @return File with highest lastModified()
     */
    public static File getNewestFile(File directory, FilenameFilter filenameFilter) {
        File newestFile = null;
        
        File[] directoryListing = directory.listFiles(filenameFilter);
        if (directoryListing == null || directoryListing.length == 0) {
            return null;
        } else {
            for (int i = 0; i < directoryListing.length; i++) {
                File file = directoryListing[i];
                if (newestFile == null) {
                    newestFile = file;
                } else {
                    if (newestFile.lastModified() < file.lastModified()){
                        newestFile = file;                        
                    }
                }
            }
        }
        
        return newestFile;
    }
    
    /**
     * Check and create (if not exists) each of the directory path from param
     * This is for creating single directory
     * 
     * @param directoryPath
     */
    public static void createDirectory(final String directoryPath) {
        createDirectories(new ArrayList<String>(){{add(directoryPath);}});
    }

    /**
     * Check and create (if not exists) each of the directory path from param
     * 
     * @param directoryPathList
     */
    public static void createDirectories(List<String> directoryPathList) {
        File directoryToCheck;
        for (String path : directoryPathList){
            if (path != null) {
                directoryToCheck = new File(path);
                if (!directoryToCheck.isDirectory() && !createdDirectory.contains(path)) {
                    try {
                        FileUtils.forceMkdir(directoryToCheck);
                        LOG.debug("[" + path + "] has been created successfully");
                        
                        //store locally to avoid future redundant IO check
                        createdDirectory.add(path);
                    }
                    catch (IOException ex) {
                        LOG.warn("Unable to create directory [" + path + "]", ex);
                    }
                }
            }
        }
    }
}
