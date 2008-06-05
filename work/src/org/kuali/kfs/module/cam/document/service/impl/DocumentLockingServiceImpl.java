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
package org.kuali.module.cams.service.impl;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceKeyConstants;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.dao.MaintenanceDocumentDao;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.module.cams.service.DocumentLockingService;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * The default implementation of the DocumentLockingService
 */
@Transactional
public class DocumentLockingServiceImpl implements DocumentLockingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentLockingServiceImpl.class);

    private MaintenanceDocumentDao maintenanceDocumentDao;

    /**
     * @see org.kuali.module.cams.service.DocumentLockingService#getLockingDocumentId(java.lang.String, java.util.List)
     */
    public String getLockingDocumentId(String documentNumber, List<MaintenanceLock> maintenanceLocks) {
        String lockingDocId = null;

        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            lockingDocId = maintenanceDocumentDao.getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(),documentNumber);
            if (StringUtils.isNotBlank(lockingDocId)) {
                break;
            }
        }

        return lockingDocId;
    }
    
    /**
     * @see org.kuali.module.cams.service.DocumentLockingService#checkForLockingDocument(java.lang.String)
     */
    public void checkForLockingDocument(String blockingDocId) {

        LOG.info("starting checkForLockingDocument");

        // if we got nothing, then no docs are blocking, and we're done
        if (StringUtils.isBlank(blockingDocId)) {
            return;
        }

        if ( LOG.isInfoEnabled() ) {
            LOG.info("Locking document found:  docId = " + blockingDocId + ".");
        }

        // load the blocking locked document
        org.kuali.core.document.Document lockedDocument;
        try {
            lockedDocument = KNSServiceLocator.getDocumentService().getByDocumentHeaderId(blockingDocId);
        }
        catch (WorkflowException e) {
            throw new ValidationException("Could not load the locking document.", e);
        }

        // if we can ignore the lock (see method notes), then exit cause we're done
        if (lockCanBeIgnored(lockedDocument)) {
            return;
        }

        // build the link URL for the blocking document. Easier to use en/docHandler because this could be
        // a maintenance document or tDoc.
        Properties parameters = new Properties();
        parameters.put(KNSConstants.PARAMETER_DOC_ID, blockingDocId);
        parameters.put(KNSConstants.PARAMETER_COMMAND, KNSConstants.METHOD_DISPLAY_DOC_SEARCH_VIEW);
        // TODO add constant for the following line
        String blockingUrl = UrlFactory.parameterizeUrl("en/DocHandler.do", parameters);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("blockingUrl = '" + blockingUrl + "'");
            LOG.debug("Record: " + lockedDocument.getDocumentHeader().getDocumentNumber() + "is locked.");
        }

        // post an error about the locked document
        String[] errorParameters = { blockingUrl, blockingDocId };
        GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_MAINTENANCE_LOCKED, errorParameters);

        throw new ValidationException("Record is locked by another document.");
    }
    
    /**
     * This method guesses whether the current user should be allowed to change a document even though it is locked. It probably
     * should use Authorization instead? See KULNRVSYS-948
     * 
     * @param lockedDocument
     * @return
     * @throws WorkflowException
     */
    private boolean lockCanBeIgnored(org.kuali.core.document.Document lockedDocument) {
        // TODO: implement real authorization for Maintenance Document Save/Route - KULNRVSYS-948

        DocumentHeader documentHeader = lockedDocument.getDocumentHeader();

        // get the user-id. if no user-id, then we can do this test, so exit
        String userId = GlobalVariables.getUserSession().getNetworkId().trim();
        if (StringUtils.isBlank(userId)) {
            return false; // dont bypass locking
        }

        // if the current user is not the initiator of the blocking document
        if (!userId.equalsIgnoreCase(documentHeader.getWorkflowDocument().getInitiatorNetworkId().trim())) {
            return false;
        }

        // if the blocking document hasn't been routed, we can ignore it
        return KNSConstants.DocumentStatusCodes.INITIATED.equals(documentHeader.getFinancialDocumentStatusCode());
    }
    
    /**
     * @return Returns the maintenanceDocumentDao.
     */
    public MaintenanceDocumentDao getMaintenanceDocumentDao() {
        return maintenanceDocumentDao;
    }

    /**
     * @param maintenanceDocumentDao The maintenanceDocumentDao to set.
     */
    public void setMaintenanceDocumentDao(MaintenanceDocumentDao maintenanceDocumentDao) {
        this.maintenanceDocumentDao = maintenanceDocumentDao;
    }
}
