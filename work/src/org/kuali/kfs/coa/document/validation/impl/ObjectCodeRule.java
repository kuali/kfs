/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.coa.businessobject.BudgetAggregationCode;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionAccount;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectConsolidation;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ObjectConsService;
import org.kuali.kfs.coa.service.ObjectLevelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class implements the business rules for {@link ObjectCode}
 */
public class ObjectCodeRule extends MaintenanceDocumentRuleBase {

    protected static ObjectLevelService objectLevelService;
    protected static ObjectCodeService objectCodeService;
    protected static ObjectConsService objectConsService;

    protected static ConfigurationService configService;
    protected static ChartService chartService;
    protected Map reportsTo;

    /**
     * 
     * Constructs a ObjectCodeRule
     * Pseudo-injects some services as well as fills out the reports to chart hierarchy
     */
    public ObjectCodeRule() {

        if (objectConsService == null) {
            configService = SpringContext.getBean(ConfigurationService.class);
            objectLevelService = SpringContext.getBean(ObjectLevelService.class);
            objectCodeService = SpringContext.getBean(ObjectCodeService.class);
            chartService = SpringContext.getBean(ChartService.class);
            objectConsService = SpringContext.getBean(ObjectConsService.class);
        }
        reportsTo = chartService.getReportsToHierarchy();
    }


