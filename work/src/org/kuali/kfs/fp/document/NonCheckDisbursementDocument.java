/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import static org.kuali.Constants.EMPTY_STRING;

import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.module.financial.bo.NonCheckDisbursementDocumentAccountingLineParser;

/**
 * This is the business object that represents the NonCheckDisbursementDocument in Kuali.
 * 
 * The "Non-Check Disbursement" document is used to record charges or credits directly assessed to university bank accounts. It is
 * used primarily by the Tax and Treasury Accounting office to record wire transfers, foreign drafts, etc.
 * 
 * 
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
        return EMPTY_STRING;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new NonCheckDisbursementDocumentAccountingLineParser();
    }

}
