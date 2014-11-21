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
package org.kuali.kfs.module.bc.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.krad.bo.BusinessObject;

public class BudgetConstructionDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

    /**
     * Add role qualifications (chart, account, year, level code) needed from document
     * 
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase#addRoleQualification(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);

        BudgetConstructionDocument document = (BudgetConstructionDocument) businessObject;
        
        attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, document.getChartOfAccountsCode());
        attributes.put(KfsKimAttributes.ACCOUNT_NUMBER, document.getAccountNumber());
        attributes.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, document.getUniversityFiscalYear().toString());
        attributes.put(BCPropertyConstants.ORGANIZATION_LEVEL_CODE, document.getOrganizationLevelCode().toString());
        attributes.put(BCPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE, document.getOrganizationLevelChartOfAccountsCode());
        attributes.put(KfsKimAttributes.ORGANIZATION_CODE, document.getOrganizationLevelOrganizationCode());
        
        if (SpringContext.getBean(BudgetDocumentService.class).isAccountReportsExist(document.getChartOfAccountsCode(), document.getAccountNumber())) {
            attributes.put(BCPropertyConstants.ACCOUNT_REPORTS_EXIST, Boolean.TRUE.toString());          
        }
        else {
            attributes.put(BCPropertyConstants.ACCOUNT_REPORTS_EXIST, Boolean.FALSE.toString());
        }
    }

}
