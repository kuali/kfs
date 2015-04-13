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

import java.math.BigDecimal;
import java.util.List;

import org.apache.ojb.broker.PersistenceBrokerAware;
import org.kuali.kfs.integration.purap.ExternalPurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

/**
 * Purap Item Business Object.
 */
public interface PurApItem extends PersistableBusinessObject, PersistenceBrokerAware, PurapEnterableItem, ExternalPurApItem {

    public abstract Integer getItemIdentifier();

    public abstract void setItemIdentifier(Integer ItemIdentifier);

    public abstract Integer getItemLineNumber();

    public abstract void setItemLineNumber(Integer itemLineNumber);

    public abstract String getItemUnitOfMeasureCode();

    public abstract void setItemUnitOfMeasureCode(String itemUnitOfMeasureCode);

    public abstract String getItemCatalogNumber();

    public abstract void setItemCatalogNumber(String itemCatalogNumber);

    public abstract String getItemDescription();

    public abstract void setItemDescription(String itemDescription);

    public abstract BigDecimal getItemUnitPrice();

    public abstract void setItemUnitPrice(BigDecimal itemUnitPrice);

    public abstract String getItemTypeCode();

    public abstract void setItemTypeCode(String itemTypeCode);

    public abstract String getItemAuxiliaryPartIdentifier();

    public abstract void setItemAuxiliaryPartIdentifier(String itemAuxiliaryPartIdentifier);

    public abstract String getExternalOrganizationB2bProductReferenceNumber();

    public abstract void setExternalOrganizationB2bProductReferenceNumber(String externalOrganizationB2bProductReferenceNumber);

    public abstract String getExternalOrganizationB2bProductTypeName();

    public abstract void setExternalOrganizationB2bProductTypeName(String externalOrganizationB2bProductTypeName);

    public abstract boolean getItemAssignedToTradeInIndicator();

    public abstract void setItemAssignedToTradeInIndicator(boolean itemAssignedToTradeInIndicator);

    public abstract ItemType getItemType();

    /**
     * Sets the itemType attribute.
     * 
     * @param itemType The itemType to set.
     * @deprecated
     */
    public abstract void setItemType(ItemType itemType);

    /**
     * This method resets the transient new account method
     */
    public void resetAccount();

    public KualiDecimal getExtendedPrice();

    public KualiDecimal getTotalAmount();
    
    public void setTotalAmount(KualiDecimal totalAmount);

    /**
     * gets the total amount to remit to a vendor
     * @return pre tax total if use tax post tax if sales
     */
    public KualiDecimal getTotalRemitAmount();
    
    public KualiDecimal calculateExtendedPrice();

    public void setExtendedPrice(KualiDecimal extendedPrice);

    public KualiDecimal getItemTaxAmount();

    public void setItemTaxAmount(KualiDecimal itemTaxAmount);

    public PurApAccountingLine getNewSourceLine();

    public void setNewSourceLine(PurApAccountingLine newAccountingLine);

    public abstract Class getAccountingLineClass();

    public abstract Class getUseTaxClass();
    
    public List<PurApAccountingLine> getSourceAccountingLines();

    public void setSourceAccountingLines(List<PurApAccountingLine> purapAccountingLines);
    
    public List<PurApAccountingLine> getBaselineSourceAccountingLines();

    public List<PurApItemUseTax> getUseTaxItems();

    public void setUseTaxItems(List<PurApItemUseTax> useTaxItems);

    public KualiDecimal getItemQuantity();

    public void setItemQuantity(KualiDecimal itemQuantity);

    public String getItemIdentifierString();

    public PurApSummaryItem getSummaryItem();
    
    public <T extends PurchasingAccountsPayableDocument> T getPurapDocument();
    
    public void setPurapDocument(PurchasingAccountsPayableDocument purapDoc);
    
    public Integer getPurapDocumentIdentifier();
    
    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier);
    
    public void fixAccountReferences();
}
