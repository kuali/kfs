/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
