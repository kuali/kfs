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
package org.kuali.kfs.module.ar.document.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

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

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return KFSConstants.FinancialDocumentTypeCodes.CASH_CONTROL;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

          if (hasDocumentId()) {
            CashControlDocument ccDoc = getCashControlDocument();

            // apply populate to PaymentApplicationDocuments
            for (CashControlDetail cashControlDetail : ccDoc.getCashControlDetails()) {
                if ( !cashControlDetail.getReferenceFinancialDocument().getDocumentHeader().hasWorkflowDocument() ) {
                    // populate workflowDocument in documentHeader, if needed
                    try {
                        WorkflowDocument workflowDocument = SpringContext.getBean(SessionDocumentService.class).getDocumentFromSession(GlobalVariables.getUserSession(), cashControlDetail.getReferenceFinancialDocumentNumber());

                        if (workflowDocument == null) {
                            // gets the workflow document from doc service
                            workflowDocument = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(cashControlDetail.getReferenceFinancialDocumentNumber(), GlobalVariables.getUserSession().getPerson() );
                            SpringContext.getBean(SessionDocumentService.class).addDocumentToUserSession(GlobalVariables.getUserSession(), workflowDocument);
                            if (workflowDocument == null) {
                                throw new WorkflowException("Unable to get retrieve document # " + cashControlDetail.getReferenceFinancialDocumentNumber() + " from document service getByDocumentHeaderId");
                            }
                        }

                        cashControlDetail.getReferenceFinancialDocument().getDocumentHeader().setWorkflowDocument(workflowDocument);
                    }
                    catch (WorkflowException e) {
                        LOG.warn("Error while instantiating workflowDoc: " + cashControlDetail.getReferenceFinancialDocumentNumber(), e);
                        throw new RuntimeException("error populating documentHeader.workflowDocument", e);
                    }
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
        if (newCashControlDetail == null) {
            newCashControlDetail = new CashControlDetail();
        }
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
