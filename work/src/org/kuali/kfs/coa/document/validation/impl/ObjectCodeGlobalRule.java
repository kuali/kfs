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
package org.kuali.module.chart.rules;

import java.util.List;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectCodeGlobal;
import org.kuali.module.chart.bo.ObjectCodeGlobalDetail;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.ObjectLevelService;

/**
 * This class represents the business rules for the maintenance of {@link ObjectCodeGlobal} business objects
 */
public class ObjectCodeGlobalRule extends MaintenanceDocumentRuleBase {
    private ObjectCodeGlobal objectCodeGlobal;
    private ObjectCodeService objectCodeService;
    private ObjectLevelService objectLevelService;

    public ObjectCodeGlobalRule() {
        super();
        setObjectCodeService(SpringContext.getBean(ObjectCodeService.class));
        setObjectLevelService(SpringContext.getBean(ObjectLevelService.class));
    }


    /**
     * This method sets the convenience objects like objectCodeGlobal, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    @Override
    public void setupConvenienceObjects() {

        // setup ObjectCodeGlobal convenience objects,
        // make sure all possible sub-objects are populated
        objectCodeGlobal = (ObjectCodeGlobal) super.getNewBo();

        // forces refreshes on all the sub-objects in the lists
        for (ObjectCodeGlobalDetail objectCodeGlobalDetail : objectCodeGlobal.getObjectCodeGlobalDetails()) {
            objectCodeGlobalDetail.refreshNonUpdateableReferences();
        }
    }

    /**
     * This performs rules checks on document approve
     * <ul>
     * <li>{@link ObjectCodeGlobalRule#checkSimpleRulesAllLines()}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects();
        // check simple rules
        success &= checkSimpleRulesAllLines();
        return success;
    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link ObjectCodeGlobalRule#checkSimpleRulesAllLines()}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects();
        // check simple rules
        success &= checkSimpleRulesAllLines();
        return success;
    }

    /**
     * This performs rules checks on document save
     * <ul>
     * <li>{@link ObjectCodeGlobalRule#checkSimpleRulesAllLines()}</li>
     * </ul>
     * This rule does not fail on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        // check simple rules
        checkSimpleRulesAllLines();

        return true;
    }

    /**
     * This method checks to make sure that each new {@link ObjectCodeGlobalDetail} has: 
     * <ul>
     * <li>valid chart of accounts code</li>
     * <li>valid fiscal year</li>
     * <li>unique identifiers (not currently implemented)</li>
     * </ul>
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.core.document.MaintenanceDocument, java.lang.String, org.kuali.core.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean success = true;
        if (bo instanceof ObjectCodeGlobalDetail) {
            ObjectCodeGlobalDetail detail = (ObjectCodeGlobalDetail) bo;
            if (!checkEmptyValue(detail.getChartOfAccountsCode())) {
                // put an error about chart code
                GlobalVariables.getErrorMap().putError("chartOfAccountsCode", KFSKeyConstants.ERROR_REQUIRED, "Chart of Accounts Code");
                success &= false;
            }
            if (!checkEmptyValue(detail.getUniversityFiscalYear())) {
                // put an error about fiscal year
                GlobalVariables.getErrorMap().putError("universityFiscalYear", KFSKeyConstants.ERROR_REQUIRED, "University Fiscal Year");
                success &= false;
            }
            if (!checkUniqueIdentifiers(detail)) {
                // TODO: put an error about unique identifier fields must not exist more than once.
                success &= false;
            }
            // both keys are present and satisfy the unique identifiers requirement, go ahead and proces the rest of the rules
            if (success) {
                success &= checkObjectCodeDetails(detail);
            }

        }
        return success;
    }

    /**
     * 
     * This method (will)put an error about unique identifier fields must not exist more than once.
     * @param dtl
     * @return true (not currently implemented fully)
     */
    private boolean checkUniqueIdentifiers(ObjectCodeGlobalDetail dtl) {
        boolean success = true;
        return success;

    }

    /**
     * 
     * This checks the following conditions:
     * <ul>
     * <li>{@link ObjectCodeGlobalRule#checkObjectLevelCode(ObjectCodeGlobal, ObjectCodeGlobalDetail, int, boolean)} </li>
     * <li>{@link ObjectCodeGlobalRule#checkNextYearObjectCode(ObjectCodeGlobal, ObjectCodeGlobalDetail, int, boolean)} </li>
     * <li>{@link ObjectCodeGlobalRule#checkReportsToObjectCode(ObjectCodeGlobal, ObjectCodeGlobalDetail, int, boolean)}</li>
     * </ul>
     * @param dtl
     * @return true if sub-rules succeed
     */
    public boolean checkObjectCodeDetails(ObjectCodeGlobalDetail dtl) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getErrorMap().getErrorCount();
        getDictionaryValidationService().validateBusinessObject(dtl);
        dtl.refreshNonUpdateableReferences();
        // here is where we need our checks for level code nd next year object code
        success &= checkObjectLevelCode(objectCodeGlobal, dtl, 0, true);
        success &= checkNextYearObjectCode(objectCodeGlobal, dtl, 0, true);
        success &= checkReportsToObjectCode(objectCodeGlobal, dtl, 0, true);
        success &= GlobalVariables.getErrorMap().getErrorCount() == originalErrorCount;

