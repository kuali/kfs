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
package org.kuali.kfs.module.ec.businessobject.lookup;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.integration.ld.LaborLedgerBalanceForEffortCertification;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.BeanPropertyComparator;

public class EffortLedgerBalanceLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private KualiModuleService kualiModuleService;
    private BusinessObjectService businessObjectService;
    private OptionsService optionsService;
    private EffortCertificationReportDefinitionService effortCertificationReportDefinitionService;

    /**
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        Map<String, String> searchFieldValues = this.getSearchFieldValues(fieldValues);
        List<String> defaultSortColumns = this.getDefaultSortColumns();
        List<? extends BusinessObject> searchResults = (List<? extends BusinessObject>) getLookupService().findCollectionBySearch(getBusinessObjectClass(), searchFieldValues);

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
    private Map<String, String> getSearchFieldValues(Map<String, String> fieldValues) {
        String reportYear = fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        String reportNumber = fieldValues.get(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER);

        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportYear);
        primaryKeys.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, reportNumber);

        EffortCertificationReportDefinition reportDefiniton = effortCertificationReportDefinitionService.findReportDefinitionByPrimaryKey(primaryKeys);

        Map<String, String> searchFieldValues = new HashMap<String, String>();
        searchFieldValues.putAll(fieldValues);
        searchFieldValues.remove(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER);

        String fiscalYears = KFSConstants.EMPTY_STRING;
        String expenseObjectTypeCodes = KFSConstants.EMPTY_STRING;
        for (Integer fiscalYear : reportDefiniton.getReportPeriods().keySet()) {
            fiscalYears += fiscalYear + SearchOperator.OR.op();
            expenseObjectTypeCodes += optionsService.getOptions(fiscalYear).getFinObjTypeExpenditureexpCd() + SearchOperator.OR.op();
        }
        searchFieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYears);
        searchFieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, expenseObjectTypeCodes);

        String balanceTypeCodes = KFSConstants.BALANCE_TYPE_ACTUAL + SearchOperator.OR.op() + KFSConstants.BALANCE_TYPE_A21;
        searchFieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balanceTypeCodes);

        return searchFieldValues;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getBusinessObjectClass()
     */
    @Override
    public Class<? extends LaborLedgerBalance> getBusinessObjectClass() {
        return kualiModuleService.getResponsibleModuleService(LaborLedgerBalanceForEffortCertification.class).createNewObjectFromExternalizableClass(LaborLedgerBalanceForEffortCertification.class).getClass();
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    @Override
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
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

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

}
