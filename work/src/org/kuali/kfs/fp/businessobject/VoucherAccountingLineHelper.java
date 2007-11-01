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
package org.kuali.module.financial.bo;

import org.kuali.core.util.KualiDecimal;

/**
 * Interface to aid voucher documents for handling debit/credit amounts.
 */
public interface VoucherAccountingLineHelper {
    /**
     * This method retrieves the credit amount.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getCredit();

    /**
     * This method sets the credit amount.
     * 
     * @param credit
     */
    public void setCredit(KualiDecimal credit);

    /**
     * This method retrieves the debit amount.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getDebit();

    /**
     * This method sets the debit amount.
     * 
     * @param debit
     */
    public void setDebit(KualiDecimal debit);
}
