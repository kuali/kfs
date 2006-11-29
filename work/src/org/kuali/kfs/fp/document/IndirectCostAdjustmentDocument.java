/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/document/IndirectCostAdjustmentDocument.java,v $
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
package org.kuali.module.financial.document;

import org.kuali.Constants;
import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.IndirectCostAdjustmentDocumentAccountingLineParser;
import org.kuali.module.financial.rules.IndirectCostAdjustmentDocumentRuleConstants;


/**
 * 
 * 
 * 
 */
public class IndirectCostAdjustmentDocument extends TransactionalDocumentBase {

    /**
     * 
     * Constructs a IndirectCostAdjustmentDocument.java.
     */
    public IndirectCostAdjustmentDocument() {
        super();
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.GRANT;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.ICR;
    }

    /**
     * per ICA specs, adds a target(receipt) line when an source(grant) line is added using the following logic to populate the
     * target line.
     * <ol>
     * <li>receipt line's chart = chart from grant line
     * <li>receipt line's account = ICR account for the account entered on the grant line
     * <li>receipt line's object code = Financial System Parameter APC for the document global receipt line object code (see APC
     * setion below)
     * <li>receipt line's amount = amount from grant line
     * 
     * </ol>
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#addSourceAccountingLine(org.kuali.core.bo.SourceAccountingLine)
     */
    @Override
    public void addSourceAccountingLine(SourceAccountingLine line) {
        // add source
        super.addSourceAccountingLine(line);
        // create and populate target line
        TargetAccountingLine targetAccountingLine = null;
        try {
            targetAccountingLine = (TargetAccountingLine) getTargetAccountingLineClass().newInstance();
        }
        catch (Exception e) {
            throw new InfrastructureException("unable to create a target accounting line", e);
        }
        // get apc object code value
        String objectCode = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(IndirectCostAdjustmentDocumentRuleConstants.INDIRECT_COST_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING, IndirectCostAdjustmentDocumentRuleConstants.RECEIPT_OBJECT_CODE);
        targetAccountingLine.setFinancialObjectCode(objectCode);
        targetAccountingLine.setAccountNumber(line.getAccount().getIndirectCostRecoveryAcctNbr());
        targetAccountingLine.setChartOfAccountsCode(line.getChartOfAccountsCode());
        targetAccountingLine.setDocumentNumber(line.getDocumentNumber());
        targetAccountingLine.setPostingYear(line.getPostingYear());
        targetAccountingLine.setAmount(line.getAmount());
        // refresh reference objects

        targetAccountingLine.refresh();
        // add target line
        addTargetAccountingLine(targetAccountingLine);
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new IndirectCostAdjustmentDocumentAccountingLineParser();
    }
}
