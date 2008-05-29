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
package org.kuali.module.cams.web.struts.action;

import static org.kuali.module.cams.CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.web.struts.form.AssetTransferForm;
import org.kuali.rice.KNSServiceLocator;

public class AssetTransferAction extends KualiTransactionalDocumentActionBase {
    private static final Logger LOG = Logger.getLogger(AssetTransferAction.class);

    /**
     * This method had to override because asset information has to be refreshed before display
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward docHandlerForward = super.docHandler(mapping, form, request, response);
        AssetTransferForm assetTransferForm = (AssetTransferForm) form;
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) assetTransferForm.getDocument();
        handleRequestFromLookup(request, assetTransferForm, assetTransferDocument);
        handleRequestFromWorkflow(assetTransferForm, assetTransferDocument);
        Asset asset = assetTransferDocument.getAsset();
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_LOCATIONS);
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
        SpringContext.getBean(AssetLocationService.class).setOffCampusLocation(asset);
        SpringContext.getBean(PaymentSummaryService.class).calculateAndSetPaymentSummary(asset);
        return docHandlerForward;
    }

    /**
     * This method handles when request is from a work flow document search
     * 
     * @param assetTransferForm Form
     * @param assetTransferDocument Document
     * @param service BusinessObjectService
     * @param assetHeader Asset header object
     * @return Asset
     */
    private void handleRequestFromWorkflow(AssetTransferForm assetTransferForm, AssetTransferDocument assetTransferDocument) {
        LOG.debug("Start- Handle request from workflow");
        if (assetTransferForm.getDocId() != null) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ASSET);
            UniversalUserService universalUserService = SpringContext.getBean(UniversalUserService.class);
            try {
                UniversalUser universalUser = universalUserService.getUniversalUser(assetTransferDocument.getRepresentativeUniversalIdentifier());
                assetTransferDocument.setAssetRepresentative(universalUser);
            }
            catch (UserNotFoundException e) {
                LOG.error("UniversalUserService returned with UserNotFoundException for uuid " + assetTransferDocument.getRepresentativeUniversalIdentifier());
            }
        }
    }

    /**
     * This method handles the request coming from asset lookup screen
     * 
     * @param request Request
     * @param assetTransferForm Current form
     * @param assetTransferDocument Document
     * @param service Business Object Service
     * @param asset Asset
     * @return Asset
     */
    private void handleRequestFromLookup(HttpServletRequest request, AssetTransferForm assetTransferForm, AssetTransferDocument assetTransferDocument) {
        LOG.debug("Start - Handle request from asset lookup screen");
        if (assetTransferForm.getDocId() == null) {
            String capitalAssetNumber = request.getParameter(CAPITAL_ASSET_NUMBER);
            assetTransferDocument.setCapitalAssetNumber(Long.valueOf(capitalAssetNumber));
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ASSET);
        }
    }


    /**
     * This method override checks the equipment loan status, and if loaned will display a confirmation message, based on user
     * response document will be canceled or routed with additional note
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Start - Handle route document");
        ActionForward forward = null;
        AssetTransferForm assetTransferForm = (AssetTransferForm) form;
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) assetTransferForm.getDocument();
        Asset asset = assetTransferDocument.getAsset();
        if (asset.getExpectedReturnDate() != null && asset.getLoanReturnDate() == null) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();
            // ask the question only once
            if (question == null && !assetTransferForm.isLoanNoteAdded()) {
                // Ask question
                forward = performQuestionWithoutInput(mapping, form, request, response, "AssetTransferLoanConfirmation", kualiConfiguration.getPropertyString(CamsKeyConstants.Transfer.WARN_TRFR_AST_LOAN_ACTIVE), KFSConstants.CONFIRMATION_QUESTION, "AssetTransfer", "AssetTransfer");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if (ConfirmationQuestion.NO.equals(buttonClicked)) {
                    // cancel if answer is NO
                    KNSServiceLocator.getDocumentService().cancelDocument(assetTransferDocument, assetTransferForm.getAnnotation());
                    forward = returnToSender(mapping, assetTransferForm);
                }
                else {
                    // if YES, add a loan note and route the document
                    insertLoanNote(assetTransferDocument, assetTransferForm);
                    forward = super.route(mapping, form, request, response);
                }
            }
        }
        else {
            forward = super.route(mapping, form, request, response);
        }
        return forward;
    }

    /**
     * This method adds a loan note to the document if it is not already added
     * 
     * @param document Asset Transfer document
     * @param form Form
     * @throws Exception
     */
    private void insertLoanNote(AssetTransferDocument document, AssetTransferForm form) throws Exception {
        if (!form.isLoanNoteAdded()) {
            LOG.debug("Adding loan note to the document");
            Note newNote = form.getNewNote();
            String propertyName = KNSServiceLocator.getNoteService().extractNoteProperty(newNote);
            PersistableBusinessObject noteParent = (PersistableBusinessObject) ObjectUtils.getPropertyValue(document, propertyName);
            Note tmpNote = KNSServiceLocator.getNoteService().createNote(newNote, noteParent);
            KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();
            tmpNote.setNoteText(kualiConfiguration.getPropertyString(CamsKeyConstants.Transfer.ASSET_LOAN_NOTE));
            tmpNote.refresh();
            DocumentHeader documentHeader = document.getDocumentHeader();
            noteParent.addNote(tmpNote);
            if (!documentHeader.getWorkflowDocument().stateIsInitiated() && StringUtils.isNotEmpty(noteParent.getObjectId())) {
                KNSServiceLocator.getNoteService().save(tmpNote);
            }
            form.setNewNote(new Note());
            form.setLoanNoteAdded(true);
        }
    }
}
