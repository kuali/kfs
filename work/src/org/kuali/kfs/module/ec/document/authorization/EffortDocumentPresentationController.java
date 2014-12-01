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
package org.kuali.kfs.module.ec.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.ec.EffortConstants.EffortCertificationEditMode;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.krad.document.Document;

/**
 * Document Presentation Controller for the Effort Certification document. allowsErrorCorrection property has been set to false in
 * data dictionary entry setHasAmountTotal property has been set to true in data dictionary entry
 */

public class EffortDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canCancel(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCancel(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canSave(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canSave(Document document) {
        return true;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canCopy(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCopy(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canBlanketApprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canBlanketApprove(Document document) {
        boolean initiated = document.getDocumentHeader().getWorkflowDocument().isInitiated();
        if (initiated) {
            return false;
        }

        return super.canBlanketApprove(document);
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canDisapprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canDisapprove(Document document) {
        boolean enroute = document.getDocumentHeader().getWorkflowDocument().isEnroute();
        if (enroute) {
            return false;
        }

        return super.canDisapprove(document);
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        editModes.add(EffortCertificationEditMode.DETAIL_TAB_ENTRY);
        editModes.add(EffortCertificationEditMode.SUMMARY_TAB_ENTRY);

        return editModes;
    }
}
