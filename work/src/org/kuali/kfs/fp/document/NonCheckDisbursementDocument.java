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
import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.module.financial.bo.NonCheckDisbursementDocumentAccountingLineParser;

/**
 * This is the business object that represents the NonCheckDisbursementDocument in Kuali.
 * 
 * The "Non-Check Disbursement" document is used to record charges or credits directly assessed to university bank accounts. It is
 * used primarily by the Tax and Treasury Accounting office to record wire transfers, foreign drafts, etc.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class NonCheckDisbursementDocument extends TransactionalDocumentBase {


    /**
     * Constructs a NonCheckDisbursementDocument instance.
     */
    public NonCheckDisbursementDocument() {
    }

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.core.document.TransactionalDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     * 
     * @see org.kuali.core.document.TransactionalDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.TO;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new NonCheckDisbursementDocumentAccountingLineParser();
    }

}
