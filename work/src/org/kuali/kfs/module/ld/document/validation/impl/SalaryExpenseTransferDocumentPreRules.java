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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.Set;

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.document.service.SalaryExpenseTransferTransactionAgeService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business pre-rules applicable to Salary Expense Transfer documents
 */
public class SalaryExpenseTransferDocumentPreRules extends PromptBeforeValidationBase {
    protected static SalaryExpenseTransferTransactionAgeService salaryTransferTransactionAgeService;

    /**
     * Will call methods to examine a ST Document. Includes Error Certification Statement for approval by a fiscal officer if
     * appropriate.
     *
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doPrompts(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = true;

        SalaryExpenseTransferDocument stDocument = (SalaryExpenseTransferDocument) document;
        preRulesOK &= errorCertStmtApproved(stDocument);

        return preRulesOK;
    }

    /**
     * Calls methods to determine whether to show Error Certification Statement.
     *
     * @param stDocument
     * @return false if Error Certification Statement isn't approved; true if Error Certification Statement is approved, and by
     *         default
     */
    protected boolean errorCertStmtApproved(SalaryExpenseTransferDocument stDocument) {
        boolean fiscalOfficerNode = checkRouteLevel(stDocument);
        boolean parameterTriggered = checkTargetLines(stDocument);

        if (fiscalOfficerNode && parameterTriggered) {
            return showErrorCertStmt();
        }
        return true;
    }

    /**
     * This method checks the current route level. If it is at the "Account" route node, then the fiscal officer is looking at this
     * document.
     *
     * @return true if it's at the "Account" route note; false otherwise
     */
    protected boolean checkRouteLevel(SalaryExpenseTransferDocument stDocument) {
        final WorkflowDocument workflowDocument = stDocument.getDocumentHeader().getWorkflowDocument();
        Set<String> currentActiveNodes = workflowDocument.getCurrentNodeNames();

        return currentActiveNodes.contains(KFSConstants.RouteLevelNames.ACCOUNT);
    }

    /**
     * This method uses SalaryExpenseTransferTransactionAgeService to determine whether or not there are any target lines with a sub
     * fund that is in the ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter and that are older by fiscal periods than the
     * current date.
     *
     * @param stDocument
     * @return true if there is a transaction that contains the sub fund in the parameter and the original transaction is older than
     *         the number of fiscal periods in the parameter; false otherwise
     */
    protected boolean checkTargetLines(SalaryExpenseTransferDocument stDocument) {
        Integer initialPeriodsFromParameter = null;

        return !(getSalaryExpenseTransferTransactionAgeService().defaultNumberOfFiscalPeriodsCheck(stDocument.getTargetAccountingLines(), initialPeriodsFromParameter));
    }

    /**
     * Shows the Error Certification Statement and returns the result.
     *
     * @return true if the Error Certification Statement returns a "Yes"; false otherwise
     */
    protected boolean showErrorCertStmt() {
        String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(LaborKeyConstants.ErrorCertification.QUESTION_ERROR_CERTIFICATION_STMT);
        boolean approved = super.askOrAnalyzeYesNoQuestion(LaborConstants.ErrorCertification.GENERATE_ERROR_CERTIFICATION_STMT_ID, questionText);

        if (!approved) {
            super.event.setActionForwardName(RiceConstants.MAPPING_BASIC);
        }
        return approved;
    }

    /**
     * Gets the SalaryTransferTransactionAgeService. Gets service from Spring since it isn't injected.
     *
     * @return Returns the salaryTransferTransactionAgeService
     */
    public SalaryExpenseTransferTransactionAgeService getSalaryExpenseTransferTransactionAgeService() {
        if (ObjectUtils.isNull(salaryTransferTransactionAgeService)) {
            salaryTransferTransactionAgeService = SpringContext.getBean(SalaryExpenseTransferTransactionAgeService.class);
        }

        return salaryTransferTransactionAgeService;
    }

    /**
     * Sets the salaryTransferTransactionAgeService.
     *
     * @param salaryTransferTransactionAgeService
     */
    public void setSalaryExpenseTransferTransactionAgeService(SalaryExpenseTransferTransactionAgeService salaryTransferTransactionAgeService) {
        this.salaryTransferTransactionAgeService = salaryTransferTransactionAgeService;
    }

}
