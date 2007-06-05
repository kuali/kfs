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

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.comparator.CellComparatorHelper;
import org.kuali.core.web.format.BooleanFormatter;
import org.kuali.core.web.format.Formatter;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.Column;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.gl.bo.TransientBalanceInquiryAttributes;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.web.inquirable.LedgerBalanceInquirableImpl;
import org.springframework.transaction.annotation.Transactional;
import org.kuali.rice.KNSServiceLocator;

@Transactional
public class LedgerBalanceForBenefitExpenseTransferLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerBalanceForBenefitExpenseTransferLookupableHelperServiceImpl.class);
    
    private OptionsService optionsService;
    
    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new LedgerBalanceInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        LOG.info("Start getSearchResults()");
        
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        String fiscalYearString = (String)fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);        
        Options options = this.getOptions(fiscalYearString);
        
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, options.getFinObjTypeExpenditureexpCd());
        fieldValues.put(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.FINANCIAL_OBJECT_FRINGE_OR_SALARY_CODE, LaborConstants.BenefitExpenseTransfer.LABOR_LEDGER_BENEFIT_CODE);
        
        // get the ledger balances with actual balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, options.getActualFinancialBalanceTypeCd());
        List cashBalances = (List) getLookupService().findCollectionBySearchHelper(LedgerBalance.class, fieldValues, false);
        
        // get the ledger balances with effort balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, LaborConstants.EFFORT_REPORTING_BALANCE_TYPE_CODE);
        List effortBalances = (List) getLookupService().findCollectionBySearchHelper(LedgerBalance.class, fieldValues, false);
        
        CollectionIncomplete cashBalancesResults = new CollectionIncomplete(cashBalances, (long)cashBalances.size());
        CollectionIncomplete effortBalancesResults = new CollectionIncomplete(effortBalances, (long)effortBalances.size());
        
        List searchResults = (List)cashBalancesResults;
        searchResults.addAll(effortBalancesResults);
        
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return searchResults;
    }
    
    /**
     * get the Options object for the given fiscal year
     * @param fiscalYearString the given fiscal year
     * @return the Options object for the given fiscal year
     */
    private Options getOptions(String fiscalYearString){
        Options options;
        if(fiscalYearString == null){
            options = optionsService.getCurrentYearOptions();
        }
        else{
            Integer fiscalYear = Integer.valueOf(fiscalYearString.trim());
            options = optionsService.getOptions(fiscalYear);
        }
        return options;
    }

    /**
     * Sets the optionsService attribute value.
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
}
