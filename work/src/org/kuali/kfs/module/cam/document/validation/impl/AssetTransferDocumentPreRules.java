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
package org.kuali.module.cams.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.Document;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.web.struts.form.AssetTransferForm;
import org.kuali.rice.KNSServiceLocator;

public class AssetTransferDocumentPreRules extends PreRulesContinuationBase {

    @Override
    public boolean doRules(Document document) {
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) document;
        Asset asset = assetTransferDocument.getAsset();
        boolean confirmProceed = true;
        // If asset is loaned, ask a confirmation question
        if (asset.getExpectedReturnDate() != null && asset.getLoanReturnDate() == null) {
            KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();
            confirmProceed = super.askOrAnalyzeYesNoQuestion(CamsConstants.ASSET_LOAN_CONFIRM_QN_ID, kualiConfiguration.getPropertyString(CamsKeyConstants.Transfer.WARN_TRFR_AST_LOAN_ACTIVE));
            if (confirmProceed) {
                insertLoanNote((AssetTransferDocument) document);
            }
            else {
                super.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
            }
        }
        return confirmProceed;
    }

    /**
     * This method adds a loan note to the document if it is not already added
     * 
     * @param document Asset Transfer document
     * @param form Form
     * @throws Exception
     */
    private void insertLoanNote(AssetTransferDocument document) {
        try {
            AssetTransferForm assetTransferForm = (AssetTransferForm) super.form;
            if (!assetTransferForm.isLoanNoteAdded()) {
                LOG.debug("Adding loan note to the document");
                Note newNote = assetTransferForm.getNewNote();
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
                assetTransferForm.setNewNote(new Note());
                assetTransferForm.setLoanNoteAdded(true);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
