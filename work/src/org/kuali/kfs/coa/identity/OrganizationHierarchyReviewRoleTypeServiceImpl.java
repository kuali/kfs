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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KimAttributes;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

public class OrganizationHierarchyReviewRoleTypeServiceImpl extends OrganizationHierarchyAwareRoleTypeServiceBase {

    protected List<String> roleQualifierRequiredAttributes = new ArrayList<String>();
    protected List<String> qualificationRequiredAttributes = new ArrayList<String>();
    {
        roleQualifierRequiredAttributes.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        roleQualifierRequiredAttributes.add(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL);
        
        qualificationRequiredAttributes.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        qualificationRequiredAttributes.add(KFSPropertyConstants.ORGANIZATION_CODE);
        qualificationRequiredAttributes.add(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL);
    }
    
    /**
        Attributes:
        Chart Code (required)
        Organization Code
        Document Type Name
        
        Requirement - Traverse the org hierarchy but not the document type hierarchy
        
     * @see org.kuali.kfs.coa.identity.OrganizationOptionalHierarchyRoleTypeServiceImpl#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        validateRequiredAttributesAgainstReceived(
                qualificationRequiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        validateRequiredAttributesAgainstReceived(
                roleQualifierRequiredAttributes, roleQualifier, ROLE_QUALIFIERS_RECEIVED_ATTIBUTES_NAME);

        if(isParentOrg(qualification.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), 
                    qualification.get(KFSPropertyConstants.ORGANIZATION_CODE), 
                    roleQualifier.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), 
                    roleQualifier.get(KFSPropertyConstants.ORGANIZATION_CODE), 
                    true)
                && 
                qualification.get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL).equalsIgnoreCase(
                        roleQualifier.get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL))){
            return true;
        }
        return false;
    }

}