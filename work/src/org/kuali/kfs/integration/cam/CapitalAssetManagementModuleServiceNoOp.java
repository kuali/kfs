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
package org.kuali.kfs.integration.cam;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.krad.document.Document;

public class CapitalAssetManagementModuleServiceNoOp implements CapitalAssetManagementModuleService {

    private Logger LOG = Logger.getLogger(getClass()); 
    
    public void deleteAssetLocks(String documentNumber, String lockingInformation) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
    }

    public void deleteDocumentAssetLocks(Document document) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
    }

    public void generateCapitalAssetLock(Document document, String documentTypeNames) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
    }

    public boolean isAssetLocked(List<Long> assetNumbers, String documentTypeName, String excludingDocumentNumber) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    public boolean isAssetLockedByCurrentDocument(String blockingDocumentNumber, String lockingInformation) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    public boolean isFpDocumentEligibleForAssetLock(AccountingDocument accountingDocument, String documentType) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    public boolean storeAssetLocks(List<Long> capitalAssetNumbers, String documentNumber, String documentType, String lockingInformation) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

}
