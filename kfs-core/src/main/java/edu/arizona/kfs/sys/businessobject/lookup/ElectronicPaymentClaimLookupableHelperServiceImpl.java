/*
 * Copyright 2010 The Kuali Foundation.
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
package edu.arizona.kfs.sys.businessobject.lookup;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.dao.LookupDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ElectronicPaymentClaimLookupableHelperServiceImpl extends org.kuali.kfs.sys.businessobject.lookup.ElectronicPaymentClaimLookupableHelperServiceImpl {

    private static final String CLAIMING_STATUS_BOTH_ANY = "A";
    private static final String GENERATING_DOCUMENT  = "generatingDocument";
    private static final String CLAIMING_STATUS = "paymentClaimStatusCode";
    private static final String DEPOSIT_DATE_FROM = "rangeLowerBoundKeyPrefix_generatingAdvanceDepositDetail.financialDocumentAdvanceDepositDate";
    
    private LookupDao lookupDao;
    
	/**
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<PersistableBusinessObject> getSearchResults(Map<String, String> fieldValues) {
        
        //  payment claim status code
        String claimingStatus = fieldValues.remove(CLAIMING_STATUS);
        if (ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED.equalsIgnoreCase(claimingStatus)) {
            fieldValues.put(CLAIMING_STATUS, ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED);
        } else if (ElectronicPaymentClaim.ClaimStatusCodes.UNCLAIMED.equalsIgnoreCase(claimingStatus)) {
            fieldValues.put(CLAIMING_STATUS, ElectronicPaymentClaim.ClaimStatusCodes.UNCLAIMED);
        } else if (CLAIMING_STATUS_BOTH_ANY.equalsIgnoreCase(claimingStatus)) { 
            fieldValues.put(CLAIMING_STATUS, "*");
        }
        
        getAdvDepCriteriaForElectronicPaymentClain(fieldValues); 
        List<PersistableBusinessObject> resultsList = (List)lookupDao.findCollectionBySearchHelper(ElectronicPaymentClaim.class, fieldValues, false, false);

        return resultsList;
    }


    private void getAdvDepCriteriaForElectronicPaymentClain(Map<String, String> fieldValues) {
        String dateRange = fieldValues.remove(KFSPropertyConstants.GENERATING_ADVANCE_DEPOSIT_DETAIL + KFSConstants.DELIMITER  + KFSPropertyConstants.FINANCIAL_DOCUMENT_ADVANCE_DEPOSIT_DATE);
        if (StringUtils.isNotBlank(dateRange)) {
      	    fieldValues.put(GENERATING_DOCUMENT + KFSConstants.DELIMITER + KFSPropertyConstants.ADVANCE_DEPOSITS + KFSConstants.DELIMITER +KFSPropertyConstants.FINANCIAL_DOCUMENT_ADVANCE_DEPOSIT_DATE, dateRange);
        }
        getAcctLineCriteriaField(fieldValues, KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);
        getAcctLineCriteriaField(fieldValues, KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_DESCRIPTION);
        String amountFrom = fieldValues.remove(KFSPropertyConstants.PAYMENT_CLAIM_AMOUNT_FROM);
        String amountTo = fieldValues.remove(KFSPropertyConstants.PAYMENT_CLAIM_AMOUNT_TO);
        String amount = getAmountCriteria(amountFrom, amountTo);
        if (StringUtils.isNotBlank(amount)) {
      	    fieldValues.put(GENERATING_DOCUMENT + KFSConstants.DELIMITER +KFSPropertyConstants.SOURCE_ACCOUNTING_LINES + KFSConstants.DELIMITER + KFSPropertyConstants.AMOUNT, amount);
        }
        fieldValues.remove(DEPOSIT_DATE_FROM);
    }
    
    private void getAcctLineCriteriaField(Map<String, String> fieldValues, String fieldName) {
	    String fieldValue = fieldValues.remove(KFSPropertyConstants.GENERATING_ACCOUNTING_LINE + KFSConstants.DELIMITER  + fieldName);
        if (StringUtils.isNotBlank(fieldValue)) {
      	    fieldValues.put(GENERATING_DOCUMENT + KFSConstants.DELIMITER + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES + KFSConstants.DELIMITER + fieldName , fieldValue);
        }

    }
    
    private String getAmountCriteria(String fromAmount, String toAmount) {
        if (StringUtils.isNotBlank(fromAmount) && StringUtils.isNotBlank(toAmount)) {
        	if (StringUtils.endsWith(fromAmount, ".")) {
        		fromAmount = fromAmount + "0";
        	}
            return fromAmount + SearchOperator.BETWEEN.op() + toAmount;
        }
        if (StringUtils.isNotBlank(fromAmount) && StringUtils.isBlank(toAmount)) {
            return SearchOperator.GREATER_THAN_EQUAL.op() + fromAmount;
        }
        if (StringUtils.isBlank(fromAmount) && StringUtils.isNotBlank(toAmount)) {
            return SearchOperator.LESS_THAN_EQUAL.op() + toAmount;
        }
        return null;
    }

    public void setLookupDao(LookupDao lookupDao) {
		this.lookupDao = lookupDao;
	}

   
}
