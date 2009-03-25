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
package org.kuali.kfs.module.cam.document.service.impl;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.document.service.DocumentLockingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.dao.MaintenanceDocumentDao;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of the DocumentLockingService
 */
@Transactional
public class DocumentLockingServiceImpl implements DocumentLockingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentLockingServiceImpl.class);

    private MaintenanceDocumentDao maintenanceDocumentDao;

    /**
     * @see org.kuali.kfs.module.cam.document.service.DocumentLockingService#getLockingDocumentId(java.lang.String, java.util.List)
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
     * @see org.kuali.kfs.module.cam.document.service.DocumentLockingService#checkForLockingDocument(java.lang.String)
     */
    public boolean checkForLockingDocument(String blockingDocId) throws ValidationException {

        LOG.info("starting checkForLockingDocument");

        // if we got nothing, then no docs are blocking, and we're done
        if (StringUtils.isBlank(blockingDocId)) {
            return false;
        }

        if ( LOG.isInfoEnabled() ) {
            LOG.info("Locking document found:  docId = " + blockingDocId + ".");
        }

        // load the blocking locked document
        org.kuali.rice.kns.document.Document lockedDocument;
        try {
            lockedDocument = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(blockingDocId);
        }
        catch (WorkflowException e) {
            throw new ValidationException("Could not load the locking document.", e);
        }

        // if we can ignore the lock (see method notes), then exit cause we're done
        if (lockCanBeIgnored(lockedDocument)) {
            return false;
        }

        // build the link URL for the blocking document. Better to use DocHandler because this could be
        // a maintenance document or tDoc.
        Properties parameters = new Properties();
        parameters.put(KNSConstants.PARAMETER_DOC_ID, blockingDocId);
        parameters.put(KNSConstants.PARAMETER_COMMAND, KNSConstants.METHOD_DISPLAY_DOC_SEARCH_VIEW);
        
        String blockingUrl = UrlFactory.parameterizeUrl(
                SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.WORKFLOW_URL_KEY)
                + "/" + KNSConstants.DOC_HANDLER_ACTION, parameters);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("blockingUrl = '" + blockingUrl + "'");
            LOG.debug("Record: " + lockedDocument.getDocumentHeader().getDocumentNumber() + "is locked.");
        }

        // post an error about the locked document
        String[] errorParameters = { blockingUrl, blockingDocId };
        GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_MAINTENANCE_LOCKED, errorParameters);

        return true;
    }
    
    /**
     * This method guesses whether the current user should be allowed to change a document even though it is locked. It probably
     * should use Authorization instead? See KULNRVSYS-948
     * 
     * @param lockedDocument
     * @return
     * @throws WorkflowException
     */
    private boolean lockCanBeIgnored(org.kuali.rice.kns.document.Document lockedDocument) {
        FinancialSystemDocumentHeader documentHeader = (FinancialSystemDocumentHeader) lockedDocument.getDocumentHeader();

        // get the user-id. if no user-id, then we can't do this test, so exit
        String userId = GlobalVariables.getUserSession().getPrincipalId();
        if (StringUtils.isBlank(userId)) {
            return false; // dont bypass locking
        }

        // if the current user is not the initiator of the blocking document
        if (!userId.equalsIgnoreCase(documentHeader.getWorkflowDocument().getInitiatorNetworkId().trim())) {
            return false;
        }

        // if the blocking document hasn't been routed, we can ignore it
        return KFSConstants.DocumentStatusCodes.INITIATED.equals(documentHeader.getFinancialDocumentStatusCode());
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
