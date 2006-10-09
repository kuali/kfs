/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of 
 * the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE 
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.kuali.module.financial.document;

import org.kuali.Constants;
import org.kuali.core.document.TransactionalDocumentBase;

/**
 * The Transfer of Funds (TF) document is used to transfer funds (cash) between accounts. There are two kinds of transfer
 * transactions, mandatory and non-mandatory. Mandatory transfers are required to meet contractual agreements. Specific object codes
 * are used to identify these transactions. Examples of these are: moving dedicated student fees to the retirement of indebtedness
 * fund group for principal and interest payments on bonds. Non-mandatory transfers are allocations of unrestricted cash between
 * fund groups which are not required either by the terms of a loan or by other external agreements. These transfers are the most
 * commonly used throughout the university.
 * 
 * 
 */
public class TransferOfFundsDocument extends TransactionalDocumentBase {
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
     * @see org.kuali.core.document.TransactionalDocument#getSourceAccountingLinesSectionTitle()
     */
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     * 
     * @see org.kuali.core.document.TransactionalDocument#getTargetAccountingLinesSectionTitle()
     */
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.TO;
    }
}
