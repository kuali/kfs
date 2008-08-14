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
package org.kuali.kfs.module.cam.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.AssetTransferDocument;
import org.kuali.kfs.module.cam.document.web.struts.AssetTransferForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.PreRulesContinuationBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;

public class AssetTransferDocumentPreRules extends PreRulesContinuationBase {

    /**
     * This method asks a question to confirm if transfer needs to be proceeded for a loaned equipment
     * 
     * @see org.kuali.rice.kns.rules.PreRulesContinuationBase#doRules(org.kuali.rice.kns.document.Document)
     */
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
