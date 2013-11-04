/*
 * Copyright 2009 The Kuali Foundation.
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
