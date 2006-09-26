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

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;


/**
 * 
 * utils for <code>YearEndDocument</code>s
 * 
 * @see org.kuali.module.gl.service.SufficientFundsService
 * @author Kuali Financial Transactions Team ()
 */
public class YearEndDocumentUtil {
    private static final String FINAL_ACCOUNTING_PERIOD = "13";

    /**
     * 
     * @return the previous fiscal year used with all GLPE
     */
    public static final Integer getPreviousFiscalYear() {
        int i = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear().intValue() - 1;
        return new Integer(i);
    }

    /**
     * 
     * @return the accounting period code used with all GLPE
     */
    public static final String getFinalAccountingPeriod() {
        return FINAL_ACCOUNTING_PERIOD;
    }

    /**
     * populates a <code>GeneralLedgerPendingEntry</code> populated with common year end document data into the explicit general
     * ledger pending entry. currently is the following:
     * <ol>
     * <li>fiscal period code = final accounting period code
     * <li>fiscal year= previous fiscal year
     * </ol>
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @param explicitEntry
     */
    public static final void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        if (!YearEndDocument.class.isAssignableFrom(transactionalDocument.getClass())) {
            throw new IllegalArgumentException("invalid (not a year end document) for class:" + transactionalDocument.getClass());
        }
        YearEndDocument yearEndDocument = (YearEndDocument) transactionalDocument;
        explicitEntry.setUniversityFiscalPeriodCode(getFinalAccountingPeriod());
        explicitEntry.setUniversityFiscalYear(getPreviousFiscalYear());
    }
}
