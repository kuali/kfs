/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.fp.businessobject;

import java.io.Serializable;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Interface to aid voucher documents for handling debit/credit amounts.
 */
public interface VoucherAccountingLineHelper extends Serializable {
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
