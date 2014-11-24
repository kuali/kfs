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
