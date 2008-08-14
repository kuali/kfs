/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.document.validation.impl;

import org.kuali.kfs.module.cg.document.ResearchDocument;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.event.ApproveDocumentEvent;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This class...
 */
public class ResearchDocumentRuleBase extends DocumentRuleBase {

    /**
     * @see org.kuali.rice.kns.rule.RouteDocumentRule#processRouteDocument(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean processRouteDocument(Document document) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @see org.kuali.rice.kns.rule.ApproveDocumentRule#processApproveDocument(org.kuali.rice.kns.rule.event.ApproveDocumentEvent)
     */
    @Override
    public boolean processApproveDocument(ApproveDocumentEvent approveEvent) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @see org.kuali.rice.kns.rule.SaveDocumentRule#processSaveDocument(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean processSaveDocument(Document document) {
        ResearchDocument researchDocument = (ResearchDocument) document;

        boolean isValidForSave;

        GlobalVariables.getErrorMap().addToErrorPath("document");

        if (isDocumentValidForSave(researchDocument)) {
            isValidForSave = processCustomSaveDocumentBusinessRules(researchDocument);
        }
        else {
            isValidForSave = false;
        }

        GlobalVariables.getErrorMap().removeFromErrorPath("document");

        return isValidForSave;
    }

    /**
     * This method should be overridden by children rule classes as a hook to implement document specific business rule checks for
     * the "save document" event.
     * 
     * @param document
     * @return boolean True if the rules checks passed, false otherwise.
     */
    protected boolean processCustomSaveDocumentBusinessRules(ResearchDocument document) {
        return true;
    }

    /**
     * Performs common validation for Research Document saves.
     * 
     * @param transactionalDocument
     * @return boolean True if the document is valid for saving, false otherwise.
     */
    protected boolean isDocumentValidForSave(ResearchDocument researchDocument) {
        boolean valid = true;

        return valid;
    }

}
