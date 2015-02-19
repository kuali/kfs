/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cam.service;

import java.util.List;

import org.kuali.kfs.module.cam.businessobject.AssetLock;

public interface AssetLockService {
	/**
	 * Adding an indicator to indicate whether or not ignore locking
	 * info when deleting existing asset locks. This will be used to update
	 * asset locks if locking info updated as well.
	 * Check and Lock for AssetLock.
	 *
	 * @param locks
	 *            new asset lock list
	 * @param ignoreLockingInfoText
	 *            indicate whether or not to ignore locking information when
	 *            delete existing asset locks granted to document
	 * @return
	 */
    boolean checkAndSetAssetLocks(List<AssetLock> locks, boolean ignoreLockingInfoForDeletion);

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

