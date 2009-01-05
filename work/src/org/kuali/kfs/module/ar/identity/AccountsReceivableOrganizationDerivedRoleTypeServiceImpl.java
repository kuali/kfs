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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.PassThruRoleTypeServiceBase;
import org.kuali.rice.kns.service.BusinessObjectService;

public class AccountsReceivableOrganizationDerivedRoleTypeServiceImpl extends PassThruRoleTypeServiceBase {
    private static final String PROCESSOR_ROLE_NAME = "Processor";
    
    protected BusinessObjectService businessObjectService;

    @Override
    public AttributeSet convertQualificationForMemberRoles(String namespaceCode, String roleName, AttributeSet qualification) {
        
        if (PROCESSOR_ROLE_NAME.equals(roleName)) {        
            Map<String,Object> arOrgOptPk = new HashMap<String, Object>();
            arOrgOptPk.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
            arOrgOptPk.put(KfsKimAttributes.ORGANIZATION_CODE, qualification.get(KfsKimAttributes.ORGANIZATION_CODE) );
            
            OrganizationOptions oo = (OrganizationOptions)businessObjectService.findByPrimaryKey(OrganizationOptions.class, arOrgOptPk);
            if ( oo != null ) {
                // copy all the other qualification attributes
                AttributeSet nestedRoleQualification = new AttributeSet( qualification );
                // now, override the chart and organization
                nestedRoleQualification.put( KfsKimAttributes.NAMESPACE_CODE, ArConstants.AR_NAMESPACE_CODE );
                nestedRoleQualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, oo.getProcessingChartOfAccountCode() );
                nestedRoleQualification.put(KfsKimAttributes.ORGANIZATION_CODE, oo.getProcessingOrganizationCode() );
                
                return nestedRoleQualification;
            } else {
                // copy all the other qualification attributes
                AttributeSet nestedRoleQualification = new AttributeSet( qualification );
                // put in an invalid set of attributes to prevent a true response
                nestedRoleQualification.put( KfsKimAttributes.NAMESPACE_CODE, ArConstants.AR_NAMESPACE_CODE );
                nestedRoleQualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, UNMATCHABLE_QUALIFICATION);
                nestedRoleQualification.put(KfsKimAttributes.ORGANIZATION_CODE, UNMATCHABLE_QUALIFICATION);
                return nestedRoleQualification;
            }
        } else {
            AttributeSet nestedRoleQualification = new AttributeSet( qualification );
            nestedRoleQualification.put( KfsKimAttributes.NAMESPACE_CODE, ArConstants.AR_NAMESPACE_CODE );
            return nestedRoleQualification;
        } 
    }
    
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }    
}
