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
package org.kuali.module.financial.bo;

import org.kuali.core.util.KualiDecimal;

/**
 * This helper class works in conjunction with the SourceAccountingLine bo to help build the UI for the Voucher. On the business
 * object side, there is a single attribute that keeps track of this and the amount field is used to keep track of the amount. This
 * helper class sits alongside the typical SourceAccountingLine bo... the synchronization between the two is the guaranteed order.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class VoucherAccountingLineHelperBase implements VoucherAccountingLineHelper {
    private KualiDecimal debit;
    private KualiDecimal credit;

    /**
     * Constructs a <code>{@link VoucherAccountingLineHelperBase}</code> instance.
     */
    public VoucherAccountingLineHelperBase() {
        this.credit = new KualiDecimal(0);
        this.debit = new KualiDecimal(0);
    }

    /**
     * This method retrieves the credit amount.
     * 
     * @return
     */
    public KualiDecimal getCredit() {
        return credit;
    }

    /**
     * This method sets the credit amount.
     * 
     * @param credit
     */
    public void setCredit(KualiDecimal credit) {
        this.credit = credit;
    }

    /**
     * This method retrieves the debit amount.
     * 
     * @return
     */
    public KualiDecimal getDebit() {
        return debit;
    }

    /**
     * This method sets the debit amount.
     * 
     * @param debit
     */
    public void setDebit(KualiDecimal debit) {
        this.debit = debit;
    }
}
