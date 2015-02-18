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
package org.kuali.kfs.module.ar.businessobject.defaultvalue;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class OrganizationAccountingDefaultValueFinderBase {
    
    protected OrganizationAccountingDefault organizationAccountingDefault;

    /**
     * Constructs a OrganizationAccountingDefaultValueFinderBase.  Sets the OrganizationAccountingDefault BO based on current
     * year, current users chart of account code, and current users organization code
     */
    @SuppressWarnings("unchecked")
    public OrganizationAccountingDefaultValueFinderBase(){        
        Integer currentUniversityFiscalYear =  SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        ChartOrgHolder chartUser = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);

        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", currentUniversityFiscalYear);
        criteria.put("chartOfAccountsCode", chartUser.getChartOfAccountsCode());
        criteria.put("organizationCode",  chartUser.getOrganizationCode());
        organizationAccountingDefault = (OrganizationAccountingDefault)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);                
    }
}

