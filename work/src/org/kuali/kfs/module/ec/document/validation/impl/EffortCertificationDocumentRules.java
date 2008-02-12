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
import java.util.Arrays;
import java.util.List;

import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rule.GenerateSalaryExpenseTransferDocumentRule;
import org.kuali.module.effort.service.EffortCertificationDocumentService;
import org.kuali.module.effort.util.EffortCertificationParameterFinder;


/**
 * To define the rules that may be applied to the effort certification document, a transactional document
 */
public class EffortCertificationDocumentRules extends TransactionalDocumentRuleBase implements GenerateSalaryExpenseTransferDocumentRule<EffortCertificationDocument> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDocumentRules.class);

    public final KualiDecimal LIMIT_OF_LINE_SALARY_CHANGE = new KualiDecimal(0.005);
    public final KualiDecimal LIMIT_OF_TOTAL_SALARY_CHANGE = new KualiDecimal(0.009);
    
    private EffortCertificationDocumentService effortCertificationDocumentService = SpringContext.getBean(EffortCertificationDocumentService.class);

    /**
     * Constructs a EffortCertificationDocumentRules.java.
     */
    public EffortCertificationDocumentRules() {
        super();
    }

    /**
     * @see org.kuali.module.effort.rule.GenerateSalaryExpenseTransferDocumentRule#processGenerateSalaryExpenseTransferDocument(org.kuali.module.effort.document.EffortCertificationDocument)
     */
    public boolean processGenerateSalaryExpenseTransferDocument(EffortCertificationDocument effortCertificationDocument) {
        LOG.info("processGenerateSalaryExpenseTransferDocument() start");

        if (!effortCertificationDocument.getDocumentHeader().getWorkflowDocument().stateIsInitiated()) {
            return effortCertificationDocumentService.generateSalaryExpenseTransferDocument(effortCertificationDocument);
        }
        return true;
    }

    public boolean processCustomAddAccountingLineBusinessRules(EffortCertificationDocument document, EffortCertificationDetail detailLine) {
        /*
         * if (!isFringeBenefitObjectCode(accountingLine)) { reportError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE,
         * LaborKeyConstants.INVALID_FRINGE_OBJECT_CODE_ERROR, accountingLine.getAccountNumber()); isValid = false; } // Only check
         * this rule for source accounting lines boolean isTargetLine = accountingLine.isTargetAccountingLine(); if (!isTargetLine) { //
         * ensure the accounts in source accounting lines are same if (!hasSameAccount(accountingDocument, accountingLine)) {
         * reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_ACCOUNT_NOT_SAME); isValid = false; } }
         */

        return false;
    }
    
    private List<String> getComparableFields() {
        List<String> comparableFields = new ArrayList<String>();
        comparableFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        comparableFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        comparableFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        
        return comparableFields;
    }
    
    public static void updateSourceAccountInformation(EffortCertificationDetail detailLine) {
        A21SubAccount a21SubAccount = detailLine.getSubAccount().getA21SubAccount();

        detailLine.setSourceChartOfAccountsCode(a21SubAccount.getCostShareChartOfAccountCode());
        detailLine.setSourceAccountNumber(a21SubAccount.getCostShareSourceAccountNumber());
        detailLine.setCostShareSourceSubAccountNumber(a21SubAccount.getCostShareSourceSubAccountNumber());
    }
}
