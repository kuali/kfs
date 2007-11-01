/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Jul 27, 2004
 *
 */
package org.kuali.module.pdp.xml;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author jsissom
 */
public class XmlTrailer implements Serializable {
    private int paymentCount;
    private BigDecimal paymentTotalAmount;

    public XmlTrailer() {
        super();
    }

    public void setField(String name, String value) {
        if ("detail_count".equals(name)) {
            int pc = Integer.parseInt(value);
            setPaymentCount(pc);
        }
        else if ("detail_tot_amt".equals(name)) {
            setPaymentTotalAmount(new BigDecimal(value));
        }
    }

    /**
     * @return Returns the paymentCount.
     */
    public int getPaymentCount() {
        return paymentCount;
    }

    /**
     * @param paymentCount The paymentCount to set.
     */
    public void setPaymentCount(int paymentCount) {
        this.paymentCount = paymentCount;
    }

    /**
     * @return Returns the paymentTotalAmount.
     */
    public BigDecimal getPaymentTotalAmount() {
        return paymentTotalAmount;
    }

    /**
     * @param paymentTotalAmount The paymentTotalAmount to set.
     */
    public void setPaymentTotalAmount(BigDecimal paymentTotalAmount) {
        this.paymentTotalAmount = paymentTotalAmount;
    }
}
