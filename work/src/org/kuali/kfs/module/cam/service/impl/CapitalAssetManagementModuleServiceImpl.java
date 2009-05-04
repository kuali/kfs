/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetLock;
import org.kuali.kfs.module.cam.service.AssetLockService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class CapitalAssetManagementModuleServiceImpl implements CapitalAssetManagementModuleService {
    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#storeAssetLocks(java.util.List, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public boolean storeAssetLocks(List<Long> capitalAssetNumbers, String documentNumber, String documentType, String lockingInformation) {
        List<AssetLock> assetLocks = getAssetLockService().buildAssetLockHelper(capitalAssetNumbers, documentNumber, documentType, StringUtils.isBlank(lockingInformation) ? CamsConstants.defaultLockingInformation : lockingInformation);
        return getAssetLockService().checkAndSetAssetLocks(assetLocks);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#deleteAssetLocks(java.lang.String, java.lang.String)
     */
    public void deleteAssetLocks(String documentNumber, String lockingInformation) {
        getAssetLockService().deleteAssetLocks(documentNumber, StringUtils.isBlank(lockingInformation) ? CamsConstants.defaultLockingInformation : lockingInformation);
    }

    protected AssetLockService getAssetLockService() {
        return SpringContext.getBean(AssetLockService.class);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#isAssetLockedByDocument(java.lang.String,
     *      java.lang.String)
     */
    public boolean isAssetLockedByCurrentDocument(String documentNumber, String lockingInformation) {
        return getAssetLockService().isAssetLockedByCurrentDocument(documentNumber, StringUtils.isBlank(lockingInformation) ? CamsConstants.defaultLockingInformation : lockingInformation);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#isAssetLocked(java.util.List, java.lang.String,
     *      java.lang.String)
     */
    public boolean isAssetLocked(List<Long> assetNumbers, String documentTypeName, String excludingDocumentNumber) {
        return getAssetLockService().isAssetLocked(assetNumbers, documentTypeName, excludingDocumentNumber);
    }

    /**
     * 
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#generateCapitalAssetLock(org.kuali.rice.kns.document.Document)
     */
    public void generateCapitalAssetLock(Document document,String documentTypeName) {
        CapitalAssetInformation capitalAssetInformation = ((CapitalAssetEditable)document).getCapitalAssetInformation(); 

        if (ObjectUtils.isNotNull(capitalAssetInformation) && ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetNumber())) {        
            ArrayList<Long> capitalAssetNumbers = new ArrayList<Long>();
            capitalAssetNumbers.add(capitalAssetInformation.getCapitalAssetNumber());                            

            if (!this.storeAssetLocks(capitalAssetNumbers, document.getDocumentNumber(), documentTypeName, null)) {
                throw new ValidationException("Asset " + capitalAssetNumbers.toString() + " is being locked by other documents.");
            }
        }            
    }    

    /**
     * 
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#deleteDocumentAssetLocks(org.kuali.rice.kns.document.Document)
     */
    public void deleteDocumentAssetLocks(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        CapitalAssetInformation capitalAssetInformation = ((CapitalAssetEditable)document).getCapitalAssetInformation(); 

        //Deleting document lock
        if (workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved()) {            
            if (ObjectUtils.isNotNull(capitalAssetInformation))
                this.deleteAssetLocks(document.getDocumentNumber(), null);
        }
    }
}
