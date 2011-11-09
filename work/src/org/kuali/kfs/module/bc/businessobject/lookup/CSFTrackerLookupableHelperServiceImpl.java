/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject.lookup;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Base lookupable helper service for budget selection lookups.
 */
public class CSFTrackerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * Super impl clears out hidden values but we need to keep principalId hidden field in the criteria. 
     * Overridding here so that the call to clear hiddens is not executed.
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH));
        
        String universityFiscalYear = ((String[]) getParameters().get(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME))[0];
        String chartOfAccountsCode = ((String[]) getParameters().get(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME))[0];
        String accountNumber = ((String[]) getParameters().get(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME))[0];
        String subAccountNumber = ((String[]) getParameters().get(KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME))[0];
        String financialObjectCode = ((String[]) getParameters().get(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME))[0];
        String financialSubObjectCode = ((String[]) getParameters().get(KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME))[0];
        
        fieldValues.put(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        fieldValues.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        fieldValues.put(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        fieldValues.put(KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME, subAccountNumber);
        fieldValues.put(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, financialObjectCode);
        fieldValues.put(KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME, financialSubObjectCode);
        
        return super.getSearchResults(fieldValues);
    }

}
