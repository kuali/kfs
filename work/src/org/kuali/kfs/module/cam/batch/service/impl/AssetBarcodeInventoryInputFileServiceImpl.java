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
import java.io.FileNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryInputFileService;
import org.kuali.kfs.sys.batch.BatchInputFileSetType;
import org.kuali.kfs.sys.batch.service.impl.BatchInputFileSetServiceImpl;

public class AssetBarcodeInventoryInputFileServiceImpl extends BatchInputFileSetServiceImpl implements  AssetBarcodeInventoryInputFileService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetBarcodeInventoryInputFileServiceImpl.class);
    
    /**
     * 
     * @see org.kuali.kfs.sys.batch.service.impl.BatchInputFileSetServiceImpl#delete(org.kuali.core.bo.user.UniversalUser, org.kuali.kfs.sys.batch.BatchInputFileSetType, java.lang.String)
     */
    @Override
    public boolean delete(UniversalUser user, BatchInputFileSetType inputType, String fileUserIdentifier) throws AuthorizationException, FileNotFoundException {
        if (user == null || inputType == null || StringUtils.isBlank(fileUserIdentifier)) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        // check user is authorized to delete a file for the batch type
        if (!this.isUserAuthorizedForBatchType(inputType, user)) {
            LOG.error("User " + user.getPersonUserIdentifier() + " is not authorized to delete a file of batch type " + inputType.getFileSetTypeIdentifer());
            throw new AuthorizationException(user.getPersonUserIdentifier(), "delete", inputType.getFileSetTypeIdentifer());
        }

        for (String fileType : inputType.getFileTypes()) {
            String fileName = generateFileName(user, inputType, fileUserIdentifier, fileType);
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        }
        File doneFile = new File(generateDoneFileName(user, inputType, fileUserIdentifier));
        if (doneFile.exists()) {
            doneFile.delete();
        }
        return true;
    }

}
