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
package org.kuali.kfs.fp.document.authorization;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * Authorizer which deals with financial processing document issues, specifically sales tax lines on documents
 */
public class FinancialProcessingAccountingLineAuthorizer extends AccountingLineAuthorizerBase {
    private final static String SALES_TAX_DOCUMENT_TYPES_PARAMETER_NAME = "SALES_TAX_APPLICABLE_DOCUMENT_TYPES";
    private final static String SALES_TAX_LINE_ACCOUNT_OBJECT_CODES_PARAMETER_NAME = "SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES";

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getUnviewableBlocks(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        Set unviewableBlocks = super.getUnviewableBlocks(accountingDocument, accountingLine, newLine, currentUser);
        if (salesTaxUnviewable(accountingDocument, accountingLine)) {
            unviewableBlocks.add(KFSConstants.AccountingLineViewStandardBlockNames.SALES_TAX_BLOCK);
        }
        return unviewableBlocks;
    }

    /**
     * Determines if the given line on the given document should not show any sales tax block it has
     * @param document the document the line lives on (or will live on)
     * @param line the accounting line which perhaps should be hiding any sales tax information
     * @return true if sales tax should not be seen for the line, false otherwise
     */
    protected boolean salesTaxUnviewable(AccountingDocument document, AccountingLine line) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String docTypeCode = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(document.getClass());
        ParameterEvaluator docTypeEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, FinancialProcessingAccountingLineAuthorizer.SALES_TAX_DOCUMENT_TYPES_PARAMETER_NAME, docTypeCode);
        if (!docTypeEvaluator.evaluationSucceeds()) return true;
        if (!StringUtils.isEmpty(line.getFinancialObjectCode()) && !StringUtils.isEmpty(line.getAccountNumber())) {
            String compare = line.getAccountNumber() + ":" + line.getFinancialObjectCode();
            ParameterEvaluator salesTaxApplicableAccountAndObjectEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, APPLICATION_PARAMETER.SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES, compare);
            if (!salesTaxApplicableAccountAndObjectEvaluator.evaluationSucceeds()) return true;
            return false;
        }
        return true;
    }
    
    /**
     * adds refresh method to the action map.
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getActionMap(org.kuali.kfs.sys.document.web.AccountingLineRenderingContext, java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Override
    protected Map<String, AccountingLineViewAction> getActionMap(AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
    
        Map<String, AccountingLineViewAction> actionMap = super.getActionMap(accountingLineRenderingContext, accountingLinePropertyName, accountingLineIndex, groupTitle);

        if (accountingLineIndex != null) {
            AccountingLineViewAction refreshAction = this.getRefreshAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
            actionMap.put(KFSConstants.RETURN_METHOD_TO_CALL, refreshAction);
        }
        
        return actionMap;
    }
    
    /**
     * constructs a refresh action image and action
     * 
     * @param accountingLine
     * @param accountingLinePropertyName
     * @param accountingLineIndex
     * @param groupTitle
     * @return
     */
    protected AccountingLineViewAction getRefreshAction(AccountingLine accountingLine, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        String actionMethod = this.getRefreshLineMethod(accountingLine, accountingLinePropertyName, accountingLineIndex);
        String actionLabel = getActionLabel(KFSKeyConstants.AccountingLineViewRendering.ACCOUNTING_LINE_REFRESH_ACTION_LABEL, groupTitle, accountingLineIndex + 1);

        String actionImageName = getRiceImagePath() + "tinybutton-refresh.gif";

        return new AccountingLineViewAction(actionMethod, actionLabel, actionImageName);
    }
    
    /**
     * constructs a refresh line method
     * 
     * @param accountingLine
     * @param accountingLineProperty
     * @param accountingLineIndex
     * @return
     */
    protected String getRefreshLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        return "refresh.line" + accountingLineIndex + ".anchoraccounting" + infix + "Anchor";
    }
}

