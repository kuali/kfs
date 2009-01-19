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
            // if the processing org is specified from the qualifications, use that
            String processingChart = qualification.get(PROCESSING_CHART_OF_ACCOUNTS_CODE);
            String processingOrg = qualification.get(PROCESSING_ORGANIZATION_CODE);
            // however, if the processing chart/org is not specified, pull the OrgOptions record for the
            // chart/org and get the processing org off that
            if (StringUtils.isBlank(processingChart) || StringUtils.isBlank(processingChart)) {
                String chart = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
                String org = qualification.get(KfsKimAttributes.ORGANIZATION_CODE);
                // if no chart/org pair is is available, then skip the conversion attempt 
                if (!StringUtils.isBlank(chart) && !StringUtils.isBlank(org)) {
                    Map<String, Object> arOrgOptPk = new HashMap<String, Object>();
                    arOrgOptPk.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chart);
                    arOrgOptPk.put(KfsKimAttributes.ORGANIZATION_CODE, org);
                    OrganizationOptions oo = (OrganizationOptions) businessObjectService.findByPrimaryKey(OrganizationOptions.class, arOrgOptPk);
                    if (oo != null) {
                        processingChart = oo.getProcessingChartOfAccountCode();
                        processingOrg = oo.getProcessingOrganizationCode();
                    }
                    else {
                        processingChart = UNMATCHABLE_QUALIFICATION;
                        processingOrg = UNMATCHABLE_QUALIFICATION;
                    }
                } else {
                    // do nothing - don't pass through to the nested role
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
