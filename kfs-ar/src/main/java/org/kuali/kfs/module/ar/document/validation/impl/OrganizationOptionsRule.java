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
import org.kuali.kfs.sys.KFSPropertyConstants;
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
                ErrorMessage eMessage = errorMessages.get(i);
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
        criteria.put(KFSPropertyConstants.PROCESSING_CHART_OF_ACCT_CD, processingChartOfAccountCode);
        criteria.put(KFSPropertyConstants.PROCESSING_ORGANIZATION_CODE, processingOrganizationCode);

        SystemInformation systemInformation = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, criteria);

        if (ObjectUtils.isNull(systemInformation)) {
            putFieldError(KFSPropertyConstants.PROCESSING_CHART_OF_ACCT_CD, ArKeyConstants.OrganizationOptionsErrors.SYS_INFO_DOES_NOT_EXIST_FOR_PROCESSING_CHART_AND_ORG, new String[] { processingChartOfAccountCode, processingOrganizationCode });
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
