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
