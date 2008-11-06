/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.service;

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;

public interface PurchasingService {

    public void setupCapitalAssetItems(PurchasingDocument purDoc);
    
    public void deleteCapitalAssetItems(PurchasingDocument purDoc, Integer itemIdentifier);
    
    public void setupCapitalAssetSystem(PurchasingDocument purDoc);

    
    /**
     * 
     * Proation for Trade in and Full Order Discount miscellaneous items.
     * @param purDoc
     */
    public void prorateForTradeInAndFullOrderDiscount(PurchasingDocument purDoc);
    public String getDefaultAssetTypeCodeNotThisFiscalYear();
    /**
     * 
     * Gets the default value for use tax
     * @param purDoc
     * @return boolean indicating value of use tax indicator
     */
    public boolean getDefaultUseTaxIndicatorValue(PurchasingDocument purDoc);
    
    public void clearAllTaxes(PurchasingDocument purDoc);
}
