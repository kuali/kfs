/*
 * Copyright 2007 The Kuali Foundation
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
