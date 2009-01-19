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
package org.kuali.kfs.module.ar.identity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.PassThruRoleTypeServiceBase;
import org.kuali.rice.kns.service.BusinessObjectService;

public class AccountsReceivableOrganizationDerivedRoleTypeServiceImpl extends PassThruRoleTypeServiceBase {
    private static final String PROCESSOR_ROLE_NAME = "Processor";
    public static final String PROCESSING_CHART_OF_ACCOUNTS_CODE = "processing" + KfsKimAttributes.CHART_OF_ACCOUNTS_CODE;
    public static final String PROCESSING_ORGANIZATION_CODE = "processing" + KfsKimAttributes.ORGANIZATION_CODE;

    protected BusinessObjectService businessObjectService;

    @Override
    public AttributeSet convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, AttributeSet qualification) {
        AttributeSet nestedRoleQualification = new AttributeSet(qualification);
        nestedRoleQualification.put(KfsKimAttributes.NAMESPACE_CODE, ArConstants.AR_NAMESPACE_CODE);
        if (PROCESSOR_ROLE_NAME.equals(roleName)) {
            String processingChart = null;
            if (qualification.containsKey(PROCESSING_CHART_OF_ACCOUNTS_CODE)) {
                processingChart = qualification.get(PROCESSING_CHART_OF_ACCOUNTS_CODE);
            }
            String processingOrg = null;
            if (qualification.containsKey(PROCESSING_ORGANIZATION_CODE)) {
                processingOrg = qualification.get(PROCESSING_ORGANIZATION_CODE);
            }
            if (StringUtils.isBlank(processingChart) || StringUtils.isBlank(processingChart)) {
                Map<String, Object> arOrgOptPk = new HashMap<String, Object>();
                arOrgOptPk.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
                arOrgOptPk.put(KfsKimAttributes.ORGANIZATION_CODE, qualification.get(KfsKimAttributes.ORGANIZATION_CODE));
                OrganizationOptions oo = (OrganizationOptions) businessObjectService.findByPrimaryKey(OrganizationOptions.class, arOrgOptPk);
                if (oo != null) {
                    processingChart = oo.getProcessingChartOfAccountCode();
                    processingOrg = oo.getProcessingOrganizationCode();
                }
                else {
                    processingChart = UNMATCHABLE_QUALIFICATION;
                    processingOrg = UNMATCHABLE_QUALIFICATION;
                }
            }
            nestedRoleQualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, processingChart);
            nestedRoleQualification.put(KfsKimAttributes.ORGANIZATION_CODE, processingOrg);
        }
        return nestedRoleQualification;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
