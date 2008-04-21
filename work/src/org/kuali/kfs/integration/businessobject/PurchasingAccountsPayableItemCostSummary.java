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
package org.kuali.module.integration.bo;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.util.KualiDecimal;

/**
 * Cost summary of a Purchase Order Item.
 */
public interface PurchasingAccountsPayableItemCostSummary extends PersistableBusinessObject {
    /**
     * @return Returns the purchaseOrderNumber.
     */
    public Integer getPurchaseOrderNumber();

    /**
     * @return Returns the itemLineNumber.
     */
    public Integer getItemLineNumber();

    /**
     * @return Returns the itemDescription.
     */
    public String getItemDescription();

    /**
     * @return Returns the itemOrderQuantity.
     */
    public Integer getItemOrderQuantity();

    /**
     * @return Returns the unitOfMeasureCode.
     */
    public String getUnitOfMeasureCode();

    /**
     * @return Returns the unitCost.
     */
    public KualiDecimal getUnitCost();

    /**
     * @return Returns the extendedCost.
     */
    public KualiDecimal getExtendedCost();
}
