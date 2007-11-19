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
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;
import org.kuali.module.vendor.bo.ContractManager;

/**
 * Purchase Order Quote List Business Object.
 * 
 * THIS CODE IS NOT USED IN RELEASE 2 BUT THE CODE WAS LEFT IN TO
 * FACILITATE TURNING IT BACK ON EARLY IN THE DEVELOPMENT CYCLE OF RELEASE 3.
 * 
 */
public class PurchaseOrderQuoteList extends PersistableBusinessObjectBase {

    private Integer purchaseOrderQuoteListIdentifier;
    private String purchaseOrderQuoteListName;
    private Integer contractManagerCode;

    private ContractManager contractManager;

    private List<PurchaseOrderQuoteListVendor> quoteListVendors;

    /**
     * Default constructor.
     */
    public PurchaseOrderQuoteList() {
        quoteListVendors = new TypedArrayList(PurchaseOrderQuoteListVendor.class);
    }

    public Integer getPurchaseOrderQuoteListIdentifier() {
        return purchaseOrderQuoteListIdentifier;
    }

    public void setPurchaseOrderQuoteListIdentifier(Integer purchaseOrderQuoteListIdentifier) {
        this.purchaseOrderQuoteListIdentifier = purchaseOrderQuoteListIdentifier;
    }

    public String getPurchaseOrderQuoteListName() {
        return purchaseOrderQuoteListName;
    }

    public void setPurchaseOrderQuoteListName(String purchaseOrderQuoteListName) {
        this.purchaseOrderQuoteListName = purchaseOrderQuoteListName;
    }

    public Integer getContractManagerCode() {
        return contractManagerCode;
    }

    public void setContractManagerCode(Integer contractManagerCode) {
        this.contractManagerCode = contractManagerCode;
    }

    public List<PurchaseOrderQuoteListVendor> getQuoteListVendors() {
        return quoteListVendors;
    }

    public void setQuoteListVendors(List<PurchaseOrderQuoteListVendor> quoteListVendors) {
        this.quoteListVendors = quoteListVendors;
    }

    public ContractManager getContractManager() {
        return contractManager;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.purchaseOrderQuoteListIdentifier != null) {
            m.put("purchaseOrderQuoteListIdentifier", this.purchaseOrderQuoteListIdentifier.toString());
        }
        return m;
    }

}
