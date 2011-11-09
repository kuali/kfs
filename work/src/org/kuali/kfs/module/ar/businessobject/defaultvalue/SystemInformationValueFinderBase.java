/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject.defaultvalue;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class SystemInformationValueFinderBase {
    
    protected SystemInformation systemInformation;

    /**
     * Constructs a SystemInformationValueFinderBase.  Sets the SystemInformation BO based on current
     * year, current users chart of account code, and current users organization code
     */
    public SystemInformationValueFinderBase(){
        
        ChartOrgHolder chartUser = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);
        
        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        criteria.put("processingChartOfAccountCode", chartUser.getChartOfAccountsCode());
        criteria.put("processingOrganizationCode", chartUser.getOrganizationCode());
        systemInformation = (SystemInformation)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, criteria);
    }
}

