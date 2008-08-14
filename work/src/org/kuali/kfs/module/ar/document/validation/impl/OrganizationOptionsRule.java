/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;

public class OrganizationOptionsRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationOptionsRule.class);
    
    private OrganizationOptions newOrganizationOptions;
    private OrganizationOptions oldOrganizationOptions;

    @Override
    public void setupConvenienceObjects() {
        newOrganizationOptions = (OrganizationOptions) super.getNewBo();
        oldOrganizationOptions = (OrganizationOptions) super.getOldBo();
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        success &= doesSystemInformationExistForProcessingChartAngOrg(newOrganizationOptions);
        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // always return true even if there are business rule failures.
        processCustomRouteDocumentBusinessRules(document);
        return true;

    }    
    
    /**
     * This method this returns true if system information row exists for processing chart and org
     * 
     * @param organizationOptions
     * @return
     */
    public boolean doesSystemInformationExistForProcessingChartAngOrg(OrganizationOptions organizationOptions){
        boolean success = true;
        
        String processingChartOfAccountCode = organizationOptions.getProcessingChartOfAccountCode();
        String processingOrganizationCode = organizationOptions.getProcessingOrganizationCode();
        
        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        criteria.put("processingChartOfAccountCode", processingChartOfAccountCode);
        criteria.put("processingOrganizationCode", processingOrganizationCode);
        
        SystemInformation systemInformation = (SystemInformation)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, criteria);
        
        if( ObjectUtils.isNull(systemInformation) ){
            putFieldError(ArConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, ArConstants.OrganizationOptionsErrors.SYS_INFO_DOES_NOT_EXIST_FOR_PROCESSING_CHART_AND_ORG, new String[]{ processingChartOfAccountCode, processingOrganizationCode } );
            success = false;
        }
        return success;
    }
}
