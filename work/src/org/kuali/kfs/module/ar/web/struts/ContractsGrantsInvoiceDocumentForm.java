/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.web.struts;


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.web.struts.CustomerInvoiceDocumentForm;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.authorization.ContractsGrantsInvoiceDocumentPresentationController;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.service.DocumentHelperService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Form Class for Contracts and Grants Invoice Document.
 */
public class ContractsGrantsInvoiceDocumentForm extends CustomerInvoiceDocumentForm {
    private KualiDecimal currentTotal = KualiDecimal.ZERO;
    
    /**
     * @see org.kuali.kfs.module.ar.document.web.struts.CustomerInvoiceDocumentForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        // clear extra buttons
        extraButtons.clear();
        // path to externalizable images
        String buttonUrl = getConfigService().getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        // get the presentation controller class
        ContractsGrantsInvoiceDocument cgInvoiceDocument = (ContractsGrantsInvoiceDocument) getDocument();
        DocumentHelperService docHelperService = SpringContext.getBean(DocumentHelperService.class);
        ContractsGrantsInvoiceDocumentPresentationController presoController = (ContractsGrantsInvoiceDocumentPresentationController) docHelperService.getDocumentPresentationController(cgInvoiceDocument);
        // add Correct Button
        if (presoController.canErrorCorrect(cgInvoiceDocument)) {
            addExtraButton("methodToCall.correct", buttonUrl + "buttonsmall_correction.gif", "Correct");
        }
        // add Prorate Button
        if (getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT) && KRADConstants.YES_INDICATOR_VALUE.equals(KNSServiceLocator.getParameterService().getParameterValueAsString(ArConstants.CG_NAMESPACE_CODE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, ArConstants.ENABLE_CG_PRORATE_BILL_IND)) 
                && !presoController.isBillingFrequencyMilestone(cgInvoiceDocument) && !presoController.isBillingFrequencyPredeterminedBillingSchedule(cgInvoiceDocument) && !cgInvoiceDocument.isCorrectionDocument()) {
            addExtraButton("methodToCall.prorateBill", buttonUrl + "buttonsmall_prorate.gif", "Prorate Bill");
        }
        return extraButtons;
    }

    /**
     * Constructs a ContractsGrantsInvoiceDocumentForm.java. Also sets new customer invoice document detail to a newly constructed
     * customer invoice detail.
     */
    public ContractsGrantsInvoiceDocumentForm() {
        super();
    }

    /**
     * @see org.kuali.kfs.module.ar.document.web.struts.CustomerInvoiceDocumentForm#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return "CGIN";
    }

    /**
     * @return ContractsGrantsInvoiceDocument
     */
    public ContractsGrantsInvoiceDocument getContractsGrantsInvoiceDocument() {
        return (ContractsGrantsInvoiceDocument) getDocument();
    }

    public KualiDecimal getCurrentTotal() {

        currentTotal = getContractsGrantsInvoiceDocument().getInvoiceGeneralDetail().getNewTotalBilled().subtract(getContractsGrantsInvoiceDocument().getInvoiceGeneralDetail().getBilledToDate());
        return currentTotal;
    }
    public void setCurrentTotal(KualiDecimal currentTotal) {
        this.currentTotal = currentTotal;
    }
    
    

}
