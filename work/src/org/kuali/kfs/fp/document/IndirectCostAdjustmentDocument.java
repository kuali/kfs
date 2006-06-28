/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.document;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
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
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
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
        targetAccountingLine.setAmount(line.getAmount());
        targetAccountingLine.setPostingYear(line.getPostingYear());
        targetAccountingLine.setFinancialDocumentNumber(line.getFinancialDocumentNumber());
        // refresh reference objects
        targetAccountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
        targetAccountingLine.refreshReferenceObject(PropertyConstants.ACCOUNT);
        targetAccountingLine.refreshReferenceObject(PropertyConstants.CHART);
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
