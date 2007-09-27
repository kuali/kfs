/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectCons;
import org.kuali.module.chart.bo.codes.BudgetAggregationCode;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.ObjectConsService;
import org.kuali.module.chart.service.ObjectLevelService;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * 
 * This class implements the business rules from: http://fms.dfa.cornell.edu:8080/confluence/display/KULCOA/Object+Code
 * 
 * 
 */
public class ObjectCodeRule extends MaintenanceDocumentRuleBase {

    private static ObjectLevelService objectLevelService;
    private static ObjectCodeService objectCodeService;
    private static ObjectConsService  objectConsService;
    
    private static KualiConfigurationService configService;
    private static ChartService chartService;
    private Map reportsTo;
    private static Set illegalValues;

    public ObjectCodeRule() {

    	if ( objectConsService == null ) {
	        configService = SpringContext.getBean(KualiConfigurationService.class);
	
	        illegalValues = retrieveParameterSet(KFSConstants.ChartApcParms.OBJECT_CODE_ILLEGAL_VALUES);
	
	        objectLevelService = SpringContext.getBean(ObjectLevelService.class);
	        objectCodeService = SpringContext.getBean(ObjectCodeService.class);
	        chartService = SpringContext.getBean(ChartService.class);
	        objectConsService = SpringContext.getBean(ObjectConsService.class);
    	}
        reportsTo = chartService.getReportsToHierarchy();
    }


    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        // default to success
        boolean success = true;
        //success &= checkEmptyValues(document);

        Object maintainableObject = document.getNewMaintainableObject().getBusinessObject();

        success &= processObjectCodeRules((ObjectCode) maintainableObject);

        return success;

    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("processCustomRouteDocumentBusinessRules called");

        boolean success = true;

        Object maintainableObject = document.getNewMaintainableObject().getBusinessObject();
        success &= processObjectCodeRules((ObjectCode) maintainableObject);

        //success &= checkEmptyValues(document);

