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
package org.kuali.kfs.module.endow.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.document.UnitsTotaling;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;

public class EndowmentTransactionalDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        if (document instanceof UnitsTotaling) {
            editModes.add(EndowConstants.UNITS_TOTALING_EDITING_MODE);
        }
        return editModes;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canEdit(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        FinancialSystemDocumentHeader documentheader = (FinancialSystemDocumentHeader) (document.getDocumentHeader());

        if (workflowDocument.isCanceled() || documentheader.getFinancialDocumentInErrorNumber() != null) {
            return false;
        }
        else
            return super.canEdit(document);
    }
}
