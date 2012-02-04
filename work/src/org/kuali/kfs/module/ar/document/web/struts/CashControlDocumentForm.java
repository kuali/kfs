/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashControlDocumentForm extends FinancialSystemTransactionalDocumentFormBase {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashControlDocumentForm.class);

    protected CashControlDetail newCashControlDetail;
    protected String processingChartOfAccCodeAndOrgCode;
    
    protected boolean cashPaymentMediumSelected;

    /**
     * Constructs a CashControlDocumentForm.java.
     */
    public CashControlDocumentForm() {

        super();

    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "CTRL";
    }
    
    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);

        CashControlDocument ccDoc = getCashControlDocument();

        if (hasDocumentId()) {

            // apply populate to PaymentApplicationDocuments
            for (CashControlDetail cashControlDetail : ccDoc.getCashControlDetails()) {

                // populate workflowDocument in documentHeader, if needed
                try {
                    WorkflowDocument workflowDocument = null;
                    //RICE20 workflowDocument doesn't exist in UserSession anymore; use SessionDocumentService.getDocumentFromSession(UserSession userSession, String docId)
                    workflowDocument = SpringContext.getBean(SessionDocumentService.class).getDocumentFromSession(GlobalVariables.getUserSession(), cashControlDetail.getReferenceFinancialDocumentNumber());
                    
                    if (workflowDocument == null) {                        
                        // gets the workflow document from doc service, doc service will also set the workflow document in the
                        // user's session
                        Document retrievedDocument = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(cashControlDetail.getReferenceFinancialDocumentNumber());
                        if (retrievedDocument == null) {
                            throw new WorkflowException("Unable to get retrieve document # " + cashControlDetail.getReferenceFinancialDocumentNumber() + " from document service getByDocumentHeaderId");
                        }
                        workflowDocument = retrievedDocument.getDocumentHeader().getWorkflowDocument();
                    }

                    cashControlDetail.getReferenceFinancialDocument().getDocumentHeader().setWorkflowDocument(workflowDocument);
                }
                catch (WorkflowException e) {
                    LOG.warn("Error while instantiating workflowDoc", e);
                    throw new RuntimeException("error populating documentHeader.workflowDocument", e);
                }
            }
        }

    }

    /**
     * This method gets the cash control document
     * 
     * @return the CashControlDocument
     */
    public CashControlDocument getCashControlDocument() {
        return (CashControlDocument) getDocument();
    }

    /**
     * This method gets the new cash control detail
     * 
     * @return cashControlDetail
     */
    public CashControlDetail getNewCashControlDetail() {
        return newCashControlDetail;
    }

    /**
     * This method sets the new cash control detail
     * 
     * @param newCashControlDetail
     */
    public void setNewCashControlDetail(CashControlDetail newCashControlDetail) {
        this.newCashControlDetail = newCashControlDetail;
    }

    /**
     * This method gets the processingChartOfAccCodeAndOrgCode
     * 
     * @return processingChartOfAccCodeAndOrgCode
     */
    public String getProcessingChartOfAccCodeAndOrgCode() {
        return this.getCashControlDocument().getAccountsReceivableDocumentHeader().getProcessingChartOfAccCodeAndOrgCode();
    }

    /**
     * This method sets processingChartOfAccCodeAndOrgCode
     * 
     * @param processingChartOfAccCodeAndOrgCode
     */
    public void setProcessingChartOfAccCodeAndOrgCode(String processingChartOfAccCodeAndOrgCode) {
        this.processingChartOfAccCodeAndOrgCode = processingChartOfAccCodeAndOrgCode;
    }

    /**
     * This method returns if payment medium is selected
     * 
     * @return true if payment medium selected, false otherwise
     */
    public boolean isCashPaymentMediumSelected() {
        return cashPaymentMediumSelected;
    }

    /**
     * This method sets if payments medium is selected
     * 
     * @param cashPaymentMediumSelected
     */
    public void setCashPaymentMediumSelected(boolean cashPaymentMediumSelected) {
        this.cashPaymentMediumSelected = cashPaymentMediumSelected;
    }

}
