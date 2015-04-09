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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class RequisitionAccountingLineAccessibleValidation extends PurchasingAccountsPayableAccountingLineAccessibleValidation {

    /**
     * Validates that the given accounting line is accessible for editing by the current user.
     * <strong>This method expects a document as the first parameter and an accounting line as the second</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        RequisitionDocument requisitionDocument = (RequisitionDocument) event.getDocument();
        //for app doc status
        //to be removed
       //remove (requisitionDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.CONTENT_REVIEW) - kfsmi-4592
        if (requisitionDocument.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_CONTENT_REVIEW) ||                
            StringUtils.equals( requisitionDocument.getApplicationDocumentStatus(), PurapConstants.RequisitionStatuses.APPDOC_IN_PROCESS) ) {
            // DO NOTHING: do not check that user owns acct lines; at this level, approvers can edit all detail on REQ

            return true;
        }
        else {
            boolean result = false;
            boolean setDummyAccountIdentifier = false;
            
            if (needsDummyAccountIdentifier()) {
                ((PurApAccountingLine)getAccountingLineForValidation()).setAccountIdentifier(Integer.MAX_VALUE);  // avoid conflicts with any accouting identifier on any other accounting lines in the doc because, you know, you never know...
                setDummyAccountIdentifier = true;
            }
            
            result = super.validate(event);
            
            if (setDummyAccountIdentifier) {
                ((PurApAccountingLine)getAccountingLineForValidation()).setAccountIdentifier(null);
            }
            
            return result;
        }
    }
    
}

