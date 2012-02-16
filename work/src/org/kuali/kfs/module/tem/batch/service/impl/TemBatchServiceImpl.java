/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch.service.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.batch.service.TemBatchService;

public class TemBatchServiceImpl implements TemBatchService {
    
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TemBatchServiceImpl.class);
    
    @Override
    public String getCompanionFileName(String fileAbsPath, String fileExtension, String companionFileExtension) {
        return StringUtils.removeEnd(fileAbsPath, fileExtension).concat(companionFileExtension);
    }

    @Override
    public File getFileByAbsolutePath(String absolutePath) {
        File dataFile = new File(absolutePath);

        if (!dataFile.exists() || !dataFile.canRead()) {
            LOG.error("Cannot find/read data file " + absolutePath);
            return null;
        }

        return dataFile;
    }

    @Override
    public void moveErrorFile(String fileName, String directory, String errorFileDirectory) {
        File dataFile = this.getFileByAbsolutePath(fileName);

        if (dataFile != null && dataFile.exists()) {
            File errorDirectory = this.getFileByAbsolutePath(directory);

            try {
                FileUtils.moveToDirectory(dataFile, errorDirectory, true);
            }
            catch (IOException ex) {
                LOG.error("Cannot move the file:" + fileName + " to the directory: " + errorFileDirectory, ex);
            }
        }
    }

}
