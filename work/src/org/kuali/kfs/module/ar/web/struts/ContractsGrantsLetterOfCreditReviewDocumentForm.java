/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.web.struts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Form Class for Contracts Grants LetterOfCredit Review Document.
 */
public class ContractsGrantsLetterOfCreditReviewDocumentForm extends FinancialSystemTransactionalDocumentFormBase {

    private List<Long> proposalNumbers;

    public ContractsGrantsLetterOfCreditReviewDocumentForm() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return "LCR";
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);


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
            addExtraButton("methodToCall.continueLOCReview", externalImageURL + "buttonsmall_continue.gif", "Continue");
            addExtraButton("methodToCall.clearInitTab", externalImageURL + "buttonsmall_clear.gif", "Clear");
        }
        // draw the Print File button if appropriate
        String printButtonURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        addExtraButton("methodToCall.print", printButtonURL + "buttonsmall_genprintfile.gif", "Print");
        String exportButtonURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
        addExtraButton("methodToCall.export", exportButtonURL + "buttonsmall_export.gif", "Export", "excludeSubmitRestriction=true");

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

        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
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

    /**
     * Sets the proposalNumbers attribute value.
     *
     * @param proposalNumbers The proposalNumbers to set.
     */
    public void setProposalNumbers(List<Long> proposalNumbers) {
        this.proposalNumbers = proposalNumbers;
    }


}
