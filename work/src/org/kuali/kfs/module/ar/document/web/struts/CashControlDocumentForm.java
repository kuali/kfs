/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class CashControlDocumentForm extends FinancialSystemTransactionalDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashControlDocumentForm.class);

    private CashControlDetail newCashControlDetail;
    private String processingChartOfAccCodeAndOrgCode;
    
    private boolean cashPaymentMediumSelected;

    /**
     * Constructs a CashControlDocumentForm.java.
     */
    public CashControlDocumentForm() {

        super();
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
                    KualiWorkflowDocument workflowDocument = null;
                    if (GlobalVariables.getUserSession().getWorkflowDocument(cashControlDetail.getReferenceFinancialDocumentNumber()) != null) {
                        workflowDocument = GlobalVariables.getUserSession().getWorkflowDocument(cashControlDetail.getReferenceFinancialDocumentNumber());
                    }
                    else {
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
