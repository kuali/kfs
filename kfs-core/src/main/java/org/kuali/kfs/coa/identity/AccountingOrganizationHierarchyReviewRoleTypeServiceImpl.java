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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeError.Builder;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;

public class AccountingOrganizationHierarchyReviewRoleTypeServiceImpl extends OrganizationHierarchyReviewRoleTypeServiceImpl {
    private static final Logger LOG = Logger.getLogger(AccountingOrganizationHierarchyReviewRoleTypeServiceImpl.class);
    /**
     * Create role type service - org.kuali.kfs.coa.identity.AccountingOrganizationHierarchyReviewRoleTypeService for
     * KFS-COA/"Organization: Always Hierarchical, Document Type & Accounting" Attributes: Chart Code (required) Organization Code
     * Document Type Name From Amount To Amount Override Code - total amount will be passed in as qualification and role type
     * service will need to compare it to from and to amount qualifier values for assignees Requirements: - Traverse the org
     * hierarchy but not the document type hierarchy - from amount must be null or <= total amount supplied / to amount must be null
     * or >= total amount supplied, and null override code on assignment matches all override codes
     *
     * @see org.kuali.kfs.coa.identity.OrganizationOptionalHierarchyRoleTypeServiceImpl#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet,
     *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean performMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {
        return doesOverrideCodeMatch(qualification, roleQualifier)
                && isValidTotalAmount(qualification, roleQualifier)
                && super.performMatch(qualification, roleQualifier);
    }

    protected boolean doesOverrideCodeMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {
        return qualification==null
                || roleQualifier==null
                || StringUtils.isBlank(qualification.get(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE))
                || StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE))
                || qualification.get(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE)
                        .equals(roleQualifier.get(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE));
    }

    protected boolean isValidTotalAmount(Map<String,String> qualification, Map<String,String> roleQualifier) {
        boolean isValidTotalAmount = false;
        if(qualification==null || roleQualifier==null) {
            return false;
        }
        String totalAmountStr = qualification.get(KfsKimAttributes.FINANCIAL_DOCUMENT_TOTAL_AMOUNT);
        if ( StringUtils.isBlank(totalAmountStr) ) {
            return false;
        }
        try {
            KualiDecimal totalAmount = new KualiDecimal(totalAmountStr);
            String toAmountStr = roleQualifier.get(KfsKimAttributes.TO_AMOUNT);
            String fromAmountStr = roleQualifier.get(KfsKimAttributes.FROM_AMOUNT);
            if ((StringUtils.isBlank(toAmountStr) || new KualiDecimal(toAmountStr).isGreaterEqual(totalAmount) )
                    && (StringUtils.isBlank(fromAmountStr) || new KualiDecimal(fromAmountStr).isLessEqual(totalAmount) )) {
                isValidTotalAmount = true;
            }
        } catch (Exception ex) {
            isValidTotalAmount = false;
            LOG.error( "Exception comparing document amount to role qualifiers.", ex );
        }
        return isValidTotalAmount;
    }

    @Override
    public List<RemotableAttributeError> validateUnmodifiableAttributes(
            String kimTypeId, Map<String,String> originalAttributeSet, Map<String,String> newAttributeSet){
        List<RemotableAttributeError> validationErrors = super.validateUnmodifiableAttributes(kimTypeId, originalAttributeSet, newAttributeSet);
        List<String> attributeErrors = null;
        KimAttribute attributeImpl;

        String fromAmountRoleMember = getAttributeValue(originalAttributeSet, KfsKimAttributes.FROM_AMOUNT);
        String fromAmountDelegationMember = getAttributeValue(newAttributeSet, KfsKimAttributes.FROM_AMOUNT);
        KimType kimType = getTypeInfoService().getKimType(kimTypeId);
        KimTypeAttribute attributeInfo;
        if(isLesserNumber(fromAmountDelegationMember, fromAmountRoleMember)){
            attributeInfo = kimType.getAttributeDefinitionById(KfsKimAttributes.FROM_AMOUNT);
            GlobalVariables.getMessageMap().putError(
                    KfsKimAttributes.FROM_AMOUNT, RiceKeyConstants.ERROR_DELEGATION_FROM_AMOUNT_LESSER,
                    getDataDictionaryService().getAttributeLabel(attributeInfo.getKimAttribute().getComponentName(), KfsKimAttributes.FROM_AMOUNT));
            attributeErrors = extractErrorsFromGlobalVariablesErrorMap(KfsKimAttributes.FROM_AMOUNT);
        }

        Builder fromBuilder = RemotableAttributeError.Builder.create(KfsKimAttributes.FROM_AMOUNT);

        if(attributeErrors!=null){
            for(String err: attributeErrors){
                fromBuilder.getErrors().add(err);
            }

            validationErrors.add(fromBuilder.build());

            attributeErrors = null;
        }

        String toAmountRoleMember = getAttributeValue(originalAttributeSet, KfsKimAttributes.TO_AMOUNT);
        String toAmountDelegationMember = getAttributeValue(newAttributeSet, KfsKimAttributes.TO_AMOUNT);
        if(StringUtils.isNotEmpty(toAmountRoleMember) && isGreaterNumber(toAmountDelegationMember, toAmountRoleMember)){
            attributeInfo = kimType.getAttributeDefinitionById(KfsKimAttributes.TO_AMOUNT);
            GlobalVariables.getMessageMap().putError(
                    KfsKimAttributes.TO_AMOUNT, RiceKeyConstants.ERROR_DELEGATION_TO_AMOUNT_GREATER,
                    getDataDictionaryService().getAttributeLabel(attributeInfo.getKimAttribute().getComponentName(), KfsKimAttributes.TO_AMOUNT));
            attributeErrors = extractErrorsFromGlobalVariablesErrorMap(KfsKimAttributes.TO_AMOUNT);
        }

        Builder toBuilder = RemotableAttributeError.Builder.create(KfsKimAttributes.TO_AMOUNT);

        if(attributeErrors!=null){


            for(String err: attributeErrors){
                toBuilder.getErrors().add(err);
            }
            validationErrors.add(toBuilder.build());

            attributeErrors = null;
        }

        return validationErrors;
    }

    protected boolean isLesserNumber(String numberStr1, String numberStr2){
        if(StringUtils.isBlank(numberStr1) ) {
            numberStr1 = "0";
        }
        if(StringUtils.isBlank(numberStr2) ) {
            numberStr2 = "0";
        }
        int number1 = KRADUtils.getIntegerValue(numberStr1);
        int number2 = KRADUtils.getIntegerValue(numberStr2);
        return number1 < number2;
    }

    protected boolean isGreaterNumber(String numberStr1, String numberStr2){
        if(StringUtils.isBlank(numberStr1) ) {
            numberStr1 = "0";
        }
        if(StringUtils.isBlank(numberStr2) ) {
            numberStr2 = "0";
        }
        int number1 = KRADUtils.getIntegerValue(numberStr1);
        int number2 = KRADUtils.getIntegerValue(numberStr2);
        return number1 > number2;
    }

}
