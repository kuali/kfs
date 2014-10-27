/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.krad.document.Document;

public interface CapitalAssetManagementModuleService {
    /**
     * FP document eligible for asset lock when any of its accounting line is taken into CAB during CAB batch.
     * 
     * @param accountingDocument
     * @return
     */
    boolean isFpDocumentEligibleForAssetLock(AccountingDocument accountingDocument, String documentType);

    /**
     * Check and store AssetLocks if they are not locked by other blocking documents. Either store all of the asset locks or none of
     * them being stored in case of dead lock. If any of the asset is blocked, the error message will be built including link(s) to
     * the blocking document(s).
     * 
     * @param capitalAssetNumbers
     * @param documentNumber
     * @param documentType
     * @param additionalInformation
     * @return return true if all of the asset locks can be granted.
     */
    boolean storeAssetLocks(List<Long> capitalAssetNumbers, String documentNumber, String documentType, String lockingInformation);

    /**
     * Delete AssetLocks by document number and lockingInfomation for PurAp doc only.
     * 
     * @param documentNumber
     * @param lockingInformation
     */
    void deleteAssetLocks(String documentNumber, String lockingInformation);


    /**
     * Check if the given document hold any asset locks.
     * 
     * @param documentNumber
     * @param lockingInformation
     * @return
     */
    boolean isAssetLockedByCurrentDocument(String blockingDocumentNumber, String lockingInformation);

    /**
     * Check if the given asset Numbers are locked by other documents already.
     * 
     * @param assetNumbers
     * @param documentTypeName
     * @param excludingDocumentNumber
     * @return
     */
    boolean isAssetLocked(List<Long> assetNumbers, String documentTypeName, String excludingDocumentNumber);


    /**
     * Creates the locks for each asset on a documents
     * 
     * @param document
     */
    public void generateCapitalAssetLock(Document document, String documentTypeNames);

    /**
     * Deletes the asset locks associated with a particular document
     * 
     * @param document
     */
    public void deleteDocumentAssetLocks(Document document);

}
