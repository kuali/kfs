/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ec.document.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ec.EffortConstants.EffortCertificationEditMode;
import org.kuali.kfs.module.ec.util.EffortCertificationParameterFinder;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Presentation Controller for the Effort Certification document. allowsErrorCorrection property has been set to false in
 * data dictionary entry setHasAmountTotal property has been set to true in data dictionary entry
 */

public class EffortDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canCancel(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean canCancel(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canSave(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean canSave(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canCopy(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean canCopy(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canBlanketApprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean canBlanketApprove(Document document) {
        boolean initiated = document.getDocumentHeader().getWorkflowDocument().stateIsInitiated();
        if (initiated) {
            return false;
        }

        return super.canBlanketApprove(document);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canDisapprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean canDisapprove(Document document) {
        boolean enroute = document.getDocumentHeader().getWorkflowDocument().stateIsEnroute();
        if (enroute) {
            return false;
        }

        return super.canDisapprove(document);
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        editModes.add(EffortCertificationEditMode.DETAIL_TAB_ENTRY);
        editModes.add(EffortCertificationEditMode.SUMMARY_TAB_ENTRY);

        return editModes;
    }
}