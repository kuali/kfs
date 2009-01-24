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

import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentHeaderDao;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.document.TransactionalDocumentBase;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This class is a KFS specific TransactionalDocumentBase class
 */
public class FinancialSystemTransactionalDocumentBase extends TransactionalDocumentBase implements FinancialSystemTransactionalDocument {
    private static final Logger LOG = Logger.getLogger(FinancialSystemTransactionalDocumentBase.class);

    protected FinancialSystemDocumentHeader documentHeader;

    /**
     * Constructs a FinancialSystemTransactionalDocumentBase.java.
     */
    public FinancialSystemTransactionalDocumentBase() {
        super();
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
    @Override
    public void prepareForSave() {
        if (this instanceof AmountTotaling) {
            getDocumentHeader().setFinancialDocumentTotalAmount(((AmountTotaling) this).getTotalDollarAmount());
        }
        super.prepareForSave();
    }

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
        if (getDocumentHeader().getWorkflowDocument().stateIsCanceled() || getDocumentHeader().getWorkflowDocument().stateIsDisapproved() || getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
            getDocumentHeader().setDocumentFinalDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        }
        super.handleRouteStatusChange();
    }

    /**
     * @see org.kuali.kfs.sys.document.Correctable#toErrorCorrection()
     */
    public void toErrorCorrection() throws WorkflowException, IllegalStateException {
        DocumentHelperService documentHelperService = SpringContext.getBean(DocumentHelperService.class);
        final Set<String> documentActionsFromPresentationController = documentHelperService.getDocumentPresentationController(this).getDocumentActions(this);
        final Set<String> documentActionsFromAuthorizer = documentHelperService.getDocumentAuthorizer(this).getDocumentActions(this, GlobalVariables.getUserSession().getPerson(), documentActionsFromPresentationController);
        if (!documentActionsFromAuthorizer.contains(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT)) {
            throw new IllegalStateException(this.getClass().getName() + " does not support document-level error correction");
        }

        String sourceDocumentHeaderId = getDocumentNumber();
        setNewDocumentHeader();
        getDocumentHeader().setFinancialDocumentInErrorNumber(sourceDocumentHeaderId);
        addCopyErrorDocumentNote("error-correction for document " + sourceDocumentHeaderId);
    }

    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("FinancialSystemTransactionalDocumentBase does not implement the answerSplitNodeQuestion method. Node name specified was: " + nodeName); 
    }
}
