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
package org.kuali.kfs.sys.document;

import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentHeaderDao;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.krad.bo.DocumentHeader;

/**
 * This class is used by the system to use financial specific objects and data for maintenance documents
 */
public class FinancialSystemMaintenanceDocument extends MaintenanceDocumentBase implements FinancialSystemDocument {
    private static final Logger LOG = Logger.getLogger(FinancialSystemMaintenanceDocument.class);

    private transient Map<String,Boolean> canEditCache;

    /**
     * Constructs a FinancialSystemMaintenanceDocument.java.
     */
    public FinancialSystemMaintenanceDocument() {
        super();
    }

    /**
     * Constructs a FinancialSystemMaintenanceDocument.java.
     * @param documentTypeName
     */
    public FinancialSystemMaintenanceDocument(String documentTypeName) {
        super(documentTypeName);
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#setDocumentHeader(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void setDocumentHeader(DocumentHeader documentHeader) {
        if ((documentHeader != null) && (!FinancialSystemDocumentHeader.class.isAssignableFrom(documentHeader.getClass()))) {
            throw new IllegalArgumentException("document header of class '" + documentHeader.getClass() + "' is not assignable from financial document header class '" + FinancialSystemDocumentHeader.class + "'");
        }
        this.documentHeader = documentHeader;
    }

    /**
     * This is the default implementation which ensures that document note attachment references are loaded.
     *
     * @see org.kuali.rice.krad.document.Document#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        // set correctedByDocumentId manually, since OJB doesn't maintain that relationship
        try {
            DocumentHeader correctingDocumentHeader = SpringContext.getBean(FinancialSystemDocumentHeaderDao.class).getCorrectingDocumentHeader(getDocumentHeader().getDocumentNumber());
            if (correctingDocumentHeader != null) {
                getFinancialSystemDocumentHeader().setCorrectedByDocumentId(correctingDocumentHeader.getDocumentNumber());
            }
        } catch (RuntimeException e) {
            LOG.error("Received WorkflowException trying to get route header id from workflow document", e);
            throw new WorkflowRuntimeException(e);
        }
        // set the ad hoc route recipients too, since OJB doesn't maintain that relationship
        // TODO - see KULNRVSYS-1054

        super.processAfterRetrieve();
    }

    /**
     * This is the default implementation which checks for a different workflow statuses, and updates the Kuali status accordingly.
     *
     * @see org.kuali.rice.krad.document.Document#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        if (getDocumentHeader().getWorkflowDocument().isCanceled()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
        }
        else if (getDocumentHeader().getWorkflowDocument().isEnroute()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.ENROUTE);
        }
        if (getDocumentHeader().getWorkflowDocument().isDisapproved()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.DISAPPROVED);
        }
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        }
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Status is: " + getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
        }

        super.doRouteStatusChange(statusChangeEvent);
    }

    @Override
    public boolean answerSplitNodeQuestion(String nodeName) {
        if (getNewMaintainableObject() == null) {
            throw new UnsupportedOperationException("Cannot access Maintainable class to answer split node question");
        }
        if (getNewMaintainableObject() instanceof FinancialSystemMaintainable) {
            return ((FinancialSystemMaintainable)getNewMaintainableObject()).answerSplitNodeQuestion(nodeName);
        } else if (getNewMaintainableObject() instanceof FinancialSystemGlobalMaintainable) {
            return ((FinancialSystemGlobalMaintainable)getNewMaintainableObject()).answerSplitNodeQuestion(nodeName);
        } else {
            throw new UnsupportedOperationException("Maintainable for "+getNewMaintainableObject().getBoClass().getName()+" does not extend org.kuali.kfs.sys.document.FinancialSystemMaintainable nor org.kuali.kfs.sys.document.FinancialSystemGlobalMaintainable and therefore cannot answer split node question");
        }
    }

    /**
     * This method is used for routing and simply returns the initiator's Chart code.
     * @return The Chart code of the document initiator
     */
    public String getInitiatorChartOfAccountsCode() {
        String[] chartOrg = getInitiatorPrimaryDepartmentCode();
        return chartOrg[0];
    }

    /**
     * This method is used for routing and simply returns the initiator's Organization code.
     * @return The Organization code of the document initiator
     */
    public String getInitiatorOrganizationCode() {
        String[] chartOrg = getInitiatorPrimaryDepartmentCode();
        return chartOrg[1];
    }

    /**
     *
     * This method is a utility method that returns a String array containing the document initiator's
     * ChartCode in the first index and the OrganizationCode in the second.
     * @return a String array.
     */
    protected String[] getInitiatorPrimaryDepartmentCode() {

        String netID = documentHeader.getWorkflowDocument().getInitiatorPrincipalId();
        Person person =  KimApiServiceLocator.getPersonService().getPerson(netID);

        String deptCode = person.getPrimaryDepartmentCode();
        String[] chartOrg = deptCode.split("-");
        return chartOrg;

    }

    @Override
    public FinancialSystemDocumentHeader getFinancialSystemDocumentHeader() {
        return (FinancialSystemDocumentHeader)documentHeader;
    }

}

