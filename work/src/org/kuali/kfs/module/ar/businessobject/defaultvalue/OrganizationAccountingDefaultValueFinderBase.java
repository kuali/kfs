/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.lookup.valuefinder;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.bo.OrganizationAccountingDefault;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.module.financial.service.UniversityDateService;

public class OrganizationAccountingDefaultValueFinderBase {
    
    protected OrganizationAccountingDefault organizationAccountingDefault;

    /**
     * Constructs a OrganizationAccountingDefaultValueFinderBase.  Sets the OrganizationAccountingDefault BO based on current
     * year, current users chart of account code, and current users organization code
     */
    public OrganizationAccountingDefaultValueFinderBase(){        
        Integer currentUniversityFiscalYear =  SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        ChartUser chartUser = ValueFinderUtil.getCurrentChartUser();

        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", currentUniversityFiscalYear);
        criteria.put("chartOfAccountsCode", chartUser.getChartOfAccountsCode());
        criteria.put("organizationCode",  chartUser.getOrganizationCode());
        organizationAccountingDefault = (OrganizationAccountingDefault)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);                
    }
}
