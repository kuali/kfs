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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderAssetTransactionType;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class PurchasingCapitalAssetItemBase extends PersistableBusinessObjectBase implements PurchasingCapitalAssetItem {

    private Integer capitalAssetItemIdentifier;
    private Integer itemIdentifier;
    private String capitalAssetTransactionTypeCode;
    private Integer capitalAssetSystemIdentifier;
    
    private CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType;
    private CapitalAssetSystem purchasingCapitalAssetSystem;
    private PurchasingDocument purchasingDocument;
    private PurchasingItem purchasingItem;
    private ItemCapitalAsset newPurchasingItemCapitalAssetLine;
    
    public PurchasingCapitalAssetItemBase(){
        super();        
        this.setNewPurchasingItemCapitalAssetLine(this.setupNewPurchasingItemCapitalAssetLine());
    }
    
    public PurchasingCapitalAssetItemBase(PurchasingDocument pd) {        
        setPurchasingDocument(pd);
    }

    public Integer getCapitalAssetItemIdentifier() {
        return capitalAssetItemIdentifier;
    }

    public void setCapitalAssetItemIdentifier(Integer capitalAssetItemIdentifier) {
        this.capitalAssetItemIdentifier = capitalAssetItemIdentifier;
    }

    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(Integer itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    public String getCapitalAssetTransactionTypeCode() {
        return capitalAssetTransactionTypeCode;
    }

    public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
    }

    public Integer getCapitalAssetSystemIdentifier() {
        return capitalAssetSystemIdentifier;
    }

    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier) {
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
    }

    public CapitalAssetBuilderAssetTransactionType getCapitalAssetTransactionType() {
        return capitalAssetTransactionType = (CapitalAssetBuilderAssetTransactionType) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CapitalAssetBuilderAssetTransactionType.class).retrieveExternalizableBusinessObjectIfNecessary(this, capitalAssetTransactionType, PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE);
    }

    public CapitalAssetSystem getPurchasingCapitalAssetSystem() {
        return purchasingCapitalAssetSystem;
    }

    public void setPurchasingCapitalAssetSystem(CapitalAssetSystem purchasingCapitalAssetSystem) {
        this.purchasingCapitalAssetSystem = purchasingCapitalAssetSystem;
    }

    public PurchasingDocument getPurchasingDocument(){
        if(ObjectUtils.isNull(this.purchasingDocument)){
            this.refreshReferenceObject("purchasingDocument");
        }
        
        return this.purchasingDocument;
    }
    
    public void setPurchasingDocument(PurchasingDocument pd){
        this.purchasingDocument = pd;        
    }
    
    public PurchasingItem getPurchasingItem(){       
        PurchasingDocument pd = this.getPurchasingDocument();
        
        if(( pd != null) && ( this.getItemIdentifier() != null)) {
            return pd.getPurchasingItem(this.getItemIdentifier());
        }
        else {
            return null;
        }
    }
    
    public boolean isEmpty() {
        if (PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL.equals(this.getPurchasingDocument().getCapitalAssetSystemTypeCode())) {
            return !(StringUtils.isNotEmpty(capitalAssetTransactionTypeCode) || ! this.getPurchasingCapitalAssetSystem().isEmpty());
        }
        else {
            return !(StringUtils.isNotEmpty(capitalAssetTransactionTypeCode) || ! this.getPurchasingDocument().getPurchasingCapitalAssetSystems().get(0).isEmpty());
        }
    }

    public void setNewPurchasingItemCapitalAssetLine(ItemCapitalAsset newItemCapitalAssetLine) {
        this.newPurchasingItemCapitalAssetLine = newItemCapitalAssetLine;
    }

    public ItemCapitalAsset getNewPurchasingItemCapitalAssetLine() {
        return newPurchasingItemCapitalAssetLine;
    }

    public ItemCapitalAsset getAndResetNewPurchasingItemCapitalAssetLine() {
        ItemCapitalAsset asset = getNewPurchasingItemCapitalAssetLine();
        setNewPurchasingItemCapitalAssetLine(setupNewPurchasingItemCapitalAssetLine());
        return asset;
    }

    public ItemCapitalAsset setupNewPurchasingItemCapitalAssetLine() {
        ItemCapitalAsset asset = null;
        return asset;
    }
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("capitalAssetItemIdentifier", this.capitalAssetItemIdentifier);
        return m;
    }

}
