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

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public abstract class PurchasingItemCapitalAssetBase extends PersistableBusinessObjectBase implements ItemCapitalAsset {

    private Long capitalAssetNumber;
    private Integer capitalAssetSystemIdentifier;
    private Integer itemCapitalAssetIdentifier;
    private CapitalAssetSystem capitalAssetSystem;
    
    /**
     * Default constructor
     */
    public PurchasingItemCapitalAssetBase() {       
        super();
    }
    
    /**
     * Constructs a PurchasingItemCapitalAsset.
     * @param capitalAssetNumber
     */
    public PurchasingItemCapitalAssetBase(Long capitalAssetNumber){
        this.capitalAssetNumber = capitalAssetNumber;
    }
        
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }
    
    public Integer getCapitalAssetSystemIdentifier() {
        return capitalAssetSystemIdentifier;
    }

    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier) {
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
    }

    public Integer getItemCapitalAssetIdentifier() {
        return itemCapitalAssetIdentifier;
    }

    public void setItemCapitalAssetIdentifier(Integer itemCapitalAssetIdentifier) {
        this.itemCapitalAssetIdentifier = itemCapitalAssetIdentifier;
    }

    public CapitalAssetSystem getCapitalAssetSystem() {
        return capitalAssetSystem;
    }

    public void setCapitalAssetSystem(CapitalAssetSystem capitalAssetSystem) {
        this.capitalAssetSystem = capitalAssetSystem;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        return m;
    }

}
