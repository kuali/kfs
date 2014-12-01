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
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.document.Document;

/**
 * 
 */
public class ProposalCloseRule extends MaintenanceDocumentRuleBase {

    /**
     * @see MaintenanceDocumentRuleBase#processSaveDocument(Document)
     */
    @Override
    public boolean processSaveDocument(Document document) {
        boolean isOk = super.processSaveDocument(document);
        if (!isOk) {
            return isOk;
        }
        ProposalAwardCloseDocument close = (ProposalAwardCloseDocument) document;
        Date userDate = close.getUserInitiatedCloseDate();
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
        isOk = today.getTime() <= userDate.getTime();
        if (!isOk) {
            putFieldError("userInitiatedCloseDate", KFSKeyConstants.ContractsAndGrants.USER_INITIATED_DATE_TOO_EARLY, userDate.toString());
        }
        return isOk;
    }
}
