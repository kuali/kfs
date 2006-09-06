/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.budget.rules;

import org.kuali.core.document.Document;
import org.kuali.core.rule.DocumentRuleBase;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.kra.budget.document.ResearchDocument;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ResearchDocumentRuleBase extends DocumentRuleBase {

    /**
     * @see org.kuali.core.rule.RouteDocumentRule#processRouteDocument(org.kuali.core.document.Document)
     */
    @Override
    public boolean processRouteDocument(Document document) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @see org.kuali.core.rule.ApproveDocumentRule#processApproveDocument(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    @Override
    public boolean processApproveDocument(ApproveDocumentEvent approveEvent) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @see org.kuali.core.rule.SaveDocumentRule#processSaveDocument(org.kuali.core.document.Document)
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
