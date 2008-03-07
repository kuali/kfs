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
package org.kuali.kfs.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.dao.LookupDao;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.kfs.bo.ElectronicPaymentClaim;
import org.springframework.transaction.annotation.Transactional;

/**
 * A helper class that gives us the ability to do special lookups on electronic payment claims.
 */
@Transactional
public class ElectronicPaymentClaimLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicPaymentClaimLookupableHelperServiceImpl.class);
    private LookupDao lookupDao;
    
    /**
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        boolean unbounded = false;
        String claimingStatus = fieldValues.remove("claimingStatus");
        Criteria additionalCriteria = new Criteria();
        if (claimingStatus != null && !claimingStatus.equals("A")) {
            if (claimingStatus.equals(ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED)) {
                additionalCriteria.addEqualTo("referenceFinancialDocumentNumber", ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED);
            } else {
                additionalCriteria.addEqualTo("referenceFinancialDocumentNumber", ElectronicPaymentClaim.ClaimStatusCodes.UNCLAIMED);
            }
        }
        Collection dbResults = lookupDao.findCollectionBySearchHelper(ElectronicPaymentClaim.class, fieldValues, unbounded, false, additionalCriteria);
        List<ElectronicPaymentClaim> results = new ArrayList<ElectronicPaymentClaim>();
        for (Object claimAsObj: dbResults) {
            results.add((ElectronicPaymentClaim)claimAsObj);
        }
        return results;
    }

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        // grab the backLocation and the docFormKey
        this.setDocFormKey((String)fieldValues.get("docFormKey"));
        this.setBackLocation((String)fieldValues.get("backLocation"));
        super.validateSearchParameters(fieldValues);
    }

    /**
     * Sets the lookupDao attribute value.
     * @param lookupDao The lookupDao to set.
     */
    public void setLookupDao(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

}
