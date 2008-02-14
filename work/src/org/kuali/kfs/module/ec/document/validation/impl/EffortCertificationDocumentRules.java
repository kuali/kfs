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

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortKeyConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rule.AddDetailLineRule;
import org.kuali.module.effort.rule.GenerateSalaryExpenseTransferDocumentRule;
import org.kuali.module.effort.rule.UpdateDetailLineRule;
import org.kuali.module.effort.service.EffortCertificationDocumentService;
import org.kuali.module.effort.util.EffortCertificationParameterFinder;

/**
 * To define the rules that may be applied to the effort certification document, a transactional document
 */
public class EffortCertificationDocumentRules extends TransactionalDocumentRuleBase implements GenerateSalaryExpenseTransferDocumentRule<EffortCertificationDocument>, AddDetailLineRule<EffortCertificationDocument, EffortCertificationDetail>, UpdateDetailLineRule<EffortCertificationDocument, EffortCertificationDetail> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDocumentRules.class);

    private EffortCertificationDocumentService effortCertificationDocumentService = SpringContext.getBean(EffortCertificationDocumentService.class);

    /**
     * Constructs a EffortCertificationDocumentRules.java.
     */
    public EffortCertificationDocumentRules() {
        super();
    }

    /**
     * This rule will generate salary expense transfer document. After the document is generated, it is hard to rollback. Thus, the
     * rule should be the very last one to be invoked.
     * 
     * @see org.kuali.module.effort.rule.GenerateSalaryExpenseTransferDocumentRule#processGenerateSalaryExpenseTransferDocumentRules(org.kuali.module.effort.document.EffortCertificationDocument)
     */
    public boolean processGenerateSalaryExpenseTransferDocumentRules(EffortCertificationDocument effortCertificationDocument) {
        LOG.info("processGenerateSalaryExpenseTransferDocument() start");

        if (this.bypassBusinessRuleIfInitiation(effortCertificationDocument)) {
            return true;
        }

        boolean valid = effortCertificationDocumentService.generateSalaryExpenseTransferDocument(effortCertificationDocument);
        if (!valid) {
            GlobalVariables.getErrorMap().putError(EffortPropertyConstants.EFFORT_CERTIFICATION_DETAIL_LINES, EffortKeyConstants.ERROR_SALARY_EXPENSE_TRANSFER_DOCUMENT_NOT_GENERATED);
            return false;
        }

        return valid;
    }

    /**
     * @see org.kuali.module.effort.rule.AddDetailLineRule#processAddDetailLineRules(org.kuali.module.effort.document.EffortCertificationDocument,
     *      org.kuali.module.effort.bo.EffortCertificationDetail)
     */
    public boolean processAddDetailLineRules(EffortCertificationDocument document, EffortCertificationDetail detailLine) {
        LOG.info("processAddDetailLineRules() start");

        // TODO: push up to presentation layer
        EffortCertificationDocumentRuleUtil.applyDefaultvalues(detailLine);

        if (!this.checkDetailLineAttributes(detailLine)) {
            return false;
        }

        if (!EffortCertificationDocumentRuleUtil.hasNonnegativePayrollAmount(detailLine)) {
            GlobalVariables.getErrorMap().putError(EffortPropertyConstants.EFFORT_CERTIFICATION_DETAIL_LINE, EffortKeyConstants.ERROR_NEGATIVE_PAYROLL_AMOUNT);
            return false;
        }

        if (!EffortCertificationDocumentRuleUtil.hasValidEffortPercent(detailLine)) {
            GlobalVariables.getErrorMap().putError(EffortPropertyConstants.EFFORT_CERTIFICATION_UPDATED_OVERALL_PERCENT, EffortKeyConstants.ERROR_INVALID_EFFORT_PERCENT);
            return false;
        }

        if (EffortCertificationDocumentRuleUtil.hasSameExistingLine(document, detailLine, this.getComparableFields())) {
            GlobalVariables.getErrorMap().putError(EffortPropertyConstants.EFFORT_CERTIFICATION_DETAIL_LINE, EffortKeyConstants.ERROR_LINE_EXISTS);
            return false;
        }

        if (EffortCertificationDocumentRuleUtil.hasClosedAccount(detailLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, EffortKeyConstants.ERROR_ACCOUNT_CLOSED);
            return false;
        }

        if (!EffortCertificationDocumentRuleUtil.canExpiredAccountBeUsed(detailLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        if (EffortCertificationDocumentRuleUtil.hasA21SubAccount(detailLine)) {
            List<String> designatedCostShareSubAccountTypeCodes = EffortCertificationParameterFinder.getCostShareSubAccountTypeCode();
            if (!EffortCertificationDocumentRuleUtil.hasCostShareSubAccount(detailLine, designatedCostShareSubAccountTypeCodes)) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.SUB_ACCOUNT, EffortKeyConstants.ERROR_NOT_COST_SHARE_SUB_ACCOUNT);
                return false;
            }
            
            // TODO: push up to presentation layer
            EffortCertificationDocumentRuleUtil.updateSourceAccountInformation(detailLine);
        }

        return true;
    }

    /**
     * @see org.kuali.module.effort.rule.UpdateDetailLineRule#processUpdateDetailLineRules(org.kuali.module.effort.document.EffortCertificationDocument,
     *      org.kuali.module.effort.bo.EffortCertificationDetail)
     */
    public boolean processUpdateDetailLineRules(EffortCertificationDocument document, EffortCertificationDetail detailLine) {
        LOG.info("processAddLineBusinessRules() start");

        if (!this.checkDetailLineAttributes(detailLine)) {
            return false;
        }

        if (!EffortCertificationDocumentRuleUtil.hasValidEffortPercent(detailLine)) {
            GlobalVariables.getErrorMap().putError(EffortPropertyConstants.EFFORT_CERTIFICATION_UPDATED_OVERALL_PERCENT, EffortKeyConstants.ERROR_INVALID_EFFORT_PERCENT);
            return false;
        }

        if (!EffortCertificationDocumentRuleUtil.hasNonnegativePayrollAmount(detailLine)) {
            GlobalVariables.getErrorMap().putError(EffortPropertyConstants.EFFORT_CERTIFICATION_PAYROLL_AMOUNT, EffortKeyConstants.ERROR_NEGATIVE_PAYROLL_AMOUNT);
            return false;
        }

        if (!EffortCertificationDocumentRuleUtil.isPayrollAmountOverChanged(detailLine, EffortConstants.LIMIT_OF_LINE_SALARY_CHANGE)) {
            GlobalVariables.getErrorMap().putError(EffortPropertyConstants.EFFORT_CERTIFICATION_PAYROLL_AMOUNT, EffortKeyConstants.ERROR_PAYROLL_AMOUNT_OVERCHANGED);
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

        return valid;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        LOG.info("processAddLineBusinessRules() start");

        EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) document;
        if (this.bypassBusinessRuleIfInitiation(effortCertificationDocument)) {
            return true;
        }
        
        if(EffortCertificationDocumentRuleUtil.isPayrollAmountChanged(effortCertificationDocument)) {
            List<Note> notes = effortCertificationDocument.getBoNotes();
            if(notes == null || notes.isEmpty()) {
                GlobalVariables.getErrorMap().putError(EffortPropertyConstants.EFFORT_CERTIFICATION_DETAIL_LINES, EffortKeyConstants.ERROR_NOTE_REQUIRED_WHEN_EFFORT_CHANGED);
                return false;
            }
        }

        if (EffortCertificationDocumentRuleUtil.isTotalPayrollAmountOverChanged(effortCertificationDocument, EffortConstants.LIMIT_OF_TOTAL_SALARY_CHANGE)) {
            GlobalVariables.getErrorMap().putError(EffortPropertyConstants.EFFORT_CERTIFICATION_DETAIL_LINES, EffortKeyConstants.ERROR_TOTAL_PAYROLL_AMOUNT_OVERCHANGED, EffortConstants.LIMIT_OF_TOTAL_SALARY_CHANGE.toString());
            return false;
        }

        if (EffortCertificationDocumentRuleUtil.isTotalEffortPercentageAs100(effortCertificationDocument)) {
            GlobalVariables.getErrorMap().putError(EffortPropertyConstants.EFFORT_CERTIFICATION_DETAIL_LINES, EffortKeyConstants.ERROR_TOTAL_EFFORT_PERCENTAGE_NOT_100);
            return false;
        }

        return true;
    }

    /**
     * check if the attributes in the detail line are valid for the defintions in data dictionary and have valid references
     * @param detailLine the given effort certification detail line
     * @return true if the attributes in the detail line are valid for the defintions in data dictionary and have valid references; otherwise, false
     */
    private boolean checkDetailLineAttributes(EffortCertificationDetail detailLine) {
        LOG.debug("checkDetailLine() start");

        detailLine.refreshNonUpdateableReferences();

        // check if the fields in the detail line are in the correct formats defined in the data dictionary
        boolean hasCorrectFormat = EffortCertificationDocumentRuleUtil.hasValidFormat(detailLine);

        // if the formats of the fields are correct, check if there exist the references of a set of specified fields
        if (hasCorrectFormat) {
            return EffortCertificationDocumentRuleUtil.hasValidReferences(detailLine);
        }

        return true;
    }

    /**
     * get the comparable fields of a detail line
     * 
     * @return the comparable fields of a detail line
     */
    private List<String> getComparableFields() {
        List<String> comparableFields = new ArrayList<String>();
        comparableFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        comparableFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        comparableFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        return comparableFields;
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
}