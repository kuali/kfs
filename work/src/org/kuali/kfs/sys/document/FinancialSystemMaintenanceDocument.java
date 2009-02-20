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
package org.kuali.kfs.sys.document;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentHeaderDao;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * This class is used by the system to use financial specific objects and data for maintenance documents
 */
public class FinancialSystemMaintenanceDocument extends MaintenanceDocumentBase {
    private static final Logger LOG = Logger.getLogger(FinancialSystemMaintenanceDocument.class);

    protected FinancialSystemDocumentHeader documentHeader;

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
     * @see org.kuali.rice.kns.document.DocumentBase#getDocumentHeader()
     */
    @Override
    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#setDocumentHeader(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void setDocumentHeader(DocumentHeader documentHeader) {
        if ((documentHeader != null) && (!FinancialSystemDocumentHeader.class.isAssignableFrom(documentHeader.getClass()))) {
            throw new IllegalArgumentException("document header of class '" + documentHeader.getClass() + "' is not assignable from financial document header class '" + FinancialSystemDocumentHeader.class + "'");
        }
        this.documentHeader = (FinancialSystemDocumentHeader) documentHeader;
    }

    /**
     * If the document has a total amount, call method on document to get the total and set in doc header.
     * 
     * @see org.kuali.rice.kns.document.Document#prepareForSave()
     */
//    @Override
//    public void prepareForSave() {
//        if (this instanceof AmountTotaling) {
//            getDocumentHeader().setFinancialDocumentTotalAmount(((AmountTotaling) this).getTotalDollarAmount());
//        }
//    }

    /**
     * This is the default implementation which ensures that document note attachment references are loaded.
     * 
     * @see org.kuali.rice.kns.document.Document#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        // set correctedByDocumentId manually, since OJB doesn't maintain that relationship
        try {
            DocumentHeader correctingDocumentHeader = SpringContext.getBean(FinancialSystemDocumentHeaderDao.class).getCorrectingDocumentHeader(getDocumentHeader().getWorkflowDocument().getRouteHeaderId().toString());
            if (correctingDocumentHeader != null) {
                getDocumentHeader().setCorrectedByDocumentId(correctingDocumentHeader.getDocumentNumber());
            }
        } catch (WorkflowException e) {
            LOG.error("Received WorkflowException trying to get route header id from workflow document");
            throw new WorkflowRuntimeException(e);
        }
        // set the ad hoc route recipients too, since OJB doesn't maintain that relationship
        // TODO - see KULNRVSYS-1054

        super.processAfterRetrieve();
    }

    /**
     * This is the default implementation which checks for a different workflow statuses, and updates the Kuali status accordingly.
     * 
     * @see org.kuali.rice.kns.document.Document#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        if (getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
        }
        else if (getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.ENROUTE);
        }
        if (getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.DISAPPROVED);
        }
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        }
        LOG.info("Status is: " + getDocumentHeader().getFinancialDocumentStatusCode());
        
        super.handleRouteStatusChange();
    }
    
    public boolean answerSplitNodeQuestion(String nodeName) {
        if (getNewMaintainableObject() instanceof FinancialSystemMaintainable) {
            FinancialSystemMaintainable fsMaintainable = (FinancialSystemMaintainable)getNewMaintainableObject();

            if (fsMaintainable == null) {
                throw new UnsupportedOperationException("Cannot access Maintainable class to answer split node question");
            }
            return fsMaintainable.answerSplitNodeQuestion(nodeName);
        } else if (getNewMaintainableObject() instanceof FinancialSystemGlobalMaintainable) {
            FinancialSystemGlobalMaintainable fsMaintainable = (FinancialSystemGlobalMaintainable)getNewMaintainableObject();

            if (fsMaintainable == null) {
                throw new UnsupportedOperationException("Cannot access Maintainable class to answer split node question");
            }
            return fsMaintainable.answerSplitNodeQuestion(nodeName);
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
    private String[] getInitiatorPrimaryDepartmentCode() {
        PersonService personService = SpringContext.getBean(PersonService.class);
        
        String netID = documentHeader.getWorkflowDocument().getInitiatorNetworkId();
        Person person =  personService.getPersonByPrincipalName(netID);
       
        String deptCode = person.getPrimaryDepartmentCode();
        String[] chartOrg = deptCode.split("-");
        return chartOrg;
        
    }
    
}