        return success;
    }
    /*
    private boolean checkEmptyValues(MaintenanceDocument maintenanceDocument) {
        boolean success = true;

        // success &= checkEmptyBOField("chartOfAccountsCode", newAccount.getChartOfAccountsCode(), "Chart of Accounts Code");
        // success &= checkEmptyBOField("accountNumber", newAccount.getAccountNumber(), "Account Number");

        return success;
    }
	*/
    private boolean processObjectCodeRules(ObjectCode objectCode) {

        boolean result = true;

        String objCode = objectCode.getFinancialObjectCode();

        if (!isLegalObjectCode(objCode)) {
            this.putFieldError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_ILLEGAL, objCode);
            result = false;
        }

        Integer year = objectCode.getUniversityFiscalYear();
        String chartCode = objectCode.getChartOfAccountsCode();
        String calculatedReportsToChartCode;
        String reportsToObjectCode = objectCode.getReportsToFinancialObjectCode();
        String nextYearObjectCode = objectCode.getNextYearFinancialObjectCode();
        
        //only validate if chartCode is NOT null ( chartCode should be provided to get determine reportsToChartCode )
        if( chartCode != null ){
            
            // We must calculate a reportsToChartCode here to duplicate the logic
            // that takes place in the preRule.
            // The reason is that when we do a SAVE, the pre-rules are not
            // run and we will get bogus error messages.
            // So, we are simulating here what the pre-rule will do.
            calculatedReportsToChartCode = (String) reportsTo.get(chartCode);
                        
            if (!verifyReportsToChartCode(year, chartCode, objectCode.getFinancialObjectCode(), calculatedReportsToChartCode, reportsToObjectCode)) {
                this.putFieldError("reportsToFinancialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_REPORTS_TO_OBJCODE_ILLEGAL, new String[]{reportsToObjectCode, calculatedReportsToChartCode} );
                result = false;
            }
        }

        String budgetAggregationCode = objectCode.getFinancialBudgetAggregationCd();

        if (!isLegalBudgetAggregationCode(budgetAggregationCode)) {
            this.putFieldError("financialBudgetAggregationCd", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_ONEOF_VALID, "Budget Aggregation Code");
            result = false;
        }

        objectCode.refresh();

        // Chart code (fin_coa_cd) must be valid - handled by dd

        if (!this.consolidationTableDoesNotHave(chartCode, objCode)) {
            this.putFieldError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_CONSOLIDATION_ERROR, chartCode+"-"+objCode);
            result = false;
        }

        if (!this.objectLevelTableDoesNotHave(chartCode, objCode)) {
            this.putFieldError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_LEVEL_ERROR, chartCode+"-"+objCode );
            result = false;
        }
        if (!StringUtils.isEmpty(nextYearObjectCode) && nextYearObjectCodeDoesNotExistThisYear(year, chartCode,  nextYearObjectCode)){
            this.putFieldError("nextYearFinancialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_BEVALID, "Next Year Object Code");
            result = false;
        }
        if (!this.isValidYear(year)){
            this.putFieldError("universityFiscalYear", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_BEVALID, "Fiscal Year");
        }

        /*
         * 
         * 
         * 
         * The framework handles this:
         * 
         * Pending object must not have duplicates waiting for approval Description (fdoc_desc) must be entered
         * 
         * Verify the DD handles these:
         * 
         * Fiscal year (univ_fisal_yr) must be entered Chart code (fin_coa_code) must be entered Object code (fin_object_code) must
         * be entered (fin_obj_cd_nm) must be entered (fin_obj_cd_shrt_nm) must be entered Object level (obj_level_code) must be
         * entered The Reports to Object (rpts_to_fin_obj_cd) must be entered
         * 
         * It seems like these are Business Rules for other objects:
         * 
         * An Object code must be active when it is used as valid value in the Labor Object Code table An Object code must be active
         * when it is used as valid value in the LD Benefits Calculation table An Object code must be active when it is used as
         * valid value in the ICR Automated Entry table An Object code must be active when it is used as valid value in the Chart
         * table
         * 
         * 
         * These still need attention:
         * 
         * Warning if chart code is inactive Warning if object level is inactive If the Next Year Object has been entered, it must
         * exist in the object code table alongside the fiscal year and chart code (rpts_to_fin_coa_cd) is looked up based on chart
         * code [fp_hcoat]
         * 
         * 
         * 
         */


        return result;

    }

    /**
     * 
     * This method checks whether newly added object code already exists in Object Level table
     * 
     * @param chartCode
     * @param objectCode
     * @return
     */
    public boolean objectLevelTableDoesNotHave(String chartCode, String objectCode) {
        try {
            ObjLevel objLevel = objectLevelService.getByPrimaryId(chartCode, objectCode);
            if (objLevel != null) {
                objLevel.getFinancialObjectLevelCode(); // this might throw an Exception when proxying is in effect
                return false;
            }
        }
        catch (PersistenceBrokerException e) {
            // intentionally ignore the Exception
        }

        return true;
    }

    /**
     * Check whether newly added object code already exists in Consolidation table
     */
    public boolean consolidationTableDoesNotHave(String chartCode, String objectCode) {
        try {
            ObjectCons objectCons = objectConsService.getByPrimaryId(chartCode, objectCode);
            if (objectCons != null) {
                objectCons.getFinConsolidationObjectCode(); // this might throw an Exception when proxying is in effect
                return false;
            }
        }
        catch (PersistenceBrokerException e) {
            // intentionally ignore the Exception
        }
        return true;
    }
    
    public boolean nextYearObjectCodeDoesNotExistThisYear(Integer year, String chartCode, String objCode) {
        try {
            ObjectCode objectCode = objectCodeService.getByPrimaryId(year, chartCode, objCode);
            if (objectCode != null) {
                return false;
            }
        }
        catch (PersistenceBrokerException e) {
            // intentionally ignore the Exception
        }
        return true;
    }

    /**
     * 
     */
    public boolean isValidYear(Integer year) {
        if (year==null) return false;
        int enteredYear = year.intValue();
        int currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().intValue();
        if ((enteredYear-currentYear) == 0 || (enteredYear-currentYear) == 1)
            return true;
        return false;
    }


    /**
     * 
     * This method is a null-safe wrapper around Set.contains().
     * 
     * @param set - methods returns false if the Set is null
     * @param value to seek
     * @return true iff Set exists and contains given value
     */
    protected boolean permitted(Set set, Object value) {
        if (set != null) {
            return set.contains(value);
        }
        return false;
    }

    protected boolean denied(Set set, Object value) {
        if (set != null) {
            return !set.contains(value);
        }
        return true;
    }


    /**
     * Object code (fin_obj_code) must not have an institutionally specified illegal value
     * 
     * @return
     */
    protected boolean isLegalObjectCode(String objectCode) {
        boolean result = denied(illegalValues, objectCode);
        return result;
    }

    /**
     * 
     * Budget Aggregation Code (fobj_bdgt_aggr_cd) must have an institutionally specified value
     * 
     * @param budgetAggregationCode
     * @return
     */
    protected boolean isLegalBudgetAggregationCode(String budgetAggregationCode) {
        
        // find all the matching records
        Map whereMap = new HashMap();
        whereMap.put("code", budgetAggregationCode);
        
        Collection budgetAggregationCodes = getBoService().findMatching(BudgetAggregationCode.class, whereMap);

        // if there is at least one result, then entered budget aggregation code is legal
        return budgetAggregationCodes.size() > 0;
    }

    protected boolean verifyObjectCode(Integer year, String chart, String objectCode) {
        return null != objectCodeService.getByPrimaryId(year, chart, objectCode);
    }

    protected boolean verifyReportsToChartCode(Integer year, String chart, String objectCode, String reportsToChartCode, String reportsToObjectCode) {
        // TODO: verify this ambiguously stated rule against the UNIFACE source
        // When the value of reportsToChartCode does not have an institutional exception, the Reports to Object
        // (rpts_to_fin_obj_cd) fiscal year, and chart code must exist in the object code table
    	
    	// if the chart and object are the same, then skip the check
    	// this assumes that the validity of the reports-to object code has already been tested (and corrected if necessary)
    	if ( StringUtils.equals( reportsToChartCode, chart ) && StringUtils.equals( reportsToObjectCode, objectCode ) ) {
    		return true;
    	}

    	// otherwise, check if the object is valid
    	return verifyObjectCode(year, reportsToChartCode, reportsToObjectCode);
    }

    private Set<String> retrieveParameterSet(String parameterName) {
        return configService.getParameterValuesAsSet(KFSConstants.CHART_NAMESPACE, KFSConstants.Components.OBJECT, parameterName);
    }

}
