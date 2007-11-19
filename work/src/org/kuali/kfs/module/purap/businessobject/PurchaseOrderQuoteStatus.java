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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Purchase Order Quote Status Business Object.
 * 
 * THIS CODE IS NOT USED IN RELEASE 2 BUT THE CODE WAS LEFT IN TO
 * FACILITATE TURNING IT BACK ON EARLY IN THE DEVELOPMENT CYCLE OF RELEASE 3.
 * 
 */
public class PurchaseOrderQuoteStatus extends PersistableBusinessObjectBase {

    private String purchaseOrderQuoteStatusCode;
    private String purchaseOrderQuoteStatusDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public PurchaseOrderQuoteStatus() {

    }

    public String getPurchaseOrderQuoteStatusCode() {
        return purchaseOrderQuoteStatusCode;
    }

    public void setPurchaseOrderQuoteStatusCode(String purchaseOrderQuoteStatusCode) {
        this.purchaseOrderQuoteStatusCode = purchaseOrderQuoteStatusCode;
    }

    public String getPurchaseOrderQuoteStatusDescription() {
        return purchaseOrderQuoteStatusDescription;
    }

    public void setPurchaseOrderQuoteStatusDescription(String purchaseOrderQuoteStatusDescription) {
        this.purchaseOrderQuoteStatusDescription = purchaseOrderQuoteStatusDescription;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("purchaseOrderQuoteStatusCode", this.purchaseOrderQuoteStatusCode);
        return m;
    }

}
