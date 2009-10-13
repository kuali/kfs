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
package org.kuali.kfs.module.cam.service;

import java.util.List;

import org.kuali.kfs.module.cam.businessobject.AssetLock;

public interface AssetLockService {
    /**
     * Check and Lock for AssetLock.
     * 
     * @param locks
     * @return
     */
    boolean checkAndSetAssetLocks(List<AssetLock> locks);

    /**
     * Delete all locks holding by documentNumber.
     * 
     * @param documentNumber
     * @param additionalInformation
     */
    void deleteAssetLocks(String documentNumber, String lockingInformation);

    /**
     * Helper method to generate AssetLock instances.
     * 
     * @param capitalAssetNumber
     * @param documentNumber
     * @param documentType
     * @param additionalInformation
     * @return
     */
    List<AssetLock> buildAssetLockHelper(List<Long> capitalAssetNumbers, String documentNumber, String documentType, String lockingInformation);

    /**
     * Check if document has any asset locks
     * 
     * @param documentNumber
     * @param lockingInformation
     * @return
     */
    boolean isAssetLockedByCurrentDocument(String documentNumber, String lockingInformation);

    /**
     * Check if assets are locked by other document. 
     * 
     * @param assetNumbers
     * @param documentTypeName
     * @param excludingDocumentNumber
     * @return
     */
    boolean isAssetLocked(List<Long> assetNumbers, String documentTypeName, String excludingDocumentNumber);
    
    List<String> getAssetLockingDocuments(List<Long> assetNumbers, String documentTypeName, String excludingDocumentNumber);
}
