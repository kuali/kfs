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
import java.io.FilenameFilter;

/**
 * This class provides a set of facilities that can be used to work with files
 */
public class FileUtil {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FileUtil.class);

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
}
