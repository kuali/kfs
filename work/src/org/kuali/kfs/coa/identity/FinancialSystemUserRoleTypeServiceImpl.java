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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;



/**
 * This role type service was designed to be attached to the KFS-SYS User role
 */
public class FinancialSystemUserRoleTypeServiceImpl extends RoleTypeServiceBase {
    public static final String PERFORM_QUALIFIER_MATCH = "performQualifierMatch";


    @Override
    protected boolean performMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {
        // if we can not find the qualifier which tells us to perform the match, just return true
        if (qualification == null || !Boolean.parseBoolean(qualification.get(PERFORM_QUALIFIER_MATCH))) {
            return true;
        }
        // if chart and org aren't on the assignment we don't care, since we are only matching to get the default chart/org for a
        // user in FinancialSystemUserServiceImpl
        if (StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)) || StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE))) {
            return false;
        }
        // exact match on namespace or qualifier namespace of kfs-sys works
        if (StringUtils.equals(roleQualifier.get(KimConstants.AttributeConstants.NAMESPACE_CODE), qualification.get(KimConstants.AttributeConstants.NAMESPACE_CODE))
                || StringUtils.equals(KFSConstants.CoreModuleNamespaces.KFS, roleQualifier.get(KimConstants.AttributeConstants.NAMESPACE_CODE))) {
            return true;
        }
        return false;
    }

    @Override
    public List<RoleMembership> getMatchingRoleMemberships(Map<String,String> qualification, List<RoleMembership> roleMemberList) {
        Map<String,String> translatedQualification = translateInputAttributes(qualification);
        validateRequiredAttributesAgainstReceived(translatedQualification);
        // if we can not find the qualifier which tells us to perform the match, just return all rows
        if (translatedQualification == null || translatedQualification.isEmpty() || !Boolean.parseBoolean(translatedQualification.get(PERFORM_QUALIFIER_MATCH))) {
            return roleMemberList;
        }
        // perform special matching to make a match that includes the namespace take priority
        RoleMembership namespaceMatch = null;
        RoleMembership nonNamespaceMatch = null;
        // look for match
        for (RoleMembership rmi : roleMemberList) {
            // if chart and org aren't on the assignment we don't care, since we are only matching to get the default chart/org for
            // a user in FinancialSystemUserServiceImpl
            if (!(StringUtils.isBlank(rmi.getQualifier().get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)) || StringUtils.isBlank(rmi.getQualifier().get(KfsKimAttributes.ORGANIZATION_CODE)))) {
                if (StringUtils.equals(translatedQualification.get(KimConstants.AttributeConstants.NAMESPACE_CODE), rmi.getQualifier().get(KimConstants.AttributeConstants.NAMESPACE_CODE))) {
                    namespaceMatch = rmi;
                }
                else if (StringUtils.equals(KFSConstants.ParameterNamespaces.KFS, rmi.getQualifier().get(KimConstants.AttributeConstants.NAMESPACE_CODE))) {
                    nonNamespaceMatch = rmi;
                }
            }
        }
        List<RoleMembership> matchingMemberships = new ArrayList<RoleMembership>(1);
        if (nonNamespaceMatch != null || namespaceMatch != null) {
            matchingMemberships.add((namespaceMatch != null) ? namespaceMatch : nonNamespaceMatch);
        }
        return matchingMemberships;
    }

    /**
     * note: for validating KFS-SYS User membership
     *   - if chart or org are specified, chart, org, and namespace are all required
     *   - none are required if not
     *   - can only have one assignment to the role for a given namespace - including no namespace
     *
     * @see org.kuali.rice.kim.service.support.impl.KimTypeInfoServiceBase#validateAttributes(org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<RemotableAttributeError> validateAttributes(String kimTypeId, Map<String,String> attributes) {
        List<RemotableAttributeError> errorList = new ArrayList <RemotableAttributeError>();
        errorList.addAll(super.validateAttributes(kimTypeId, attributes));
        String chartCode = attributes.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String organizationCode = attributes.get(KfsKimAttributes.ORGANIZATION_CODE);
        String namespaceCode = attributes.get(KimConstants.AttributeConstants.NAMESPACE_CODE);
        if(StringUtils.isBlank(chartCode) && StringUtils.isBlank(organizationCode)){
            //remove chartofAccountCode, organizationCode and namespaceCode errors
            //Object results = GlobalVariables.getMessageMap().getErrorMessagesForProperty(attributeName);
            //RiceKeyConstants.ERROR_REQUIRED
            removeError(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, errorList );
            removeError(KfsKimAttributes.ORGANIZATION_CODE, errorList );
            if(StringUtils.isBlank(namespaceCode)) {
                removeError(KimConstants.AttributeConstants.NAMESPACE_CODE, errorList );
            }
        } //- if chart or org are specified, chart, org, and namespace are all required
          //- none are required if not
        else if (StringUtils.isNotEmpty(chartCode) || StringUtils.isNotEmpty(organizationCode)){
            if(StringUtils.isBlank(chartCode) || StringUtils.isBlank(organizationCode) || StringUtils.isBlank(namespaceCode)) {
                errorList.add(RemotableAttributeError.Builder.create(KimConstants.AttributeConstants.NAMESPACE_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED).build());
            }
        }

        return errorList;
    }

    protected void removeError( String attributeName, Collection<RemotableAttributeError> errorList ) {
        Iterator<RemotableAttributeError> iter = errorList.iterator();
        while ( iter.hasNext() ) {
            if ( StringUtils.equals( iter.next().getAttributeName(), attributeName ) ) {
                iter.remove();
            }
        }
    }

    @Override
    public List<RemotableAttributeError> validateUniqueAttributes(String kimTypeId, Map<String,String> newAttributes, Map<String,String> oldAttributes){

        if(areAllAttributeValuesEmpty(newAttributes)){
            return Collections.emptyList();
        } else
            return super.validateUniqueAttributes(kimTypeId, newAttributes, oldAttributes);
    }

    protected boolean areAllAttributeValuesEmpty( Map<String,String> attributes){
        boolean areAllAttributesEmpty = true;
        if(attributes!=null)
            for(String attributeNameKey: attributes.keySet()){
                if(StringUtils.isNotBlank(attributes.get(attributeNameKey))){
                    areAllAttributesEmpty = false;
                    break;
                }
            }
        return areAllAttributesEmpty;
    }

    @Override
    public List<String> getUniqueAttributes(String kimTypeId){
        return Collections.singletonList(KimConstants.AttributeConstants.NAMESPACE_CODE);
    }

}
