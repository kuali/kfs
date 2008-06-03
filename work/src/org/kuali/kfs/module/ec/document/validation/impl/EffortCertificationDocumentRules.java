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
package org.kuali.module.effort.rules;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.AccountingLineRuleHelperService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortKeyConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rule.AddDetailLineRule;
import org.kuali.module.effort.rule.LoadDetailLineRule;
import org.kuali.module.effort.rule.UpdateDetailLineRule;
import org.kuali.module.effort.service.EffortCertificationDocumentService;
import org.kuali.module.effort.service.EffortCertificationExtractService;
import org.kuali.module.effort.service.EffortCertificationReportDefinitionService;
import org.kuali.module.integration.service.LaborModuleService;

/**
 * To define the rules that may be applied to the effort certification document, a transactional document
 */
public class EffortCertificationDocumentRules extends TransactionalDocumentRuleBase implements AddDetailLineRule<EffortCertificationDocument, EffortCertificationDetail>, UpdateDetailLineRule<EffortCertificationDocument, EffortCertificationDetail>, LoadDetailLineRule<EffortCertificationDocument> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDocumentRules.class);

    private EffortCertificationDocumentService effortCertificationDocumentService = SpringContext.getBean(EffortCertificationDocumentService.class);
    private EffortCertificationReportDefinitionService effortCertificationReportDefinitionService = SpringContext.getBean(EffortCertificationReportDefinitionService.class);
    private EffortCertificationExtractService effortCertificationExtractService = SpringContext.getBean(EffortCertificationExtractService.class);

    private BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    private LaborModuleService laborModuleService = SpringContext.getBean(LaborModuleService.class);
    private AccountingLineRuleHelperService accountingLineRuleHelperService = SpringContext.getBean(AccountingLineRuleHelperService.class);

    /**
     * @see org.kuali.module.effort.rule.AddDetailLineRule#processAddDetailLineRules(org.kuali.module.effort.document.EffortCertificationDocument,
     *      org.kuali.module.effort.bo.EffortCertificationDetail)
     */
    public boolean processAddDetailLineRules(EffortCertificationDocument document, EffortCertificationDetail detailLine) {
        LOG.info("processAddDetailLineRules() start");

        document.refreshNonUpdateableReferences();
        detailLine.refreshNonUpdateableReferences();

        if (!this.checkDetailLineAttributes(detailLine)) {
            return false;
        }

        if (!EffortCertificationDocumentRuleUtil.hasNonnegativePayrollAmount(detailLine)) {
            reportError(EffortPropertyConstants.EFFORT_CERTIFICATION_PAYROLL_AMOUNT, EffortKeyConstants.ERROR_NEGATIVE_PAYROLL_AMOUNT);
            return false;
        }

        if (!EffortCertificationDocumentRuleUtil.hasValidEffortPercent(detailLine)) {
            reportError(EffortPropertyConstants.EFFORT_CERTIFICATION_UPDATED_OVERALL_PERCENT, EffortKeyConstants.ERROR_INVALID_EFFORT_PERCENT);
            return false;
        }

        List<String> comparableFields = EffortConstants.DETAIL_LINES_CONSOLIDATION_FILEDS;
        if (detailLine.isNewLineIndicator() && EffortCertificationDocumentRuleUtil.hasSameExistingLine(document, detailLine, comparableFields)) {
            reportError(EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS, EffortKeyConstants.ERROR_LINE_EXISTS);
            return false;
        }

        if (EffortCertificationDocumentRuleUtil.hasClosedAccount(detailLine)) {
            reportError(KFSPropertyConstants.ACCOUNT, EffortKeyConstants.ERROR_ACCOUNT_CLOSED);
            return false;
        }

        if (detailLine.isNewLineIndicator() && !EffortCertificationDocumentRuleUtil.canExpiredAccountBeUsed(detailLine)) {
            Account account = detailLine.getAccount();
            Account continuation = account.getContinuationAccount();
            
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED, account.getAccountNumber(), continuation.getChartOfAccountsCode(), continuation.getAccountNumber());            
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.module.effort.rule.UpdateDetailLineRule#processUpdateDetailLineRules(org.kuali.module.effort.document.EffortCertificationDocument,
     *      org.kuali.module.effort.bo.EffortCertificationDetail)
     */
    public boolean processUpdateDetailLineRules(EffortCertificationDocument document, EffortCertificationDetail detailLine) {
        LOG.info("processAddLineBusinessRules() start");

        if (!this.processAddDetailLineRules(document, detailLine)) {
            return false;
        }

        if (!EffortCertificationDocumentRuleUtil.hasValidEffortPercent(detailLine)) {
            reportError(EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS, EffortKeyConstants.ERROR_INVALID_EFFORT_PERCENT);
            return false;
        }

        if (!EffortCertificationDocumentRuleUtil.hasNonnegativePayrollAmount(detailLine)) {
            reportError(EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS, EffortKeyConstants.ERROR_NEGATIVE_PAYROLL_AMOUNT);
            return false;
        }

        KualiDecimal originalTotalAmount = document.getTotalOriginalPayrollAmount();
        if (EffortCertificationDocumentRuleUtil.isPayrollAmountOverChanged(detailLine, originalTotalAmount, EffortConstants.PERCENT_LIMIT_OF_LINE_SALARY_CHANGE)) {
            reportError(EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS, EffortKeyConstants.ERROR_PAYROLL_AMOUNT_OVERCHANGED, (Double.valueOf(EffortConstants.PERCENT_LIMIT_OF_LINE_SALARY_CHANGE)).toString());
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    @Override
    public boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        LOG.info("processAddLineBusinessRules() start");

        EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) (approveEvent.getDocument());
        if (this.bypassBusinessRuleIfInitiation(effortCertificationDocument)) {
            return true;
        }

        boolean valid = true;
        for (EffortCertificationDetail detailLine : effortCertificationDocument.getEffortCertificationDetailLines()) {
            valid &= this.processUpdateDetailLineRules(effortCertificationDocument, detailLine);
        }

        valid &= this.processCustomRouteDocumentBusinessRules(effortCertificationDocument);

        if (valid) {
            effortCertificationDocumentService.addRouteLooping(effortCertificationDocument);
        }

        return valid;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        LOG.info("processAddLineBusinessRules() start");

        EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) document;

        // the docuemnt must have at least one detail line
        if (!EffortCertificationDocumentRuleUtil.hasDetailLine(effortCertificationDocument)) {
            reportError(EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS, EffortKeyConstants.ERROR_NOT_HAVE_DETAIL_LINE);
            return false;
        }

        if (this.bypassBusinessRuleIfInitiation(effortCertificationDocument)) {
            return true;
        }

        if (EffortCertificationDocumentRuleUtil.isPayrollAmountChangedFromOriginal(effortCertificationDocument)) {
            List<Note> notes = effortCertificationDocument.getDocumentHeader().getBoNotes();
            if (notes == null || notes.isEmpty()) {
                reportError(EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS, EffortKeyConstants.ERROR_NOTE_REQUIRED_WHEN_EFFORT_CHANGED);
                return false;
            }
        }

        if (EffortCertificationDocumentRuleUtil.isTotalPayrollAmountOverChanged(effortCertificationDocument, EffortConstants.AMOUNT_LIMIT_OF_TOTAL_SALARY_CHANGE)) {
            reportError(EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS, EffortKeyConstants.ERROR_TOTAL_PAYROLL_AMOUNT_OVERCHANGED, (Double.valueOf(EffortConstants.AMOUNT_LIMIT_OF_TOTAL_SALARY_CHANGE)).toString());
            return false;
        }

        if (!EffortCertificationDocumentRuleUtil.isTotalEffortPercentageAs100(effortCertificationDocument)) {
            reportError(EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS, EffortKeyConstants.ERROR_TOTAL_EFFORT_PERCENTAGE_NOT_100);
            return false;
        }

        String emplid = effortCertificationDocument.getEmplid();
        effortCertificationDocument.refreshReferenceObject(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_DEFINITION);
        EffortCertificationReportDefinition reportDefinition = effortCertificationDocument.getEffortCertificationReportDefinition();
        if (effortCertificationReportDefinitionService.hasApprovedEffortCertification(emplid, reportDefinition)) {
            List<Note> notes = effortCertificationDocument.getDocumentHeader().getBoNotes();
            if (notes == null || notes.isEmpty()) {
                reportError(EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS, EffortKeyConstants.ERROR_NOTE_REQUIRED_WHEN_APPROVED_EFFORT_CERTIFICATION_EXIST, emplid, reportDefinition.getUniversityFiscalYear().toString(), reportDefinition.getEffortCertificationReportNumber());
                return false;
            }
        }

        return true;
    }

    /**
     * @see org.kuali.module.effort.rule.LoadDetailLineRule#processLoadDetailLineRules(org.kuali.module.effort.document.EffortCertificationDocument)
     */
    public boolean processLoadDetailLineRules(EffortCertificationDocument effortCertificationDocument) {
        LOG.info("processLoadDetailLineRules() start");

        boolean isValid = true;
        String emplid = effortCertificationDocument.getEmplid();

        effortCertificationDocument.refreshReferenceObject(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_DEFINITION);
        EffortCertificationReportDefinition reportDefinition = effortCertificationDocument.getEffortCertificationReportDefinition();

        if (reportDefinition == null) {
            reportError(EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS, EffortKeyConstants.ERROR_REPORT_DEFINITION_NOT_EXIST);
            return false;
        }

        if (!reportDefinition.isActive()) {
            reportError(EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS, EffortKeyConstants.ERROR_REPORT_DEFINITION_INACTIVE);
            return false;
        }

        isValid = StringUtils.equals(KFSConstants.PeriodStatusCodes.OPEN, reportDefinition.getEffortCertificationReportPeriodStatusCode());
        if (!isValid) {
            reportError(EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS, EffortKeyConstants.ERROR_REPORT_DEFINITION_PERIOD_NOT_OPENED);
            return false;
        }

        isValid = !effortCertificationReportDefinitionService.hasPendingEffortCertification(emplid, reportDefinition);
        if (!isValid) {
            reportError(EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS, EffortKeyConstants.ERROR_PENDING_EFFORT_CERTIFICATION_EXIST);
            return false;
        }

        isValid = effortCertificationReportDefinitionService.hasBeenUsedForEffortCertificationGeneration(reportDefinition);
        if (!isValid) {
            reportError(EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS, EffortKeyConstants.ERROR_CREATE_PROCESS_HAS_NOT_BEEN_COMPLETED);
            return false;
        }

        isValid = effortCertificationExtractService.isEmployeeEligibleForEffortCertification(emplid, reportDefinition);
        if (!isValid) {
            reportError(EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS, EffortKeyConstants.ERROR_EMPLOYEE_NOT_ELIGIBLE, emplid);
            return false;
        }

        int countOfPendingSalaryExpenseTransfer = laborModuleService.countPendingSalaryExpenseTransfer(emplid);
        if (countOfPendingSalaryExpenseTransfer > 0) {
            reportError(EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS, EffortKeyConstants.ERROR_PENDING_SALARAY_EXPENSE_TRANSFER_EXIST, emplid, Integer.toString(countOfPendingSalaryExpenseTransfer));
            return false;
        }

        return this.populateEffortCertificationDocument(effortCertificationDocument);
    }

    /**
     * check if the attributes in the detail line are valid for the defintions in data dictionary and have valid references
     * 
     * @param detailLine the given effort certification detail line
     * @return true if the attributes in the detail line are valid for the defintions in data dictionary and have valid references;
     *         otherwise, false
     */
    private boolean checkDetailLineAttributes(EffortCertificationDetail detailLine) {
        LOG.debug("checkDetailLine() start");

        DataDictionary dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();

        // check if the fields in the detail line are in the correct formats defined in the data dictionary
        boolean hasValidFormat = EffortCertificationDocumentRuleUtil.hasValidFormat(detailLine);

        // if the formats of the fields are correct, check if there exist the references of a set of specified fields
        boolean hasValidReference = true;
        if (hasValidFormat) {
            hasValidReference &= accountingLineRuleHelperService.isValidAccount(detailLine.getAccount(), dataDictionary, EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS);
            hasValidReference &= accountingLineRuleHelperService.isValidChart(detailLine.getChartOfAccounts(), dataDictionary, EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS);

            if (!KFSConstants.getDashSubAccountNumber().equals(detailLine.getSubAccountNumber())) {
                hasValidReference &= accountingLineRuleHelperService.isValidSubAccount(detailLine.getSubAccount(), dataDictionary, EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS);
            }
        }

        return hasValidFormat && hasValidReference;
    }

    /**
     * determine if the business rule needs to be bypassed. If the given document is in the state of initiation, bypass
     * 
     * @param effortCertificationDocument the given document
     * @return true if the given document is in the state of initiation; otherwise, false
     */
    private boolean bypassBusinessRuleIfInitiation(EffortCertificationDocument effortCertificationDocument) {
        return effortCertificationDocument.getDocumentHeader().getWorkflowDocument().stateIsInitiated();
    }

    /**
     * determine if the given document can be populated. If so, populate it and return true
     * 
     * @param effortCertificationDocument the given document
     * @return true if the given document can be populated; otherwise, return false and the document is not changed
     */
    private boolean populateEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument) {
        String emplid = effortCertificationDocument.getEmplid();
        EffortCertificationReportDefinition reportDefinition = effortCertificationDocument.getEffortCertificationReportDefinition();
        EffortCertificationDocumentBuild documentBuild = effortCertificationExtractService.extract(emplid, reportDefinition);

        if (documentBuild == null) {
            reportError(EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS, EffortKeyConstants.ERROR_EMPLOYEE_NO_ELIGIBLE_LABOR_BALANCE, emplid);
            return false;
        }

        effortCertificationDocumentService.removeEffortCertificationDetailLines(effortCertificationDocument);
        boolean success = effortCertificationDocumentService.populateEffortCertificationDocument(effortCertificationDocument, documentBuild);

        if (effortCertificationReportDefinitionService.hasBeenUsedForEffortCertificationGeneration(emplid, reportDefinition)) {
            effortCertificationDocument.setEffortCertificationDocumentCode(true);
        }

        return success;
    }

    // record the error into the global error map
    private void reportError(String propertyName, String errorKey, String... errorParameters) {
        GlobalVariables.getErrorMap().putError(propertyName, errorKey, errorParameters);
    }
}