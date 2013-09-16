/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.OrganizationHierarchyAwareRoleTypeServiceBase;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.type.KimTypeAttribute;

/**
 * Role type service which assigns members by organization.  If attributes reimbursementAmount and authorizationAmount are passed in, members
 * will only match if they are members of the correct organizations and if the difference between the reimbursementAmount and the authorizationAmount
 * is greater than or equal to the set reimbursementOveragePercentage
 */
public class ReimbursementOverageOrganizationHierarchyRoleTypeServiceImpl extends OrganizationHierarchyAwareRoleTypeServiceBase {
    /**
     *
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#performMatch(java.util.Map, java.util.Map)
     */
    @Override
    protected boolean performMatch(Map<String, String> inputAttributes, Map<String, String> storedAttributes) {
        if (isParentOrg(inputAttributes.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), inputAttributes.get(KfsKimAttributes.ORGANIZATION_CODE), storedAttributes.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), storedAttributes.get(KfsKimAttributes.ORGANIZATION_CODE), true)) {
            if (inputAttributes.containsKey(TemKimAttributes.REIMBURSEMENT_AMOUNT) && inputAttributes.containsKey(TemKimAttributes.AUTHORIZATION_AMOUNT)) {
                return reimbursementIsGreaterThanAuthorizationByOverage(inputAttributes.get(TemKimAttributes.REIMBURSEMENT_AMOUNT), inputAttributes.get(TemKimAttributes.AUTHORIZATION_AMOUNT), storedAttributes.get(TemKimAttributes.REIMBURESEMENT_OVERAGE_PERCENTAGE));
            } else {
                return true; // no reimbursementAmount and authorizationAmount? then only do the org check
            }
        }
        return false;
    }

    /**
     * Parses all the passed in values and determines if the difference between the reimbursement amount and the authorization amount should trigger a match
     * @param reimbursementAmountAsString the unparsed reimbursement amount
     * @param authorizationAmountAsString the unparsed authorization amount
     * @param reimbursementOveragePercentageAsString the unparsed reimbursement overage percentage amount
     * @return true if:
     * <ol>
     * <li>the reimbursement overage percentage is blank or 0</li>
     * <li>the reimbursement was larger than the authorization and the difference between the two was greater than or equal to the reimbursementOveragePercentage</li>
     * </ol>
     * and false otherwise
     */
    protected boolean reimbursementIsGreaterThanAuthorizationByOverage(String reimbursementAmountAsString, String authorizationAmountAsString, String reimbursementOveragePercentageAsString) {
        if (StringUtils.isBlank(reimbursementOveragePercentageAsString)) {
            return true;
        }

        final KualiDecimal reimbursementOveragePercentage = new KualiDecimal(reimbursementOveragePercentageAsString);
        if (reimbursementOveragePercentage.isZero()) {
            return true;
        }

        try {
            final KualiDecimal reimbursementAmount = new KualiDecimal(reimbursementAmountAsString);
            final KualiDecimal authorizationAmount = new KualiDecimal(authorizationAmountAsString);

            if (KualiDecimal.ZERO.isGreaterEqual(reimbursementAmount) || KualiDecimal.ZERO.isGreaterEqual(authorizationAmount)) {
                return false; // reimbursement total or authorization total are equal to or less than 0; let's not trigger a match
            }

            if (authorizationAmount.isGreaterThan(reimbursementAmount)) {
                return false; // authorization is more than reimbursement?  Then there's no overage....
            }

            final KualiDecimal oneHundred = new KualiDecimal(100); // multiply by 100 so we get some scale without having to revert to BigDecimals
            final KualiDecimal diff = reimbursementAmount.subtract(authorizationAmount).multiply(oneHundred);
            final KualiDecimal diffPercentage = diff.divide(authorizationAmount.multiply(oneHundred)).multiply(oneHundred); // mult authorizationAmount by 100 to get some scale; mult 100 by result to correctly compare to percentage

            return diffPercentage.isGreaterEqual(reimbursementOveragePercentage);
        } catch (NumberFormatException nfe) {
            return false; // we either couldn't parse reimbursement amount or authorization amount.  That's weird, but whatever...we shall not match
        }
    }

    /**
     * Verifies that the overage percent is greater than 0
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#validateAttributes(java.lang.String, java.util.Map)
     */
    @Override
    public List<RemotableAttributeError> validateAttributes(String kimTypeId, Map<String, String> attributes) {
        List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
        errors.addAll(super.validateAttributes(kimTypeId, attributes));

        final RemotableAttributeError overagePercentageError = validateReimbursementOveragePercentage(attributes.get(TemKimAttributes.REIMBURESEMENT_OVERAGE_PERCENTAGE));
        if (overagePercentageError != null) {
            errors.add(overagePercentageError);
        }

        return errors;
    }

    /**
     * Verifies that the reimbursement overage percentage represents a positive number less than 100
     * @param reimbursementOveragePercentage the reimbursement overage percentage to validate
     * @return the error from a bad reimbursement overage percentage, or null if the reimbursement overage percentage was valid
     */
    protected RemotableAttributeError validateReimbursementOveragePercentage(String reimbursementOveragePercentageAsString) {
        if (StringUtils.isBlank(reimbursementOveragePercentageAsString)) {
            return null; // no value to validate?  no error then...
        }

        RemotableAttributeError.Builder errorBuilder = RemotableAttributeError.Builder.create(TemKimAttributes.REIMBURESEMENT_OVERAGE_PERCENTAGE);

        // is the passed in String parsable to something numeric?
        KualiDecimal reimbursementOveragePercentage;
        try {
            reimbursementOveragePercentage = new KualiDecimal(reimbursementOveragePercentageAsString);
        } catch (NumberFormatException nfe) {
            return null; // just skip out - we should have already validated this
        }
        if (reimbursementOveragePercentage.isNegative()) {
            errorBuilder.addErrors(TemKeyConstants.ERROR_REIMBURSEMENT_OVERAGE_ORGANIZATION_HIERARCHY_ROLE_NEGATIVE);
        }
        if (!errorBuilder.getErrors().isEmpty()) {
            return errorBuilder.build();
        }
        return null;
    }

    /**
     * Overridden to error out a NumberFormatException before the data dictionary stuff would work
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#validateDataDictionaryAttribute(org.kuali.rice.kim.api.type.KimTypeAttribute, java.lang.String, java.lang.String)
     */
    @Override
    protected List<RemotableAttributeError> validateDataDictionaryAttribute(KimTypeAttribute attr, String key, String value) {
        if (TemKimAttributes.REIMBURESEMENT_OVERAGE_PERCENTAGE.equals(key) && !StringUtils.isBlank(value)) {
            RemotableAttributeError.Builder errorBuilder = RemotableAttributeError.Builder.create(TemKimAttributes.REIMBURESEMENT_OVERAGE_PERCENTAGE);
            KualiDecimal reimbursementOveragePercentage;
            try {
                new KualiDecimal(value);
            } catch (NumberFormatException nfe) {
                errorBuilder.addErrors(TemKeyConstants.ERROR_REIMBURSEMENT_OVERAGE_ORGANIZATION_HIERARCHY_ROLE_NON_NUMERIC);

                List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
                errors.add(errorBuilder.build());
                return errors; // we can't test numeric values, so let's skedaddle
            }
        }
        return super.validateDataDictionaryAttribute(attr, key, value);
    }
}
