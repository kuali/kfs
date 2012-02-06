/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
