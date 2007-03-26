/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.lookupable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.service.LaborBalanceInquiryService;
import org.springframework.beans.factory.BeanFactory;

public class BaseFundsLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private BalanceService balanceService;
    private Map fieldValues;
    private LaborBalanceInquiryService laborBalanceInquiryService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.core.lookup.Lookupable#gfetSearchResults(java.util.Map)
     */

    @Override
    public List getSearchResults(Map fieldValues) {

        boolean unbounded = false;

        setBackLocation((String) fieldValues.get(Constants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(Constants.DOC_FORM_KEY));
        
        if ((fieldValues.get(PropertyConstants.FINANCIAL_OBJECT_CODE).toString().length() > 0)) {

            // Check for a valid labor object code for this inquiry
            if (StringUtils.indexOfAny(fieldValues.get(PropertyConstants.FINANCIAL_OBJECT_CODE).toString(), LaborConstants.BalanceInquiries.VALID_LABOR_OBJECT_CODES) != 0)
            GlobalVariables.getErrorMap().putError(LaborConstants.BalanceInquiries.ERROR_INVALID_LABOR_OBJECT_CODE, 
                    LaborConstants.BalanceInquiries.ERROR_INVALID_LABOR_OBJECT_CODE, "2");
        }
        
        // Parse the map and call the DAO to process the inquiry
        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
        laborBalanceInquiryService = (LaborBalanceInquiryService) beanFactory.getBean("laborBalanceInquiryService");
        Collection searchResultsCollection = laborBalanceInquiryService.getBaseFunds(fieldValues);

        // sort list if default sort column given
        List searchResults = (List) searchResultsCollection;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }
        
        // Get the result limit number from configuration
        beanFactory = SpringServiceLocator.getBeanFactory();
        kualiConfigurationService = (KualiConfigurationService) beanFactory.getBean("kualiConfigurationService");
        String limitConfig = kualiConfigurationService.getApplicationParameterValue(Constants.ParameterGroups.SYSTEM, Constants.LOOKUP_RESULTS_LIMIT_URL_KEY);
        Integer limit = null;
        if (limitConfig != null) {
            limit = Integer.valueOf(limitConfig);
        }
        Long collectionCount = new Long(searchResults.size());
        Long actualCountIfTruncated = new Long(0);

        // When the maxium limit of rows is reached, remove the extra rows.
        if (limit != null) {
            if (collectionCount >= limit.intValue()) {
                actualCountIfTruncated = collectionCount;
                for (int i = collectionCount.intValue() - 1; i >= limit; i--) {
                    searchResults.remove(i);
                }
            }
        }
        return new CollectionIncomplete(searchResults, actualCountIfTruncated);
    }
}