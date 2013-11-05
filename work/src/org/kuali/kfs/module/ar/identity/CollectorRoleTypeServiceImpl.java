/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeError.Builder;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

public class CollectorRoleTypeServiceImpl extends RoleTypeServiceBase {

    /**
     * note: for validating Sub-account review role - if acct or org are specified, sub-account and chart are all required
     *
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#validateAttributes(java.lang.String, java.util.Map)
     */
    @Override
    public List<RemotableAttributeError> validateAttributes(String kimTypeId, Map<String,String> attributes) {
        List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
        errors.addAll(super.validateAttributes(kimTypeId, attributes));

        Builder errorBuilder = null;

        String billingChartCode = attributes.get(ArKimAttributes.BILLING_CHART_OF_ACCOUNTS_CODE);
        String billingOrganizationCode = attributes.get(ArKimAttributes.BILLING_ORGANIZATION_CODE);
        String processingChartCode = attributes.get(ArKimAttributes.PROCESSING_CHART_OF_ACCOUNTS_CODE);
        String processingOrganizationCode = attributes.get(ArKimAttributes.PROCESSING_ORGANIZATION_CODE);

        // either billing chart & org OR processing chart & org are required
        if (StringUtils.isNotEmpty(billingChartCode)) {
            if (StringUtils.isEmpty(billingOrganizationCode)) {
                // both billing chart and org are required
                errorBuilder = RemotableAttributeError.Builder.create(ArKimAttributes.BILLING_ORGANIZATION_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
                errors.add(errorBuilder.build());
            }

            if (StringUtils.isNotEmpty(processingChartCode)) {
                // either billing chart/org OR processing chart/org are required, but not both
                errorBuilder = RemotableAttributeError.Builder.create(ArKimAttributes.PROCESSING_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
                errors.add(errorBuilder.build());
            }
            if (StringUtils.isNotEmpty(processingOrganizationCode)) {
                // either billing chart/org OR processing chart/org are required, but not both
                errorBuilder = RemotableAttributeError.Builder.create(ArKimAttributes.PROCESSING_ORGANIZATION_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
                errors.add(errorBuilder.build());
            }
        } else {
            if (StringUtils.isNotEmpty(billingOrganizationCode)) {
                // both billing chart and org are required
                errorBuilder = RemotableAttributeError.Builder.create(ArKimAttributes.BILLING_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
                errors.add(errorBuilder.build());

                if (StringUtils.isNotEmpty(processingChartCode)) {
                    // either billing chart/org OR processing chart/org are required, but not both
                    errorBuilder = RemotableAttributeError.Builder.create(ArKimAttributes.PROCESSING_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
                    errors.add(errorBuilder.build());
                }
                if (StringUtils.isNotEmpty(processingOrganizationCode)) {
                    // either billing chart/org OR processing chart/org are required, but not both
                    errorBuilder = RemotableAttributeError.Builder.create(ArKimAttributes.PROCESSING_ORGANIZATION_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
                    errors.add(errorBuilder.build());
                }
            } else {
                if (StringUtils.isEmpty(processingChartCode)) {
                    if (StringUtils.isNotEmpty(processingOrganizationCode)) {
                        // both processing chart and processing org are required
                        errorBuilder = RemotableAttributeError.Builder.create(ArKimAttributes.PROCESSING_ORGANIZATION_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
                        errors.add(errorBuilder.build());
                    }
                } else {
                    if (StringUtils.isEmpty(processingOrganizationCode)) {
                        // either billing chart/org OR processing chart/org are required, but not both
                        errorBuilder = RemotableAttributeError.Builder.create(ArKimAttributes.BILLING_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
                        errors.add(errorBuilder.build());
                    }
                }
            }
        }

        String startingLetter = attributes.get(ArKimAttributes.CUSTOMER_LAST_NAME_STARTING_LETTER);
        String endingLetter = attributes.get(ArKimAttributes.CUSTOMER_LAST_NAME_ENDING_LETTER);

        // need to clear up validation rules around starting and ending letters - can they be empty? are they always required?
        if (StringUtils.isNotEmpty(startingLetter) && StringUtils.isNotEmpty(endingLetter)) {
            char customerLastNameStartingLetter = attributes.get(ArKimAttributes.CUSTOMER_LAST_NAME_STARTING_LETTER).charAt(0);
            char customerLastNameEndingLetter = attributes.get(ArKimAttributes.CUSTOMER_LAST_NAME_ENDING_LETTER).charAt(0);

            if (customerLastNameStartingLetter > customerLastNameEndingLetter) {
                errorBuilder = RemotableAttributeError.Builder.create(ArKimAttributes.CUSTOMER_LAST_NAME_STARTING_LETTER, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
                errors.add(errorBuilder.build());
            }
        }

        return errors;
    }

}
