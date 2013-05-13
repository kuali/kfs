/*
 * Copyright 2010 The Kuali Foundation.
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

package org.kuali.kfs.coa.document.authorization;

import java.util.Set;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;


/**
 * This class can be shared by all account-involved maintenance documents which have special nested reference accounts.
 */
public class SubAccountMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {
    
    // COA code fields that are PKs of nested reference accounts but don't exist in the Sub-Account BO as FKs.
    public static final String[] COA_CODE_NAMES = {        
        KFSPropertyConstants.A21_SUB_ACCOUNT + "." + KFSPropertyConstants.COST_SHARE_SOURCE_CHART_OF_ACCOUNTS_CODE, 
    };

    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     * 
     * This methods adds the extra COA code fields that are PKs of nested reference accounts but don't exist in the BO as FKs
     * to the readOnlyPropertyNames set when accounts can't cross charts. 
     * Since these fields aren't included in AccountPersistenceStructureService.listChartOfAccountsCodeNames as 
     * in super.getConditionallyReadOnlyPropertyNames, they need to be added individually for such special cases.
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);

        // if accounts can't cross charts, then add the extra chartOfAccountsCode fields to be displayed readOnly
        if (!SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) {
            for (int i=0; i<COA_CODE_NAMES.length; i++) {
                readOnlyPropertyNames.add(COA_CODE_NAMES[i]);
            }
        }
        
        return readOnlyPropertyNames;                
    }
    
}
