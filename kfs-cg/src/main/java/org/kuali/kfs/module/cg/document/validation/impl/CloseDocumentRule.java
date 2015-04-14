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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.sql.Date;

import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Rules for handling Closes.
 */
public class CloseDocumentRule extends TransactionalDocumentRuleBase {

    @Override
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        ProposalAwardCloseDocument closeDocument = (ProposalAwardCloseDocument) document;
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

        String closeDateLabel = dataDictionaryService.getAttributeLabel(ProposalAwardCloseDocument.class, "userInitiatedCloseDate");
        String closeOnOrBeforeDateLabel = dataDictionaryService.getAttributeLabel(ProposalAwardCloseDocument.class, "closeOnOrBeforeDate");

        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        if (closeDate == null) {
            closeDateIsNotNull = false;
            String label = dataDictionaryService.getAttributeLabel(ProposalAwardCloseDocument.class, "userInitiatedCloseDate");
        } else if (today.getTime() > closeDate.getTime()) {
            closeDateIsTodayOrLater = false;
            GlobalVariables.getMessageMap().putError("userInitiatedCloseDate", KFSKeyConstants.ContractsAndGrants.USER_INITIATED_DATE_TOO_EARLY, closeDateLabel);
        }
        if (closeOnOrBeforeDate == null) {
            closeOnOrBeforeDateIsNotNull = false;
        } else if (today.getTime() > closeOnOrBeforeDate.getTime()) {
            closeOnOrBeforeDateIsTodayOrLater = false;
            GlobalVariables.getMessageMap().putError("closeOnOrBeforeDate", KFSKeyConstants.ContractsAndGrants.CLOSE_ON_OR_BEFORE_DATE_TOO_EARLY, closeOnOrBeforeDateLabel);
        } else if (closeOnOrBeforeDate.getTime() > closeDate.getTime()) {
            closeOnOrBeforeDateIsCloseDateOrEarlier = false;
            GlobalVariables.getMessageMap().putError("closeOnOrBeforeDate", KFSKeyConstants.ContractsAndGrants.CLOSE_ON_OR_BEFORE_DATE_TOO_LATE, closeOnOrBeforeDateLabel, closeDateLabel);
        }
        GlobalVariables.getMessageMap().removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        isValid = closeDateIsNotNull && closeDateIsTodayOrLater && closeOnOrBeforeDateIsNotNull && closeOnOrBeforeDateIsTodayOrLater && closeOnOrBeforeDateIsCloseDateOrEarlier;
        return isValid;
    }

}