    /**
     * This method calls the following rules on document save:
     * <ul>
     * <li>{@link ObjectCodeRule#processObjectCodeRules(ObjectCode)}</li>
     * </ul>
     * It does not fail if rules fail
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        // default to success
        boolean success = true;

        Object maintainableObject = document.getNewMaintainableObject().getBusinessObject();

        success &= processObjectCodeRules((ObjectCode) maintainableObject);
        
        if (isObjectCodeInactivating(document)) {
            success &= checkForBlockingOffsetDefinitions((ObjectCode)maintainableObject);
            success &= checkForBlockingIndirectCostRecoveryExclusionAccounts((ObjectCode)maintainableObject);
        }

        return success;

    }

    /**
     * This method calls the following rules on document route:
     * <ul>
     * <li>{@link ObjectCodeRule#processObjectCodeRules(ObjectCode)}</li>
     * </ul>
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("processCustomRouteDocumentBusinessRules called");

        boolean success = true;

        Object maintainableObject = document.getNewMaintainableObject().getBusinessObject();
        success &= processObjectCodeRules((ObjectCode) maintainableObject);
        
        if (isObjectCodeInactivating(document)) {
            success &= checkForBlockingOffsetDefinitions((ObjectCode)maintainableObject);
            success &= checkForBlockingIndirectCostRecoveryExclusionAccounts((ObjectCode)maintainableObject);
        }

        return success;
    }

    /**
     * 
     * This checks the following rules:
     * <ul>
     * <li>object code valid</li>
     * <li>reports to chart code is valid (similar to what {@link ObjectCodePreRules} does)</li>
     * <li>is the budget aggregation code valid</li>
     * <li>then checks to make sure that this object code hasn't already been entered in the consolidation and level table</li>
     * <li>finally checks to make sure that the next year object code (if filled out) isn't already in there and that this object code has a valid fiscal year</li> 
     * </ul>
     * @param objectCode
     * @return
     */
    protected boolean processObjectCodeRules(ObjectCode objectCode) {

        boolean result = true;

        String objCode = objectCode.getFinancialObjectCode();

        if (!/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ObjectCode.class, KFSConstants.ChartApcParms.OBJECT_CODE_ILLEGAL_VALUES, objCode).evaluationSucceeds()) {
            this.putFieldError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_ILLEGAL, objCode);
            result = false;            
        }

        Integer year = objectCode.getUniversityFiscalYear();
        String chartCode = objectCode.getChartOfAccountsCode();
        String calculatedReportsToChartCode;
        String reportsToObjectCode = objectCode.getReportsToFinancialObjectCode();
        String nextYearObjectCode = objectCode.getNextYearFinancialObjectCode();

        // only validate if chartCode is NOT null ( chartCode should be provided to get determine reportsToChartCode )
        if (chartCode != null) {

            // We must calculate a reportsToChartCode here to duplicate the logic
            // that takes place in the preRule.
            // The reason is that when we do a SAVE, the pre-rules are not
            // run and we will get bogus error messages.
            // So, we are simulating here what the pre-rule will do.
            calculatedReportsToChartCode = (String) reportsTo.get(chartCode);

            if (!verifyReportsToChartCode(year, chartCode, objectCode.getFinancialObjectCode(), calculatedReportsToChartCode, reportsToObjectCode)) {
                this.putFieldError("reportsToFinancialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_REPORTS_TO_OBJCODE_ILLEGAL, new String[] { reportsToObjectCode, calculatedReportsToChartCode });
                result = false;
            }
        }

        String budgetAggregationCode = objectCode.getFinancialBudgetAggregationCd();

        if (!isLegalBudgetAggregationCode(budgetAggregationCode)) {
            this.putFieldError("financialBudgetAggregationCd", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_ONEOF_VALID, "Budget Aggregation Code");
            result = false;
        }

        //KFSMI-798 - refresh() changed to refreshNonUpdateableReferences()
        //All references for ObjectCode are non-updatable
        objectCode.refreshNonUpdateableReferences();

        // Chart code (fin_coa_cd) must be valid - handled by dd

        if (!this.consolidationTableDoesNotHave(chartCode, objCode)) {
            this.putFieldError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_CONSOLIDATION_ERROR, chartCode + "-" + objCode);
            result = false;
        }

        if (!this.objectLevelTableDoesNotHave(chartCode, objCode)) {
            this.putFieldError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_LEVEL_ERROR, chartCode + "-" + objCode);
            result = false;
        }
        if (!StringUtils.isEmpty(nextYearObjectCode) && nextYearObjectCodeDoesNotExistThisYear(year, chartCode, nextYearObjectCode)) {
            this.putFieldError("nextYearFinancialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_BEVALID, "Next Year Object Code");
            result = false;
        }
        if (!this.isValidYear(year)) {
            this.putFieldError("universityFiscalYear", KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_BEVALID, "Fiscal Year");
        }
        
        /*
         * The framework handles this: Pending object must not have duplicates waiting for approval Description (fdoc_desc) must be
         * entered Verify the DD handles these: Fiscal year (univ_fisal_yr) must be entered Chart code (fin_coa_code) must be
         * entered Object code (fin_object_code) must be entered (fin_obj_cd_nm) must be entered (fin_obj_cd_shrt_nm) must be
         * entered Object level (obj_level_code) must be entered The Reports to Object (rpts_to_fin_obj_cd) must be entered It seems
         * like these are Business Rules for other objects: An Object code must be active when it is used as valid value in the
         * Labor Object Code table An Object code must be active when it is used as valid value in the LD Benefits Calculation table
         * An Object code must be active when it is used as valid value in the ICR Automated Entry table An Object code must be
         * active when it is used as valid value in the Chart table These still need attention: Warning if chart code is inactive
         * Warning if object level is inactive If the Next Year Object has been entered, it must exist in the object code table
         * alongside the fiscal year and chart code (rpts_to_fin_coa_cd) is looked up based on chart code [fp_hcoat]
         */


        return result;

    }

    /**
     * This method checks whether newly added object code already exists in Object Level table
     * 
     * @param chartCode
     * @param objectCode
     * @return false if this object code already exists in the object level table
     */
    public boolean objectLevelTableDoesNotHave(String chartCode, String objectCode) {
        try {
            ObjectLevel objLevel = objectLevelService.getByPrimaryId(chartCode, objectCode);
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
     * 
     * This Check whether newly added object code already exists in Consolidation table
     * @param chartCode
     * @param objectCode
     * @return false if this object code already exists in the object consolidation table
     */
    public boolean consolidationTableDoesNotHave(String chartCode, String objectCode) {
        try {
            ObjectConsolidation objectCons = objectConsService.getByPrimaryId(chartCode, objectCode);
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

    /**
     * 
     * This checks to see if the next year object code already exists in the next fiscal year
     * @param year
     * @param chartCode
     * @param objCode
     * @return false if this object code exists in the next fiscal year
     */
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
     * This checks to make sure the fiscal year they are trying to assign is valid
     * @param year
     * @return true if this is a valid year
     */
    /*
     *  KFSMI 5058 revised to return true value 
     * 
     */
    @Deprecated
    public boolean isValidYear(Integer year) {
        return true;
    }


    /**
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

    /**
     * 
     * This method is a null-safe wrapper around Set.contains()
     * @param set
     * @param value
     * @return true if this value is not contained in the Set or Set is null
     */
    protected boolean denied(List set, Object value) {
        if (set != null) {
            return !set.contains(value);
        }
        return true;
    }

    /**
     * Budget Aggregation Code (fobj_bdgt_aggr_cd) must have an institutionally specified value
     * 
     * @param budgetAggregationCode
     * @return true if this is a legal budget aggregation code
     */
    protected boolean isLegalBudgetAggregationCode(String budgetAggregationCode) {

        // find all the matching records
        Map whereMap = new HashMap();
        whereMap.put("code", budgetAggregationCode);

        Collection budgetAggregationCodes = getBoService().findMatching(BudgetAggregationCode.class, whereMap);

        // if there is at least one result, then entered budget aggregation code is legal
        return budgetAggregationCodes.size() > 0;
    }

    /**
     * 
     * This checks to see if the object code already exists in the system
     * @param year
     * @param chart
     * @param objectCode
     * @return true if it exists
     */
    protected boolean verifyObjectCode(Integer year, String chart, String objectCode) {
        return null != objectCodeService.getByPrimaryId(year, chart, objectCode);
    }

    /**
     * 
     * This method checks When the value of reportsToChartCode does not have an institutional exception, the Reports to Object
     * (rpts_to_fin_obj_cd) fiscal year, and chart code must exist in the object code table
     * if the chart and object are the same, then skip the check
     * this assumes that the validity of the reports-to object code has already been tested (and corrected if necessary)
     * @param year
     * @param chart
     * @param objectCode
     * @param reportsToChartCode
     * @param reportsToObjectCode
     * @return true if the object code's reports to chart and chart are the same and reports to object and object code are the same
     * or if the object code already exists
     */
    protected boolean verifyReportsToChartCode(Integer year, String chart, String objectCode, String reportsToChartCode, String reportsToObjectCode) {
        // TODO: verify this ambiguously stated rule against the UNIFACE source
        // When the value of reportsToChartCode does not have an institutional exception, the Reports to Object
        // (rpts_to_fin_obj_cd) fiscal year, and chart code must exist in the object code table

        // if the chart and object are the same, then skip the check
        // this assumes that the validity of the reports-to object code has already been tested (and corrected if necessary)
        if (StringUtils.equals(reportsToChartCode, chart) && StringUtils.equals(reportsToObjectCode, objectCode)) {
            return true;
        }

        // otherwise, check if the object is valid
        return verifyObjectCode(year, reportsToChartCode, reportsToObjectCode);
    }
    
    /**
     * Determines if the given maintenance document constitutes an inactivation of the object code it is maintaining
     * @param maintenanceDocument the maintenance document maintaining an object code
     * @return true if the document is inactivating the object code, false otherwise
     */
    protected boolean isObjectCodeInactivating(MaintenanceDocument maintenanceDocument) {
        if (maintenanceDocument.isEdit() && maintenanceDocument.getOldMaintainableObject() != null && maintenanceDocument.getOldMaintainableObject().getBusinessObject() != null) {
            final ObjectCode oldObjectCode = (ObjectCode)maintenanceDocument.getOldMaintainableObject().getBusinessObject();
            final ObjectCode newObjectCode = (ObjectCode)maintenanceDocument.getNewMaintainableObject().getBusinessObject();
            
            return oldObjectCode.isActive() && !newObjectCode.isActive();
        }
        return false;
    }
    
    /**
     * Checks that no offset definitions are dependent on the given object code if it is inactivated
     * @param objectCode the object code trying to inactivate
     * @return true if no offset definitions rely on the object code, false otherwise; this method also inserts error statements
     */
    protected boolean checkForBlockingOffsetDefinitions(ObjectCode objectCode) {
        final BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        boolean result = true;
        
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put("universityFiscalYear", objectCode.getUniversityFiscalYear());
        keys.put("chartOfAccountsCode", objectCode.getChartOfAccountsCode());
        keys.put("financialObjectCode", objectCode.getFinancialObjectCode());
        
        final int matchingCount = businessObjectService.countMatching(OffsetDefinition.class, keys);
        if (matchingCount > 0) {
            GlobalVariables.getMessageMap().putErrorForSectionId("Edit Object Code",KFSKeyConstants.ERROR_DOCUMENT_OBJECTMAINT_INACTIVATION_BLOCKING,new String[] {(objectCode.getUniversityFiscalYear() != null ? objectCode.getUniversityFiscalYear().toString() : ""), objectCode.getChartOfAccountsCode(), objectCode.getFinancialObjectCode(), Integer.toString(matchingCount), OffsetDefinition.class.getName()});
            result = false;
        }
        return result;
    }
    
    /**
     * Checks that no ICR Exclusion by Account records are dependent on the given object code if it is inactivated
     * @param objectCode the object code trying to inactivate
     * @return if no ICR Exclusion by Account records rely on the object code, false otherwise; this method also inserts error statements
     */
    protected boolean checkForBlockingIndirectCostRecoveryExclusionAccounts(ObjectCode objectCode) {
        boolean result = true;
        
        final UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        if (objectCode.getUniversityFiscalYear() != null && objectCode.getUniversityFiscalYear().equals(universityDateService.getCurrentFiscalYear())) {
            final BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
            
            Map<String, Object> keys = new HashMap<String, Object>();
            keys.put("chartOfAccountsCode", objectCode.getChartOfAccountsCode());
            keys.put("financialObjectCode", objectCode.getFinancialObjectCode());
            
            final int matchingCount = businessObjectService.countMatching(IndirectCostRecoveryExclusionAccount.class, keys);
            if (matchingCount > 0) {
                GlobalVariables.getMessageMap().putErrorForSectionId("Edit Object Code",KFSKeyConstants.ERROR_DOCUMENT_OBJECTMAINT_INACTIVATION_BLOCKING,new String[] {(objectCode.getUniversityFiscalYear() != null ? objectCode.getUniversityFiscalYear().toString() : ""), objectCode.getChartOfAccountsCode(), objectCode.getFinancialObjectCode(), Integer.toString(matchingCount), IndirectCostRecoveryExclusionAccount.class.getName()});
                result = false;
            }
        }
        return result;
    }
    
}
