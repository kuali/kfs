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
package org.kuali.kfs.module.ar.document.web.struts;


import java.util.List;

import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.authorization.ContractsGrantsInvoiceDocumentPresentationController;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Form Class for Contracts and Grants Invoice Document.
 */
public class ContractsGrantsInvoiceDocumentForm extends CustomerInvoiceDocumentForm {

    /**
     * @see org.kuali.kfs.module.ar.document.web.struts.CustomerInvoiceDocumentForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        // clear extra buttons
        extraButtons.clear();
        // path to externalizable images
        String buttonUrl = getConfigService().getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        // get the presentation controller class
        ContractsGrantsInvoiceDocument cgInvoiceDocument = (ContractsGrantsInvoiceDocument) getDocument();
        DocumentHelperService docHelperService = SpringContext.getBean(DocumentHelperService.class);
        ContractsGrantsInvoiceDocumentPresentationController presoController = (ContractsGrantsInvoiceDocumentPresentationController) docHelperService.getDocumentPresentationController(cgInvoiceDocument);
        // add Correct Button
        if (presoController.canErrorCorrect(cgInvoiceDocument)) {
            extraButtons.add(generateErrorCorrectionButton());
        }
        // add Prorate Button
        if (presoController.canProrate(cgInvoiceDocument)) {
            addExtraButton(ArConstants.PRORATE_BUTTON_METHOD, buttonUrl + ArConstants.PRORATE_BUTTON_FILE_NAME, ArConstants.PRORATE_BUTTON_ALT_TEXT);
        }
        return extraButtons;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.web.struts.CustomerInvoiceDocumentForm#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE;
    }

    /**
     * @return ContractsGrantsInvoiceDocument
     */
    public ContractsGrantsInvoiceDocument getContractsGrantsInvoiceDocument() {
        return (ContractsGrantsInvoiceDocument) getDocument();
    }

    public KualiDecimal getCurrentTotal() {
        return getContractsGrantsInvoiceDocument().getInvoiceGeneralDetail().getNewTotalBilled().subtract(getContractsGrantsInvoiceDocument().getInvoiceGeneralDetail().getBilledToDateAmount());
    }

    public boolean isShowTransmissionDateButton() {
        return getEditingMode().containsKey(ArAuthorizationConstants.ContractsGrantsInvoiceDocumentEditMode.MODIFY_TRANSMISSION_DATE);
    }
}
