/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.document.AccountingDocumentBase;

/**
 * The Transfer of Funds (TF) document is used to transfer funds (cash) between accounts. There are two kinds of transfer
 * transactions, mandatory and non-mandatory. Mandatory transfers are required to meet contractual agreements. Specific object codes
 * are used to identify these transactions. Examples of these are: moving dedicated student fees to the retirement of indebtedness
 * fund group for principal and interest payments on bonds. Non-mandatory transfers are allocations of unrestricted cash between
 * fund groups which are not required either by the terms of a loan or by other external agreements. These transfers are the most
 * commonly used throughout the university.
 */
public class TransferOfFundsDocument extends AccountingDocumentBase implements Copyable, Correctable, AmountTotaling{
    private static final long serialVersionUID = -3871133713027969492L;

    /**
     * Initializes the array lists and some basic info.
     */
    public TransferOfFundsDocument() {
        super();
    }

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     * 
     * @see org.kuali.kfs.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.TO;
    }
}
