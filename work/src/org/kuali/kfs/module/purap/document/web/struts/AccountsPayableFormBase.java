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
package org.kuali.module.purap.web.struts.form;

import org.kuali.module.purap.bo.PurchasingApItem;

/**
 * This class is the form class for the Purchasing documents.
 */
public class AccountsPayableFormBase extends PurchasingAccountsPayableFormBase {
    
    private PurchasingApItem newPurchasingItemLine;
    private Boolean notOtherDeliveryBuilding = true;
    
    /**
     * Constructs a RequisitionForm instance and sets up the appropriately casted document. 
     */
    public AccountsPayableFormBase() {
        super();
    }
    /**
     * Gets the newPurchasingItemLine attribute. 
     * @return Returns the newPurchasingItemLine.
     */
    public PurchasingApItem getNewPurchasingItemLine() {
        return newPurchasingItemLine;
    }
    /**
     * Sets the newPurchasingItemLine attribute value.
     * @param newPurchasingItemLine The newPurchasingItemLine to set.
     */
    public void setNewPurchasingItemLine(PurchasingApItem newPurchasingItemLine) {
        this.newPurchasingItemLine = newPurchasingItemLine;
    }
    
    public PurchasingApItem getAndResetNewPurchasingItemLine() {
        PurchasingApItem aPurchasingItemLine = getNewPurchasingItemLine();
        setNewPurchasingItemLine(setupNewPurchasingItemLine());
        return aPurchasingItemLine;
    }
    
    /**
     * 
     * This method should be overriden (or see accountingLines for an alternate way of doing this with newInstance)
     * @return
     */
    public PurchasingApItem setupNewPurchasingItemLine() {
        return null;
    }
    public Boolean getNotOtherDeliveryBuilding() {
        return notOtherDeliveryBuilding;
    }
    public void setNotOtherDeliveryBuilding(Boolean notOtherDeliveryBuilding) {
        this.notOtherDeliveryBuilding = notOtherDeliveryBuilding;
    }
       
}