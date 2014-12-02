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
	 * Generate asset locks for FP document if it collects capital
	 * asset number(s) and has capital asset transactions eligible for CAB
	 * batch. Creating asset lock is based on each capital asset line rather
	 * than the whole FP document.
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
