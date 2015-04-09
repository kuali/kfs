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
