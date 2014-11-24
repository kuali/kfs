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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class RequisitionRequiresAccountValidationBranchingValidation extends BranchingValidation {
    
    public static final String NEEDS_ACCOUNT_VALIDATION = "needsAccountValidation";
    
    private PurApItem itemForValidation;
    
    /**
     * Requisition should only force complete accounting strings under the following cases: any accounts have been entered, document
     * is in "hasAccountingLines" route level, or if document is in "account review" route level.
     * 
     * @see org.kuali.kfs.sys.document.validation.BranchingValidation#determineBranch(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        RequisitionDocument req = (RequisitionDocument)event.getDocument();
        //for app doc status
        //to be removed
        /*remove req.isDocumentStoppedInRouteNode(NodeDetailEnum.HAS_ACCOUNTING_LINES) ||
            req.isDocumentStoppedInRouteNode(NodeDetailEnum.ACCOUNT_REVIEW) ||
            req.isDocumentStoppedInRouteNode(NodeDetailEnum.CONTENT_REVIEW) ||
            -kfsmi-4592*/
        if (req.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_HAS_ACCOUNTING_LINES) ||
                req.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_ACCOUNT) ||
                req.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_CONTENT_REVIEW) ||
                !itemForValidation.getSourceAccountingLines().isEmpty() || req.isBlanketApproveRequest()) {
                return NEEDS_ACCOUNT_VALIDATION;
        } else {
            return KFSConstants.EMPTY_STRING;
        }
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

}
