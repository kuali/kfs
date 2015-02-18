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
import org.kuali.rice.krad.util.KRADConstants;

public class IndirectCostRecoveryRateMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {
    public static final String COA_CODE_NAME = KRADConstants.ADD_PREFIX + "." + KFSPropertyConstants.INDIRECT_COST_RECOVERY_RATE_DETAILS + "." + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;

    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     * 
     * This methods adds the extra chart code field in IndirectCostRecoveryRateDetails, as the potential reference account doesn't exist in
     * the collection, and thus isn't included in AccountPersistenceStructureService.listChartOfAccountsCodeNames as in the super's method.
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);

        // if accounts can't cross charts, then add the extra chartOfAccountsCode fields to be displayed readOnly
        if (!SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) { 
            readOnlyPropertyNames.add(COA_CODE_NAME);
        }
        
        return readOnlyPropertyNames;                
    }
    
}
