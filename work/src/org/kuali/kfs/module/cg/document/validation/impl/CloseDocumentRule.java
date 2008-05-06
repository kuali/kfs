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
package org.kuali.module.cg.rules;

import java.sql.Date;

import org.kuali.core.document.Document;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cg.bo.Close;

/**
 * Rules for handling Closes.
 */
public class CloseDocumentRule extends TransactionalDocumentRuleBase {

    @Override
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        Close closeDocument = (Close) document;
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
        Date closeDate = closeDocument.getUserInitiatedCloseDate();
        Date closeOnOrBeforeDate = closeDocument.getCloseOnOrBeforeDate();
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        
        boolean isValid = true;
        boolean closeDateIsNotNull = true;
        boolean closeDateIsTodayOrLater = true;
        boolean closeOnOrBeforeDateIsNotNull = true;
        boolean closeOnOrBeforeDateIsTodayOrLater = true;
        boolean closeOnOrBeforeDateIsCloseDateOrEarlier = true;

        String closeDateLabel = dataDictionaryService.getAttributeLabel(Close.class, "userInitiatedCloseDate");
        String closeOnOrBeforeDateLabel = dataDictionaryService.getAttributeLabel(Close.class, "closeOnOrBeforeDate");

        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        if (closeDate == null) {
            closeDateIsNotNull = false;
            String label = dataDictionaryService.getAttributeLabel(Close.class, "userInitiatedCloseDate");
        } else if (today.getTime() > closeDate.getTime()) {
            closeDateIsTodayOrLater = false;
            GlobalVariables.getErrorMap().putError("userInitiatedCloseDate", KFSKeyConstants.ContractsAndGrants.USER_INITIATED_DATE_TOO_EARLY, closeDateLabel);
        }
        if (closeOnOrBeforeDate == null) {
            closeOnOrBeforeDateIsNotNull = false;
        } else if (today.getTime() > closeOnOrBeforeDate.getTime()) {
            closeOnOrBeforeDateIsTodayOrLater = false;
            GlobalVariables.getErrorMap().putError("closeOnOrBeforeDate", KFSKeyConstants.ContractsAndGrants.CLOSE_ON_OR_BEFORE_DATE_TOO_EARLY, closeOnOrBeforeDateLabel);
        } else if (closeOnOrBeforeDate.getTime() > closeDate.getTime()) {
            closeOnOrBeforeDateIsCloseDateOrEarlier = false;
            GlobalVariables.getErrorMap().putError("closeOnOrBeforeDate", KFSKeyConstants.ContractsAndGrants.CLOSE_ON_OR_BEFORE_DATE_TOO_LATE, closeOnOrBeforeDateLabel, closeDateLabel);
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(KFSPropertyConstants.DOCUMENT);
        
        isValid = closeDateIsNotNull && closeDateIsTodayOrLater && closeOnOrBeforeDateIsNotNull && closeOnOrBeforeDateIsTodayOrLater && closeOnOrBeforeDateIsCloseDateOrEarlier;
        return isValid;
    }

}
