/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.batch.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.batch.AssetBarcodeInventoryInputFileType;
import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryInputFileService;
import org.kuali.kfs.module.cam.document.web.struts.AssetBarCodeInventoryInputFileForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchInputFileSetType;
import org.kuali.kfs.sys.batch.service.impl.BatchInputFileSetServiceImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.FileStorageException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.exception.AuthorizationException;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.DateTimeService;

public class AssetBarcodeInventoryInputFileServiceImpl extends BatchInputFileSetServiceImpl implements  AssetBarcodeInventoryInputFileService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetBarcodeInventoryInputFileServiceImpl.class);

    public Map<String, String> save(Person user, AssetBarcodeInventoryInputFileType inputType, String fileUserIdentifier, Map<String, InputStream> typeToStreamMap, AssetBarCodeInventoryInputFileForm form) throws AuthorizationException, FileStorageException {
        Date creationDate = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        Map<String, File> typeToTempFiles = copyStreamsToTemporaryDirectory(user, inputType, fileUserIdentifier, typeToStreamMap, creationDate);
        
        // null the map, because it's full of exhausted input streams that are useless 
        typeToStreamMap = null;
        
        if (!inputType.validate(typeToTempFiles)) {
            deleteTempFiles(typeToTempFiles);
            LOG.error("Upload file validation failed for user " + user.getName() + " identifier " + fileUserIdentifier);
            throw new ValidationException("File validation failed");
        }
        
        byte[] buf = new byte[1024];

        Map<String, String> typeToFileNames = new LinkedHashMap<String, String>();
        Map<String, File> typeToFiles = new LinkedHashMap<String, File>();
        try {
            for (String fileType : inputType.getFileTypes()) {
                File tempFile = typeToTempFiles.get(fileType);
                String saveFileName = inputType.getDirectoryPath(fileType) + File.separator + tempFile.getName();
                try {
                    InputStream fileContents = new FileInputStream(tempFile);
                    File fileToSave = new File(saveFileName);
    
                    copyInputStreamToFile(fileContents, fileToSave, buf);
                    fileContents.close();
                    typeToFileNames.put(fileType, saveFileName);
                    typeToFiles.put(fileType, fileToSave);
                }
                catch (IOException e) {
                    LOG.error("unable to save contents to file " + saveFileName, e);
                    throw new RuntimeException("errors encountered while writing file " + saveFileName, e);
                }
            }
        }
        finally {
            deleteTempFiles(typeToTempFiles);
        }

        String doneFileName = inputType.getDoneFileDirectoryPath() + File.separator + inputType.getDoneFileName(user, fileUserIdentifier, creationDate);
        File doneFile = new File(doneFileName);
        try {
            doneFile.createNewFile();
            
            typeToFiles.put(KFSConstants.DONE_FILE_TYPE, doneFile);
        }
        catch (IOException e) {
            LOG.error("unable to create done file", e);
            throw new RuntimeException("unable to create done file", e);
        }
        
        inputType.process(typeToFiles,form);
        
        return typeToFileNames;
    }
}

