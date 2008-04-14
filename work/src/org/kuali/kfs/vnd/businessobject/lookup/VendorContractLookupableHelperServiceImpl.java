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
package org.kuali.module.vendor.lookup;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.dao.LookupDao;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.vendor.bo.VendorContract;
import org.springframework.transaction.annotation.Transactional;

public class VendorContractLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private LookupDao lookupDao;
    private DateTimeService dateTimeService;

    public void setLookupDao(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Overrides the getSearchResults in the super class so that we can do some customization in our vendor contract lookup.
     * 
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<PersistableBusinessObject> getSearchResults(Map<String, String> fieldValues) {

        boolean unbounded = false;
        super.setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        Date now = dateTimeService.getCurrentSqlDate();
        Criteria additionalCriteria = new Criteria();
        additionalCriteria.addLessOrEqualThan("vendorContractBeginningDate", now);
        additionalCriteria.addGreaterOrEqualThan("vendorContractEndDate", now);

        // We ought to call the findCollectionBySearchHelper that would accept the additionalCriteria
        boolean usePrimaryKeyValuesOnly = getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(getBusinessObjectClass(), fieldValues);
        List<PersistableBusinessObject> searchResults = (List) lookupDao.findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded, usePrimaryKeyValuesOnly, additionalCriteria);

        List<PersistableBusinessObject> finalSearchResults = new ArrayList();
        // loop through results to eliminate inactive or debarred vendors
        for (PersistableBusinessObject object : searchResults) {
            VendorContract vendorContract = (VendorContract) object;
            if (vendorContract.getVendorDetail().isActiveIndicator() && !vendorContract.getVendorDetail().isVendorDebarred()) {
                finalSearchResults.add(vendorContract);
            }
        }

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(finalSearchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }

        return finalSearchResults;
    }

}
