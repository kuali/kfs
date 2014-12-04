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
package org.kuali.kfs.sec.document.validation.impl;

import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.UpdateAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingRuleEngineRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * Hooks into rules to make access security checks for accounting documents
 */
public class AccessSecurityAccountingDocumentRuleBase extends AccountingRuleEngineRuleBase {

    /**
     * For add or update accounting line events checks the given user has access permissions for the line
     *
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingRuleEngineRuleBase#validateForEvent(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validateForEvent(AttributedDocumentEvent event) {
        boolean isValid = super.validateForEvent(event);

        if (isValid && (event instanceof AddAccountingLineEvent || event instanceof UpdateAccountingLineEvent)) {
            AccountingLineEvent accountingLineEvent = (AccountingLineEvent) event;
            isValid = checkEditAccessForAccountingLine((AccountingDocument) accountingLineEvent.getDocument(), accountingLineEvent.getAccountingLine());
        }

        return isValid;
    }

    /**
     * Calls AccessSecurityService to check access edit permissions on accounting line for the current user
     *
     * @param document AccountingDocument containing the line to check
     * @param line AccountingLine to check access on
     * @return boolean true if user is allowed to edit the accounting line, false if the user is not allowed to
     */
    protected boolean checkEditAccessForAccountingLine(AccountingDocument document, AccountingLine line) {
        boolean editAccessAllowed = true;

        AccessSecurityRestrictionInfo restrictionInfo = new AccessSecurityRestrictionInfo();
        boolean hasEditAccessPermission = getAccessSecurityService().canEditDocumentAccountingLine(document, line, GlobalVariables.getUserSession().getPerson(), restrictionInfo);

        if (!hasEditAccessPermission) {
            GlobalVariables.getMessageMap().putError(restrictionInfo.getPropertyName(), SecKeyConstants.ERROR_ACCOUNTING_LINE_ADD_OR_UPDATE, restrictionInfo.getPropertyLabel(), restrictionInfo.getRetrictedValue());
            editAccessAllowed = false;
        }

        return editAccessAllowed;
    }
    private static AccessSecurityService accessSecurityService;
    protected AccessSecurityService getAccessSecurityService() {
        if ( accessSecurityService == null ) {
            accessSecurityService = SpringContext.getBean(AccessSecurityService.class);
        }
        return accessSecurityService;
    }
}
