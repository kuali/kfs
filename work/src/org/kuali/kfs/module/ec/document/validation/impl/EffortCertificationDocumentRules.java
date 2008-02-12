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

import static org.kuali.kfs.KFSConstants.AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.SOURCE_ACCOUNTING_LINE_ERROR_PATTERN;
import static org.kuali.kfs.KFSConstants.TARGET_ACCOUNTING_LINE_ERROR_PATTERN;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_FORMAT;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_MAX_LENGTH;
import static org.kuali.kfs.KFSKeyConstants.ERROR_INVALID_FORMAT;
import static org.kuali.kfs.KFSKeyConstants.ERROR_MAX_LENGTH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.document.Document;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingLineRuleUtil;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.effort.EffortConstants;
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

    public boolean processAddLineBusinessRules(EffortCertificationDocument document, EffortCertificationDetail detailLine) {
        LOG.info("processAddLineBusinessRules() start");

        this.applyDefaultvalues(detailLine);
        
        if(!this.checkDetailLineAttributes(detailLine)) {
            return false;
        }

        if (!EffortCertificationDocumentValidator.hasNonnegativePayrollAmount(detailLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        if (!EffortCertificationDocumentValidator.hasValidEffortPercent(detailLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        if (EffortCertificationDocumentValidator.hasSameExistingLine(document, detailLine, this.getComparableFields())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        if (EffortCertificationDocumentValidator.hasClosedAccount(detailLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        if (!EffortCertificationDocumentValidator.canExpiredAccountBeUsed(detailLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        if (EffortCertificationDocumentValidator.hasA21SubAccount(detailLine)) {
            List<String> designatedCostShareSubAccountTypeCodes = EffortCertificationParameterFinder.getCostShareSubAccountTypeCode();
            if (!EffortCertificationDocumentValidator.hasCostShareSubAccount(detailLine, designatedCostShareSubAccountTypeCodes)) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.SUB_ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
                return false;
            }

            this.updateSourceAccountInformation(detailLine);
        }

        return true;
    }

    public boolean processCustomUpdateLineBusinessRules(EffortCertificationDocument document, EffortCertificationDetail detailLine) {
        LOG.info("processAddLineBusinessRules() start");
        
        if(!this.checkDetailLineAttributes(detailLine)) {
            return false;
        }

        if (!EffortCertificationDocumentValidator.hasValidEffortPercent(detailLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        if (!EffortCertificationDocumentValidator.hasNonnegativePayrollAmount(detailLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        if (!EffortCertificationDocumentValidator.isPayrollAmountOverChanged(detailLine, LIMIT_OF_LINE_SALARY_CHANGE)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        this.updateObjectCode(detailLine);

        return true;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        LOG.info("processAddLineBusinessRules() start");

        EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) document;

        // TODO: if a change on one of accounts, provide a note

        if (EffortCertificationDocumentValidator.isPayrollAmountOverChanged(effortCertificationDocument, LIMIT_OF_TOTAL_SALARY_CHANGE)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        if (EffortCertificationDocumentValidator.isTotalEffortPercentageAs100(effortCertificationDocument)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        return true;
    }
    
    
    private boolean checkDetailLineAttributes(EffortCertificationDetail detailLine) {
        LOG.debug("checkDetailLine() start");

        detailLine.refreshNonUpdateableReferences();
        
        // check if the fields in the detail line are in the correct formats defined in the data dictionary
        boolean hasCorrectFormat = EffortCertificationDocumentValidator.hasValidFormat(detailLine);
        
        // if the formats of the fields are correct, check if there exist the references of a set of specified fields
        if(hasCorrectFormat) {
            return EffortCertificationDocumentValidator.hasValidReferences(detailLine);
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
     * update the information of the source attributes for the given detail line
     * 
     * @param detailLine the given detail line
     */
    private void updateSourceAccountInformation(EffortCertificationDetail detailLine) {
        A21SubAccount a21SubAccount = detailLine.getSubAccount().getA21SubAccount();

        if (a21SubAccount != null) {
            detailLine.setSourceChartOfAccountsCode(a21SubAccount.getCostShareChartOfAccountCode());
            detailLine.setSourceAccountNumber(a21SubAccount.getCostShareSourceAccountNumber());
            detailLine.setCostShareSourceSubAccountNumber(a21SubAccount.getCostShareSourceSubAccountNumber());
        }
    }

    /**
     * reset the attribute with the blank value to the default values
     * 
     * @param detailLine the given detail line
     */
    private void applyDefaultvalues(EffortCertificationDetail detailLine) {
        if (StringUtils.isBlank(detailLine.getSubAccountNumber())) {
            detailLine.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }

        if (StringUtils.isBlank(detailLine.getCostShareSourceSubAccountNumber())) {
            detailLine.setCostShareSourceSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }

        if (StringUtils.isBlank(detailLine.getSourceChartOfAccountsCode())) {
            detailLine.setSourceChartOfAccountsCode(EffortConstants.DASH_CHART_OF_ACCOUNTS_CODE);
        }

        if (StringUtils.isBlank(detailLine.getSourceAccountNumber())) {
            detailLine.setSourceAccountNumber(EffortConstants.DASH_ACCOUNT_NUMBER);
        }
    }
    
    /**
     * update the object code associated with the given detail line
     * @param detailLine the given detail line
     */
    private void updateObjectCode(EffortCertificationDetail detailLine) {
        List<String> designatedCostShareSubAccountTypeCodes = EffortCertificationParameterFinder.getCostShareSubAccountTypeCode();
        boolean hasCostShareSubAccount = EffortCertificationDocumentValidator.hasCostShareSubAccount(detailLine, designatedCostShareSubAccountTypeCodes);
        boolean hasContractGrantAccount = EffortCertificationDocumentValidator.hasContractGrantAccount(detailLine);
        
        String currentObjectCode = detailLine.getFinancialObjectCode();           
        String alternativeObjectCode = this.getAlternativeObjectCode(currentObjectCode, hasContractGrantAccount && !hasCostShareSubAccount);
        detailLine.setFinancialObjectCode(alternativeObjectCode);
    }

    /**
     * find the alternative object code of the given object code
     * 
     * @param objectCode the given object code
     * @param inversed indicate if the inversing is used
     * @return the alternative object code of the given object code
     */
    private String getAlternativeObjectCode(String objectCode, boolean inversed) {
        List<List<String>> objectCodeExchangeMap = this.getObjectCodeExchangeMap();

        int sourceIndex = inversed ? 1 : 0;
        int targetIndex = inversed ? 0 : 1;

        if (objectCodeExchangeMap.get(sourceIndex).contains(objectCode)) {
            int index = objectCodeExchangeMap.get(sourceIndex).indexOf(objectCode);
            return objectCodeExchangeMap.get(targetIndex).get(index);

        }

        return objectCode;
    }

    /**
     * get the object code exchange map. In each pair, one object code can be a substitute of another when a certain condition can
     * be met.
     * 
     * @return get the object code exchange map
     */
    private List<List<String>> getObjectCodeExchangeMap() {
        List<String> sourceObjectCodes = Arrays.asList("2000", "2400", "2280", "2480");
        List<String> targetObjectCodes = Arrays.asList("2008", "2408", "2288", "2488");

        List<List<String>> objectCodeExchangeMap = new ArrayList<List<String>>();
        objectCodeExchangeMap.add(sourceObjectCodes);
        objectCodeExchangeMap.add(targetObjectCodes);

        return objectCodeExchangeMap;
    }
}
