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

package org.kuali.kfs.module.purap.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Purchase Order Quote List Business Object.
 */
public class PurchaseOrderQuoteList extends PersistableBusinessObjectBase implements MutableInactivatable{

    private Integer purchaseOrderQuoteListIdentifier;
    private String purchaseOrderQuoteListName;
    private Integer contractManagerCode;
    private String contractManagerName;
    private boolean active;
    
    private ContractManager contractManager;

    private List<PurchaseOrderQuoteListVendor> quoteListVendors;

    /**
     * Default constructor.
     */
    public PurchaseOrderQuoteList() {
        quoteListVendors = new ArrayList<PurchaseOrderQuoteListVendor>();
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
     * @deprecated
     * @param contractManager
     */
    public void setContractManager(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    public String getContractManagerName() {
        return contractManager.getContractManagerName();
    }

    public void setContractManagerName(String contractManagerName) {
        this.contractManagerName = contractManagerName;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.purchaseOrderQuoteListIdentifier != null) {
            m.put("purchaseOrderQuoteListIdentifier", this.purchaseOrderQuoteListIdentifier.toString());
        }
        return m;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