        return success;
    }

    /**
     * This method checks that the reports to object code input on the top level of the global document is valid for a given chart's
     * reportToChart in the detail section
     * 
     * @param dtl
     * @return true if the reports to object is valid for the given reports to chart
     */
    private boolean checkReportsToObjectCode(ObjectCodeGlobal objectCodeGlobal, ObjectCodeGlobalDetail dtl, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;
        if (checkEmptyValue(objectCodeGlobal.getReportsToFinancialObjectCode())) {
            // objectCodeGlobal.refreshReferenceObject("reportsToFinancialObject");
            String reportsToObjectCode = objectCodeGlobal.getReportsToFinancialObjectCode();
            String reportsToChartCode = dtl.getChartOfAccounts().getReportsToChartOfAccountsCode();
            Integer fiscalYear = dtl.getUniversityFiscalYear();

            // verify that this combination exists in the db
            ObjectCode objCode = objectCodeService.getByPrimaryId(fiscalYear, reportsToChartCode, reportsToObjectCode);
            if (ObjectUtils.isNull(objCode)) {
                success &= false;
                String[] errorParameters = { reportsToObjectCode, reportsToChartCode, fiscalYear.toString() };
                if (add) {
                    errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.OBJECT_CODE_GLOBAL_DETAILS + "." + "chartOfAccountsCode";
                    putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_INVALID_RPTS_TO_OBJ_CODE, errorParameters);
                }
                else {
                    errorPath = KFSPropertyConstants.OBJECT_CODE_GLOBAL_DETAILS + "[" + lineNum + "]." + "chartOfAccountsCode";
                    putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_INVALID_RPTS_TO_OBJ_CODE, errorParameters);
                }
            }
            return success;

        }
        else {
            GlobalVariables.getErrorMap().putError("reportsToFinancialObjectCode", KFSKeyConstants.ERROR_REQUIRED, "Reports to Object Code");
            success &= false;
        }

        return success;
    }


    /**
     * This method checks that the next year object code specified in the change document is a valid object code for a given chart
     * and year
     * 
     * @param dtl
     * @return false if this object code doesn't exist in the next fiscal year
     */
    private boolean checkNextYearObjectCode(ObjectCodeGlobal objectCodeGlobal, ObjectCodeGlobalDetail dtl, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;
        // first check to see if the Next Year object code was filled in
        if (checkEmptyValue(objectCodeGlobal.getNextYearFinancialObjectCode())) {
            // then this value must also exist as a regular financial object code currently
            ObjectCode objCode = objectCodeService.getByPrimaryId(dtl.getUniversityFiscalYear(), dtl.getChartOfAccountsCode(), objectCodeGlobal.getNextYearFinancialObjectCode());
            if (ObjectUtils.isNull(objCode)) {
                success &= false;
                String[] errorParameters = { objectCodeGlobal.getNextYearFinancialObjectCode(), dtl.getChartOfAccountsCode(), dtl.getUniversityFiscalYear().toString() };
                if (add) {
                    errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.OBJECT_CODE_GLOBAL_DETAILS + "." + "chartOfAccountsCode";
                    putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_INVALID_NEXT_YEAR_OBJ_CODE, errorParameters);
                }
                else {
                    errorPath = KFSPropertyConstants.OBJECT_CODE_GLOBAL_DETAILS + "[" + lineNum + "]." + "chartOfAccountsCode";
                    putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_INVALID_NEXT_YEAR_OBJ_CODE, errorParameters);
                }
            }
            return success;
        }

        return success;
    }

    /**
     * This method checks that the object level code from the object code change document actually exists for the chart object
     * specified in the detail
     * 
     * @param dtl
     * @return false if object level doesn't exist for the chart, and level code filled in
     */
    private boolean checkObjectLevelCode(ObjectCodeGlobal objectCodeGlobal, ObjectCodeGlobalDetail dtl, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;
        // first check to see if the level code is filled in
        if (checkEmptyValue(objectCodeGlobal.getFinancialObjectLevelCode())) {
            ObjLevel objLevel = objectLevelService.getByPrimaryId(dtl.getChartOfAccountsCode(), objectCodeGlobal.getFinancialObjectLevelCode());
            if (ObjectUtils.isNull(objLevel)) {
                success &= false;
                String[] errorParameters = { objectCodeGlobal.getFinancialObjectLevelCode(), dtl.getChartOfAccountsCode() };
                if (add) {
                    errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.OBJECT_CODE_GLOBAL_DETAILS + "." + "chartOfAccountsCode";
                    putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_INVALID_OBJ_LEVEL, errorParameters);
                }
                else {
                    errorPath = KFSPropertyConstants.OBJECT_CODE_GLOBAL_DETAILS + "[" + lineNum + "]." + "chartOfAccountsCode";
                    putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_INVALID_OBJ_LEVEL, errorParameters);
                }
            }
            return success;

        }
        else {
            GlobalVariables.getErrorMap().putError("financialObjectLevelCode", KFSKeyConstants.ERROR_REQUIRED, "Object Level Code");
            success &= false;
        }
        return success;
    }

    /**
     * This method checks the simple rules for all lines at once and gets called on save, submit, etc. but not on add
     * 
     * <ul>
     * <li>{@link ObjectCodeGlobalRule#checkFiscalYearAllLines(ObjectCodeGlobal)} </li>
     * <li>{@link ObjectCodeGlobalRule#checkChartAllLines(ObjectCodeGlobal)} </li>
     * <li>{@link ObjectCodeGlobalRule#checkObjectLevelCodeAllLines(ObjectCodeGlobal)} </li>
     * <li>{@link ObjectCodeGlobalRule#checkNextYearObjectCodeAllLines(ObjectCodeGlobal)} </li>
     * <li>{@link ObjectCodeGlobalRule#checkReportsToObjectCodeAllLines(ObjectCodeGlobal)} </li>
     * </ul>
     * @return
     */
    protected boolean checkSimpleRulesAllLines() {
        boolean success = true;
        // check if there are any object codes and accounts, if either fails this should fail
        if (!checkForObjectCodeGlobalDetails(objectCodeGlobal.getObjectCodeGlobalDetails())) {
            success = false;
        }
        else {
            // check object codes
            success &= checkFiscalYearAllLines(objectCodeGlobal);

            // check chart code
            success &= checkChartAllLines(objectCodeGlobal);

            // check object level code
            success &= checkObjectLevelCodeAllLines(objectCodeGlobal);

            // check next year object code
            success &= checkNextYearObjectCodeAllLines(objectCodeGlobal);

            // check reports to object code
            success &= checkReportsToObjectCodeAllLines(objectCodeGlobal);

        }
        return success;
    }

    /**
     * 
     * This checks to make sure that there is at least one {@link ObjectCodeGlobalDetail} in the collection
     * @param objectCodeGlobalDetails
     * @return false if the collection is empty or null
     */
    protected boolean checkForObjectCodeGlobalDetails(List<ObjectCodeGlobalDetail> objectCodeGlobalDetails) {
        if (objectCodeGlobalDetails == null || objectCodeGlobalDetails.size() == 0) {
            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.OBJECT_CODE_GLOBAL_DETAILS + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_NO_CHART_FISCAL_YEAR);
            return false;
        }
        return true;
    }

    /**
     * 
     * This method calls {@link ObjectCodeGlobalRule#checkFiscalYear(ObjectCodeGlobal, ObjectCodeGlobalDetail, int, boolean)} on each detail object
     * @param objectCodeGlobal
     * @return true if all lines pass
     */
    protected boolean checkFiscalYearAllLines(ObjectCodeGlobal objectCodeGlobal) {
        boolean success = true;
        int i = 0;
        for (ObjectCodeGlobalDetail objectCodeGlobalDetail : objectCodeGlobal.getObjectCodeGlobalDetails()) {

            // check fiscal year first
            success &= checkFiscalYear(objectCodeGlobal, objectCodeGlobalDetail, i, false);

            // increment counter for sub object changes list
            i++;
        }

        return success;
    }

    /**
     * 
     * This method calls {@link ObjectCodeGlobalRule#checkChartOnObjCodeDetails(ObjectCodeGlobal, ObjectCodeGlobalDetail, int, boolean)} on each detail object
     * 
     * @param ocChangeDocument
     * @return true if all lines pass
     */
    protected boolean checkChartAllLines(ObjectCodeGlobal ocChangeDocument) {
        boolean success = true;
        int i = 0;
        for (ObjectCodeGlobalDetail objectCodeGlobalDetail : ocChangeDocument.getObjectCodeGlobalDetails()) {

            // check chart
            success &= checkChartOnObjCodeDetails(ocChangeDocument, objectCodeGlobalDetail, i, false);
            // increment counter for sub object changes list
            i++;
        }

        return success;
    }


    /**
     * 
     * This method calls {@link ObjectCodeGlobalRule#checkReportsToObjectCode(ObjectCodeGlobal, ObjectCodeGlobalDetail, int, boolean)} on each detail object
     * 
     * @param objectCodeGlobalDocument2
     * @return true if all lines pass
     */
    private boolean checkReportsToObjectCodeAllLines(ObjectCodeGlobal objectCodeGlobalDocument2) {
        boolean success = true;
        int i = 0;
        for (ObjectCodeGlobalDetail objectCodeGlobalDetail : objectCodeGlobal.getObjectCodeGlobalDetails()) {

            // check fiscal year first
            success &= checkReportsToObjectCode(objectCodeGlobal, objectCodeGlobalDetail, i, false);

            // increment counter for sub object changes list
            i++;
        }

        return success;
    }

    /**
     * 
     * This method calls {@link ObjectCodeGlobalRule#checkNextYearObjectCode(ObjectCodeGlobal, ObjectCodeGlobalDetail, int, boolean)} on each detail object
     * 
     * @param objectCodeGlobalDocument2
     * @return true if all lines pass
     */
    private boolean checkNextYearObjectCodeAllLines(ObjectCodeGlobal objectCodeGlobalDocument2) {
        boolean success = true;
        int i = 0;
        for (ObjectCodeGlobalDetail objectCodeGlobalDetail : objectCodeGlobal.getObjectCodeGlobalDetails()) {

            // check fiscal year first
            success &= checkNextYearObjectCode(objectCodeGlobal, objectCodeGlobalDetail, i, false);

            // increment counter for sub object changes list
            i++;
        }

        return success;
    }

    /**
     * 
     * This method calls {@link ObjectCodeGlobalRule#checkObjectLevelCode(ObjectCodeGlobal, ObjectCodeGlobalDetail, int, boolean)} on each detail object
     * 
     * @param objectCodeGlobalDocument2
     * @return true if all lines pass
     */
    private boolean checkObjectLevelCodeAllLines(ObjectCodeGlobal objectCodeGlobalDocument2) {
        boolean success = true;
        int i = 0;
        for (ObjectCodeGlobalDetail objectCodeGlobalDetail : objectCodeGlobal.getObjectCodeGlobalDetails()) {

            // check fiscal year first
            success &= checkObjectLevelCode(objectCodeGlobal, objectCodeGlobalDetail, i, false);

            // increment counter for sub object changes list
            i++;
        }

        return success;
    }

    /**
     * 
     * This checks to make sure that the fiscal year has been entered
     * @param objectCodeGlobal
     * @param objectCodeGlobalDetail
     * @param lineNum
     * @param add
     * @return false if no fiscal year value
     */
    protected boolean checkFiscalYear(ObjectCodeGlobal objectCodeGlobal, ObjectCodeGlobalDetail objectCodeGlobalDetail, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;
        // first must have an actual fiscal year
        if (objectCodeGlobalDetail.getUniversityFiscalYear() == null) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.OBJECT_CODE_GLOBAL_DETAILS + "." + "universityFiscalYear";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_FISCAL_YEAR_MUST_EXIST);
            }
            else {
                errorPath = KFSPropertyConstants.OBJECT_CODE_GLOBAL_DETAILS + "[" + lineNum + "]." + "universityFiscalYear";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_FISCAL_YEAR_MUST_EXIST);
            }
            success &= false;
            return success;
        }

        return success;
    }

    /**
     * 
     * This checks to make sure that the chart of accounts for the detail object has been filled in
     * @param objectCodeGlobal
     * @param objectCodeGlobalDetail
     * @param lineNum
     * @param add
     * @return false if chart of accounts code null
     */
    protected boolean checkChartOnObjCodeDetails(ObjectCodeGlobal objectCodeGlobal, ObjectCodeGlobalDetail objectCodeGlobalDetail, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;
        // first must have an actual fiscal year
        if (objectCodeGlobalDetail.getChartOfAccounts() == null) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.OBJECT_CODE_GLOBAL_DETAILS + "." + "chartOfAccountsCode";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_CHART_MUST_EXIST);
            }
            else {
                errorPath = KFSPropertyConstants.OBJECT_CODE_GLOBAL_DETAILS + "[" + lineNum + "]." + "chartOfAccountsCode";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_CHART_MUST_EXIST);
            }
            success &= false;
            return success;
        }

        return success;
    }


    private void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;

    }


    private void setObjectLevelService(ObjectLevelService objectLevelService) {
        this.objectLevelService = objectLevelService;

    }
}
