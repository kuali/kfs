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
package org.kuali.module.purap.service;

import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;

public interface ReceivingService {

    /**
     * Populates a Receiving Line Document with information from a Purchase Order.
     * 
     * @param rlDoc
     * @param poDocId
     */
    public void populateReceivingLineFromPurchaseOrder(ReceivingLineDocument rlDoc, String poDocId);
 
    /**
     * Performs a threshold check on the purchase order to determine if any attribute on the purchase order
     * falls within a defined threshold. This check is only perfromed if the receiving required flag is set to N.
     * 
     * @param po
     */
    public void setReceivingRequiredIndicatorForPurchaseOrder(PurchaseOrderDocument po);
    
}
