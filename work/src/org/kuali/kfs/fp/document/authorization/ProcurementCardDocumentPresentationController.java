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
package org.kuali.kfs.fp.document.authorization;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;

public class ProcurementCardDocumentPresentationController extends AccountingDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canCancel(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCancel(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canCopy(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCopy(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canDisapprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canDisapprove(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canEdit(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        //DocumentType
        boolean canRouteReviewFullEdit = false;
        Set<String> currentNodeNames = workflowDocument.getCurrentNodeNames();
        if (CollectionUtils.isNotEmpty(currentNodeNames)) {
            for (String routeNode : currentNodeNames ) {
                if ( StringUtils.equalsIgnoreCase(routeNode, KFSConstants.RouteLevelNames.ACCOUNT_REVIEW_FULL_EDIT ) ) {
                    canRouteReviewFullEdit = true;
                    break;
                }
            }
        }

        // FULL_ENTRY only if: a) person has an approval request, b) we are at the correct level, c) it's not a correction document,
        // d) it is not an ADHOC request (important so that ADHOC don't get full entry).
        if (canRouteReviewFullEdit
                && (((FinancialSystemDocumentHeader) document.getDocumentHeader()).getFinancialDocumentInErrorNumber() == null)
                && workflowDocument.isApprovalRequested()
                && !workflowDocument.isAcknowledgeRequested()) {
            return true;
        }

        return super.canEdit(document);
    }
}
