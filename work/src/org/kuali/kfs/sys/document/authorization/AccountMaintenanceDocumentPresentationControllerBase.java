/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.authorization;

import java.util.Set;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class AccountMaintenanceDocumentPresentationControllerBase extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    public static String[] coaPropertyNames = {
        KFSPropertyConstants.REPORTS_TO_CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ENDOWMENT_INCOME_CHART_OF_ACCOUNTS_CODE,
        KFSPropertyConstants.CONTINUATION_CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.INCOME_STREAM_CHART_OF_ACCOUNTS_CODE,
        KFSPropertyConstants.CONTRACT_CONTROL_CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.INDIRECT_COST_RECOVERY_CHART_OF_ACCOUNTS_CODE,
        KFSPropertyConstants.COST_SHARE_SOURCE_CHART_OF_ACCOUNTS_CODE
    };

    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);

        if (!SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) {
            for (int i=0; i<coaPropertyNames.length; i++) {
                readOnlyPropertyNames.add(coaPropertyNames[i]);
            }
        }
        
        return readOnlyPropertyNames;                
    }
}
