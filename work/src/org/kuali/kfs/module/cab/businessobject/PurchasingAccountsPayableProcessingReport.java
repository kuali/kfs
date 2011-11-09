/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cab.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class PurchasingAccountsPayableProcessingReport extends GeneralLedgerEntry {
    private Integer purapDocumentIdentifier;
    private KualiDecimal reportAmount;

    /**
     * Gets the purapDocumentIdentifier attribute. 
     * @return Returns the purapDocumentIdentifier.
     */
    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    /**
     * Sets the purapDocumentIdentifier attribute value.
     * @param purapDocumentIdentifier The purapDocumentIdentifier to set.
     */
    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    /**
     * Gets the reportAmount attribute. 
     * @return Returns the reportAmount.
     */
    public KualiDecimal getReportAmount() {
        return reportAmount;
    }

    /**
     * Sets the reportAmount attribute value.
     * @param reportAmount The reportAmount to set.
     */
    public void setReportAmount(KualiDecimal reportAmount) {
        this.reportAmount = reportAmount;
    }
    
    
}
