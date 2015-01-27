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


import java.util.List;

import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.authorization.ContractsGrantsInvoiceDocumentAuthorizer;
import org.kuali.kfs.module.ar.document.authorization.ContractsGrantsInvoiceDocumentPresentationController;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Form Class for Contracts & Grants Invoice Document.
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
        ContractsGrantsInvoiceDocumentAuthorizer documentAuthorizer = (ContractsGrantsInvoiceDocumentAuthorizer) docHelperService.getDocumentAuthorizer(cgInvoiceDocument);
        final Person user = GlobalVariables.getUserSession().getPerson();

        // add Correct Button
        if (presoController.canErrorCorrect(cgInvoiceDocument) && documentAuthorizer.canErrorCorrect(cgInvoiceDocument, user)) {
            extraButtons.add(generateErrorCorrectionButton());
        }
        // add Prorate Button
        if (getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT) && presoController.canProrate(cgInvoiceDocument)) {
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
        return getContractsGrantsInvoiceDocument().getInvoiceGeneralDetail().getTotalAmountBilledToDate().subtract(getContractsGrantsInvoiceDocument().getInvoiceGeneralDetail().getTotalPreviouslyBilled());
    }

    public boolean isShowTransmissionDateButton() {
        return getEditingMode().containsKey(ArAuthorizationConstants.ContractsGrantsInvoiceDocumentEditMode.MODIFY_TRANSMISSION_DATE);
    }
}
