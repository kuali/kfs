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
package org.kuali.kfs.module.tem.batch.service;

import java.io.File;

public interface TemBatchService {

    /**
     * generate the name of a companion file of the given file with the given companion file extension
     * 
     * @param fileAbsPath the given file
     * @param fileExtension the extension of the given file
     * @param companionFileExtension the given companion file extension
     * @return the name of a companion file of the given file with the given companion file extension
     */
    public String getCompanionFileName(String fileAbsPath, String fileExtension, String companionFileExtension);
    
    /**
     * get the file based on the given absolute file path
     * 
     * @param absolutePath the given absolute file path
     * @return the file with the given absolute file path
     */
    public File getFileByAbsolutePath(String absolutePath);
    
    /**
     * move the given file to the specified directory
     * 
     * @param fileName the given file name
     * @param directory the specified directory
     */
    public void moveErrorFile(String fileName, String directory, String errorFileDirectory);
}
