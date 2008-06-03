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
package org.kuali.module.effort.lookup;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.service.EffortCertificationReportDefinitionService;
import org.kuali.module.integration.bo.LaborLedgerBalance;
import org.kuali.module.integration.service.LaborModuleService;
import org.kuali.rice.kns.util.KNSConstants;

public class EffortLedgerBalanceLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private BusinessObjectService businessObjectService;
    private LaborModuleService laborModuleService;
    private OptionsService optionsService;
    private EffortCertificationReportDefinitionService effortCertificationReportDefinitionService;

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {        
        Map<String, Object> searchFieldValues = this.getSearchFieldValues(fieldValues);
        List<String> defaultSortColumns = this.getDefaultSortColumns();
        
        List<? extends BusinessObject> searchResults = (List<? extends BusinessObject>)getLookupService().findCollectionBySearch(getBusinessObjectClass(), searchFieldValues);
        
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }

        return searchResults;
    }

    /**
     * build the real search field value map from the given field values
     * 
     * @param fieldValues the given field values
     * @return the real search field value map built from the given field values
     */
    private Map<String, Object> getSearchFieldValues(Map<String, String> fieldValues) {
        String reportYear = fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        String reportNumber = fieldValues.get(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER);

        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportYear);
        primaryKeys.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, reportNumber);

        EffortCertificationReportDefinition reportDefiniton = effortCertificationReportDefinitionService.findReportDefinitionByPrimaryKey(primaryKeys);

        Map<String, Object> searchFieldValues = new HashMap<String, Object>();
        searchFieldValues.putAll(fieldValues);
        searchFieldValues.remove(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER);

        String fiscalYears = KFSConstants.EMPTY_STRING;
        String expenseObjectTypeCodes = KFSConstants.EMPTY_STRING;
        for (Integer fiscalYear : reportDefiniton.getReportPeriods().keySet()) {
            fiscalYears += fiscalYear + KNSConstants.OR_LOGICAL_OPERATOR;
            expenseObjectTypeCodes += optionsService.getOptions(fiscalYear).getFinObjTypeExpenditureexpCd() + KNSConstants.OR_LOGICAL_OPERATOR;
        }
        searchFieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYears);
        searchFieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, expenseObjectTypeCodes);

        String balanceTypeCodes = KFSConstants.BALANCE_TYPE_ACTUAL + KNSConstants.OR_LOGICAL_OPERATOR + KFSConstants.BALANCE_TYPE_A21;
        searchFieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balanceTypeCodes);

        return searchFieldValues;
    }

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getBusinessObjectClass()
     */
    @Override
    public Class<? extends LaborLedgerBalance> getBusinessObjectClass() {
        return laborModuleService.getLaborLedgerBalanceForEffortCertificationClass();
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the laborModuleService attribute value.
     * 
     * @param laborModuleService The laborModuleService to set.
     */
    public void setLaborModuleService(LaborModuleService laborModuleService) {
        this.laborModuleService = laborModuleService;
    }

    /**
     * Sets the effortCertificationReportDefinitionService attribute value.
     * 
     * @param effortCertificationReportDefinitionService The effortCertificationReportDefinitionService to set.
     */
    public void setEffortCertificationReportDefinitionService(EffortCertificationReportDefinitionService effortCertificationReportDefinitionService) {
        this.effortCertificationReportDefinitionService = effortCertificationReportDefinitionService;
    }

    /**
     * Sets the optionsService attribute value.
     * 
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

}
