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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.AutoPopulatingList;

public class OrganizationOptionsRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationOptionsRule.class);

    protected OrganizationOptions newOrganizationOptions;
    protected OrganizationOptions oldOrganizationOptions;

    @Override
    public void setupConvenienceObjects() {
        newOrganizationOptions = (OrganizationOptions) super.getNewBo();
        oldOrganizationOptions = (OrganizationOptions) super.getOldBo();
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        success &= doesSystemInformationExistForProcessingChartAngOrg(newOrganizationOptions);
        success &= isOrganizationOptionsPostalZipCodeNotEmpty(newOrganizationOptions);
        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // always return true even if there are business rule failures.
        processCustomRouteDocumentBusinessRules(document);
        return true;

    }

    // TODO method is gone - fix for kim / session work
//    /**
//     * 
//     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#checkAuthorizationRestrictions(org.kuali.rice.kns.document.MaintenanceDocument)
//     */
//    @Override
//    protected boolean checkAuthorizationRestrictions(MaintenanceDocument document) {
//        boolean success = super.checkAuthorizationRestrictions(document);
//
//        MessageMap map = GlobalVariables.getMessageMap();
//        
//        if(map.containsMessageKey(KFSKeyConstants.ERROR_DOCUMENT_AUTHORIZATION_RESTRICTED_FIELD_CHANGED)) {
//            removeRestrictedFieldChangedErrors(map, KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE);
//            removeRestrictedFieldChangedErrors(map, KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.OrganizationOptionsFields.PROCESSING_ORGANIZATION_CODE);
//            removeRestrictedFieldChangedErrors(map, KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_ADDRESS_NAME);
//            removeRestrictedFieldChangedErrors(map, KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_LINE1_STREET_ADDRESS);
//            removeRestrictedFieldChangedErrors(map, KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_LINE2_STREET_ADDRESS);
//            removeRestrictedFieldChangedErrors(map, KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_CITY_NAME);
//            removeRestrictedFieldChangedErrors(map, KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_STATE_CODE);
//            removeRestrictedFieldChangedErrors(map, KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_ZIP_CODE);
//        }
//        
//        return success;
//    }

    /**
     * This method...
     * @param map
     */
    protected void removeRestrictedFieldChangedErrors(MessageMap map, String propertyKey) {
        AutoPopulatingList<ErrorMessage> errorMessages = map.getErrorMessagesForProperty(propertyKey);
        if(errorMessages!=null) {
            for(int i=0; i<errorMessages.size(); i++) {
                ErrorMessage eMessage = (ErrorMessage)errorMessages.get(i);
                String errorKey = eMessage.getErrorKey();
                if(errorKey.equals(KFSKeyConstants.ERROR_DOCUMENT_AUTHORIZATION_RESTRICTED_FIELD_CHANGED)) {
                    errorMessages.remove(i);
                }
            }
        }
    }
        
    /**
     * This method this returns true if system information row exists for processing chart and org
     * 
     * @param organizationOptions
     * @return
     */
    public boolean doesSystemInformationExistForProcessingChartAngOrg(OrganizationOptions organizationOptions) {
        boolean success = true;

        String processingChartOfAccountCode = organizationOptions.getProcessingChartOfAccountCode();
        String processingOrganizationCode = organizationOptions.getProcessingOrganizationCode();

        Map criteria = new HashMap();
        criteria.put(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        criteria.put(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, processingChartOfAccountCode);
        criteria.put(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_ORGANIZATION_CODE, processingOrganizationCode);

        SystemInformation systemInformation = (SystemInformation) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, criteria);

        if (ObjectUtils.isNull(systemInformation)) {
            putFieldError(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, ArKeyConstants.OrganizationOptionsErrors.SYS_INFO_DOES_NOT_EXIST_FOR_PROCESSING_CHART_AND_ORG, new String[] { processingChartOfAccountCode, processingOrganizationCode });
            success = false;
        }
        return success;
    }

    /**
     * This method returns false if org option postal code is empty and sales tax indicator is set to Y.  Else, returns true.
     * @param organizationOptions
     * @return
     */
    public boolean isOrganizationOptionsPostalZipCodeNotEmpty(OrganizationOptions organizationOptions) {
        boolean success = true;

        // check if sales tax is enabled && if org postal code is empty
        if (SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.ACCOUNTS_RECEIVABLE_DOCUMENT.class, ArConstants.ENABLE_SALES_TAX_IND) && StringUtils.isBlank(organizationOptions.getOrganizationPostalZipCode())) {
            putFieldError(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_POSTAL_ZIP_CODE, ArKeyConstants.OrganizationOptionsErrors.ERROR_ORG_OPTIONS_ZIP_CODE_REQUIRED);
            success = false;
        }

        return success;
    }
}
