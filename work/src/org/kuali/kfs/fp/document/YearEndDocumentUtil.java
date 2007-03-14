/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.kuali.core.document.TransactionalDocument;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.util.SpringServiceLocator;


/**
 * 
 * utils for <code>YearEndDocument</code>s
 * 
 * @see org.kuali.module.gl.service.SufficientFundsService
 * 
 */
public class YearEndDocumentUtil {
    private static final String FINAL_ACCOUNTING_PERIOD = "13";

    /**
     * 
     * @return the previous fiscal year used with all GLPE
     */
    public static final Integer getPreviousFiscalYear() {
        int i = SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear().intValue() - 1;
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
