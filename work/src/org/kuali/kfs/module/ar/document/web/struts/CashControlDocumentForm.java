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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.authorization.CashControlDocumentPresentationController;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

public class CashControlDocumentForm extends FinancialSystemTransactionalDocumentFormBase {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashControlDocumentForm.class);

    protected CashControlDetail newCashControlDetail;
    protected String processingChartOfAccCodeAndOrgCode;

    protected boolean cashPaymentMediumSelected;
    protected transient ConfigurationService configService;

    /**
     * Constructs a CashControlDocumentForm.java.
     */
    public CashControlDocumentForm() {

        super();

    }

    /**
     * @see org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        // clear extra buttons
        extraButtons.clear();

        String buttonUrl = getConfigService().getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        CashControlDocument cashControlDocument = (CashControlDocument) getDocument();
        CashControlDocumentPresentationController cashControlDocumentPresentationController = (CashControlDocumentPresentationController) KNSServiceLocator.getDocumentHelperService().getDocumentPresentationController(getDocument());

        if (cashControlDocumentPresentationController.canErrorCorrect(cashControlDocument)) {
            addExtraButton("methodToCall.correct", buttonUrl + "buttonsmall_correction.gif", "Correct");
        }

        return extraButtons;
    }

    /**
     * @param property
     * @param source
     * @param altText
     */
    protected void addExtraButton(String property, String source, String altText) {

        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
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

    /**
     * @return
     */
    protected ConfigurationService getConfigService() {
        if (configService == null) {
            configService = SpringContext.getBean(ConfigurationService.class);
        }
        return configService;
    }

}
