/*
 * Copyright 2005-2006 The Kuali Foundation.
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
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cg.bo.Close;

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
        if(!isOk) {
            return isOk;
        }
        Close close = (Close) document.getDocumentBusinessObject();
        Date userDate = close.getUserInitiatedCloseDate();
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
        isOk = today.getTime() <= userDate.getTime();
        if(!isOk) {
            putFieldError("userInitiatedCloseDate", KFSKeyConstants.ContractsAndGrants.USER_INITIATED_DATE_TOO_EARLY, userDate.toString());
        }
        return isOk;
    }
}
