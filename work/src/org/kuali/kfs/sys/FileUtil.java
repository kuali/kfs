/*
 * Copyright 2011 The Kuali Foundation
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
