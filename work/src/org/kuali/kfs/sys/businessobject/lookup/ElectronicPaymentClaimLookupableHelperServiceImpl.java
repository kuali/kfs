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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.dao.LookupDao;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * A helper class that gives us the ability to do special lookups on electronic payment claims.
 */
@Transactional
public class ElectronicPaymentClaimLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicPaymentClaimLookupableHelperServiceImpl.class);
    private LookupDao lookupDao;
    private BusinessObjectService businessObjectService;
    
    /**
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<PersistableBusinessObject> getSearchResults(Map<String, String> fieldValues) {
        boolean unbounded = false;
        String claimingStatus = fieldValues.remove("paymentClaimStatusCode");
        if (claimingStatus != null && !claimingStatus.equals("A")) {
            if (claimingStatus.equals(ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED)) {
                fieldValues.put("paymentClaimStatusCode", ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED);
            } else {
                fieldValues.put("paymentClaimStatusCode", ElectronicPaymentClaim.ClaimStatusCodes.UNCLAIMED);
            }
        }
        String organizationReferenceId = fieldValues.remove("generatingAccountingLine.organizationReferenceId");
        List<PersistableBusinessObject> resultsList = (List)lookupDao.findCollectionBySearchHelper(ElectronicPaymentClaim.class, fieldValues, unbounded, false, null);
        if (!StringUtils.isBlank(organizationReferenceId)) {
            Set<String> matchingAdvanceDepositDocumentNumbers = getAdvanceDepositsWithOrganizationReferenceId(organizationReferenceId);
            
            List<PersistableBusinessObject> prunedResults = new ArrayList<PersistableBusinessObject>();
            for (PersistableBusinessObject epcAsPBO : resultsList) {
                final ElectronicPaymentClaim epc = (ElectronicPaymentClaim)epcAsPBO;
                if (matchingAdvanceDepositDocumentNumbers.contains(epc.getDocumentNumber())) {
                    prunedResults.add(epc);
                }
            }
            resultsList = new CollectionIncomplete<PersistableBusinessObject>(prunedResults, ((CollectionIncomplete)resultsList).getActualSizeIfTruncated());
            
        } 
        return resultsList;
    }
    
    /**
     * Finds the document ids for all AD documents which have an accounting line with the given organizationReferenceId
     * @param organizationReferenceId the organization reference id to find advance deposit documents for
     * @return a Set of advance deposit documents
     */
    protected Set<String> getAdvanceDepositsWithOrganizationReferenceId(String organizationReferenceId) {
        Set<String> adIds = new HashSet<String>();
        
        Map fields = new HashMap();
        fields.put("sourceAccountingLines.organizationReferenceId", organizationReferenceId);
        Collection ads = getBusinessObjectService().findMatching(AdvanceDepositDocument.class, fields);
        for (Object adAsObject : ads) {
            final AdvanceDepositDocument adDoc = (AdvanceDepositDocument)adAsObject;
            adIds.add(adDoc.getDocumentNumber());
        }
        
        return adIds;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        // grab the backLocation and the docFormKey
        this.setDocFormKey((String)fieldValues.get("docFormKey"));
        this.setBackLocation((String)fieldValues.get("backLocation"));
        super.validateSearchParameters(fieldValues);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#isResultReturnable(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public boolean isResultReturnable(BusinessObject claimAsBO) {
        boolean result = super.isResultReturnable(claimAsBO);
        ElectronicPaymentClaim claim = (ElectronicPaymentClaim)claimAsBO;
        if (result && ((claim.getPaymentClaimStatusCode() != null && claim.getPaymentClaimStatusCode().equals(ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED)) || (!StringUtils.isBlank(claim.getReferenceFinancialDocumentNumber())))) {
            result = false;
        }
        return result;
    }

    /**
     * Sets the lookupDao attribute value.
     * @param lookupDao The lookupDao to set.
     */
    public void setLookupDao(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
