/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.util;

import org.kuali.core.util.KualiDecimal;

public class OriginEntryStatistics {
    private KualiDecimal debitTotalAmount = KualiDecimal.ZERO;
    private KualiDecimal creditTotalAmount = KualiDecimal.ZERO;
    private Integer rowCount = 0;

    public void addDebit(KualiDecimal d) {
        if (d != null) {
            debitTotalAmount = debitTotalAmount.add(d);
        }
    }
    public void addCredit(KualiDecimal c) {
        if (c != null) {
            creditTotalAmount = creditTotalAmount.add(c);
        }
    }
    public void incrementCount() {
        rowCount++;
    }

    public KualiDecimal getCreditTotalAmount() {
        return creditTotalAmount;
    }
    public void setCreditTotalAmount(KualiDecimal creditTotalAmount) {
        this.creditTotalAmount = creditTotalAmount;
    }
    public KualiDecimal getDebitTotalAmount() {
        return debitTotalAmount;
    }
    public void setDebitTotalAmount(KualiDecimal debitTotalAmount) {
        this.debitTotalAmount = debitTotalAmount;
    }
    public Integer getRowCount() {
        return rowCount;
    }
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }
}
