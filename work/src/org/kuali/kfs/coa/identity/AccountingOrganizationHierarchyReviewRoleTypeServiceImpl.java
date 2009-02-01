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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimDelegationMemberInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.KimDelegationTypeService;

public class AccountingOrganizationHierarchyReviewRoleTypeServiceImpl 
        extends OrganizationHierarchyReviewRoleTypeServiceImpl implements KimDelegationTypeService {

    /**
     * Create role type service - org.kuali.kfs.coa.identity.AccountingOrganizationHierarchyReviewRoleTypeService 
     * for KFS-COA/"Organization: Always Hierarchical, Document Type & Accounting"

        Attributes:
        Chart Code (required)
        Organization Code
        Document Type Name
        From Amount
        To Amount
        Override Code
        - total amount will be passed in as qualification and role type service will need to 
        compare it to from and to amount qualifier values for assignees
        
        Requirements:
        - Traverse the org hierarchy but not the document type hierarchy - from amount must be null or <= total amount 
        supplied / to amount must be null or >= total amount supplied, and null override code on assignment matches all override codes

     * @see org.kuali.kfs.coa.identity.OrganizationOptionalHierarchyRoleTypeServiceImpl#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        boolean orgHierarchyDocTypeMatch = super.performMatch(qualification, roleQualifier);

        return orgHierarchyDocTypeMatch 
                && doesOverrideCodeMatch(qualification, roleQualifier)
                && isValidTotalAmount(qualification, roleQualifier);
    }

    private boolean doesOverrideCodeMatch(AttributeSet qualification, AttributeSet roleQualifier){
        String overrideCode = qualification.get(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE);
        String overrideCodeQualifier = roleQualifier.get(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE);
        if(overrideCodeQualifier==null || overrideCode.equals(overrideCodeQualifier))
            return true;
        return false;
    }

    private boolean isValidTotalAmount(AttributeSet qualification, AttributeSet roleQualifier){
        boolean isValidTotalAmount = false;
        try{
            int totalAmount = new Integer(qualification.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT)).intValue();
            String toAmountStr = roleQualifier.get(KfsKimAttributes.TO_AMOUNT);
            String fromAmountStr = roleQualifier.get(KfsKimAttributes.FROM_AMOUNT);
            if((StringUtils.isEmpty(toAmountStr) || new Integer(toAmountStr).intValue()>totalAmount) &&
                    (StringUtils.isEmpty(fromAmountStr) || new Integer(fromAmountStr).intValue()<totalAmount)){
                isValidTotalAmount = true;
            }
        } catch(Exception ex){
            isValidTotalAmount = false;
        }
        return isValidTotalAmount;
    }

    
    public boolean doesDelegationQualifierMatchQualification(AttributeSet qualification, AttributeSet delegationQualifier){
        return performMatch(translateInputAttributeSet(qualification), delegationQualifier);
    }
    
    public List<KimDelegationMemberInfo> doDelegationQualifiersMatchQualification(
            AttributeSet qualification, List<KimDelegationMemberInfo> delegationMemberList){
        AttributeSet translatedQualification = translateInputAttributeSet(qualification);
        List<KimDelegationMemberInfo> matchingMemberships = new ArrayList<KimDelegationMemberInfo>();
        for ( KimDelegationMemberInfo dmi : delegationMemberList ) {
            if ( performMatch( translatedQualification, dmi.getQualifier() ) ) {
                matchingMemberships.add( dmi );
            }
        }
        return matchingMemberships;
    }

    public AttributeSet convertQualificationAttributesToRequired(AttributeSet qualificationAttributes){
        return qualificationAttributes;
    }

}