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

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.workflow.service.KualiWorkflowDocument;

/**
 * This is the business object that represents the NonCheckDisbursementDocument in Kuali.
 * 
 * I don't have any idea what this does
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class NonCheckDisbursementDocument extends TransactionalDocumentBase {


    /**
     * Constructs a NonCheckDisbursementDocument instance.
     */
    public NonCheckDisbursementDocument() {
    }

//    /**
//     * Makes sure the document header is prepared for account and organization based routing. This overrides the parent to pass in
//     * the target accounting lines list and not the source lines list.
//     */
//    public void populateDocumentForRouting() {
//        KualiWorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
//        addAttributeDefinitionsForAccountingLines(workflowDocument, targetAccountingLines);
//    }

    /**
     * This method calculates the debit total for a <code>NonCheckDisbursementDocument keying off of the </code> debit/debit code,
     * only summing the accounting lines with a debitDebitCode that matched the debit constant, and returns the results.
     * 
     * @return KualiDecimal for decimal currency
     */
    public KualiDecimal getDebitTotal() {
        KualiDecimal debitTotal = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            if (StringUtils.equals(al.getDebitCreditCode(), Constants.GL_DEBIT_CODE)) {
                debitTotal = debitTotal.add(al.getAmount());
            }
        }
        return debitTotal;
    }

    /**
     * This method calculates the credit total for a <code>NonCheckDisbursementDocument</code> keying off of the debit/credit
     * code, only summing the accounting lines with a debitCreditCode that matched the debit constant, and returns the results.
     * 
     * @return KualiDecimal for decimal currency
     */
    public KualiDecimal getCreditTotal() {
        KualiDecimal creditTotal = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            if (StringUtils.equals(al.getDebitCreditCode(), Constants.GL_CREDIT_CODE)) {
                creditTotal = creditTotal.add(al.getAmount());
            }
        }
        return creditTotal;
    }

    /**
     * Overrides the base implementation to return "From".
     */
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     */
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.TO;
    }

}
