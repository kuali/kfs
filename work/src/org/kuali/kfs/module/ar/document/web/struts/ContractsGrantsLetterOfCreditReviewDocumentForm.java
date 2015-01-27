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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;

/**
 * Form Class for Contracts & Grants LetterOfCredit Review Document.
 */
public class ContractsGrantsLetterOfCreditReviewDocumentForm extends FinancialSystemTransactionalDocumentFormBase {

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return ArConstants.ArDocumentTypeCodes.LETTER_OF_CREDIT_REVIEW;
    }

    /**
     * Build additional document specific buttons and set extraButtons list.
     *
     * @return - list of extra buttons to be displayed to the user
     */
    @Override
    public List<ExtraButton> getExtraButtons() {

        // clear out the extra buttons array
        extraButtons.clear();

        ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLetterOfCreditReviewDocument = (ContractsGrantsLetterOfCreditReviewDocument) getDocument();
        DocumentHelperService docHelperService = SpringContext.getBean(DocumentHelperService.class);
        TransactionalDocumentPresentationController presoController = (TransactionalDocumentPresentationController) docHelperService.getDocumentPresentationController(contractsGrantsLetterOfCreditReviewDocument);
        Set<String> editModes = presoController.getEditModes(contractsGrantsLetterOfCreditReviewDocument);

        // special buttons for the first 'init' screen
        if (editModes.contains(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB)) {
            String externalImageURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
            addExtraButton(KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + ArConstants.CONTINUE_LOC_REVIEW_METHOD, externalImageURL + ArConstants.CONTINUE_BUTTON_FILE_NAME, ArConstants.CONTINUE_BUTTON_ALT_TEXT);
            addExtraButton(KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + ArConstants.CLEAR_INIT_TAB_METHOD, externalImageURL + ArConstants.CLEAR_BUTTON_FILE_NAME, ArConstants.CLEAR_BUTTON_ALT_TEXT);
        } else {
            // draw the Print File button if appropriate
            String printButtonURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
            addExtraButton(KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + ArConstants.PRINT_METHOD, printButtonURL + ArConstants.PRINT_BUTTON_FILE_NAME, ArConstants.PRINT_BUTTON_ALT_TEXT);
            String exportButtonURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
            addExtraButton(KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + ArConstants.EXPORT_METHOD, exportButtonURL + ArConstants.EXPORT_BUTTON_FILE_NAME, ArConstants.EXPORT_BUTTON_ALT_TEXT, ArConstants.EXPORT_BUTTON_ONCLICK_TEXT);
        }

        return extraButtons;
    }

    /**
     * Adds a new button to the extra buttons collection.
     *
     * @param property - property for button
     * @param source - location of image
     * @param altText - alternate text for button if images don't appear
     */
    protected void addExtraButton(String property, String source, String altText) {
        addExtraButton(property, source, altText, StringUtils.EMPTY);
    }

    /**
     * Adds a new button to the extra buttons collection.
     *
     * @param property - property for button
     * @param source - location of image
     * @param altText - alternate text for button if images don't appear
     * @param onClick - onclick property for the button
     */
    protected void addExtraButton(String property, String source, String altText, String onClick) {
        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);
        newButton.setExtraButtonOnclick(onClick);

        extraButtons.add(newButton);
    }

    /**
     * Gets the proposalNumbers attribute.
     *
     * @return Returns the proposalNumbers.
     */
    public List<Long> getProposalNumbers() {
        // To get the list of all proposal numbers from the review details.
        ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLetterOfCreditReviewDocument = (ContractsGrantsLetterOfCreditReviewDocument) getDocument();
        Set<Long> pps = new HashSet<Long>();

        for (ContractsGrantsLetterOfCreditReviewDetail detail : contractsGrantsLetterOfCreditReviewDocument.getAccountReviewDetails()) {
            pps.add(detail.getProposalNumber());
        }

        List<Long> ppNos = new ArrayList<Long>(pps);
        Collections.sort(ppNos);
        return ppNos;
    }

}
