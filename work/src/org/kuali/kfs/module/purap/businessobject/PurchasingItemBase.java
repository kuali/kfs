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

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.ObjectUtils;

public abstract class PurchasingItemBase extends PurApItemBase implements PurchasingItem {

    public boolean isConsideredEntered() {
        // TODO PURAP - this check is here because PO 'Change' docs all extend this class not PurchaseOrderDocumentRule... is that hierarchy necessary?
        if (this instanceof PurchaseOrderItem) {
            // if item is PO item... only validate active items
            PurchaseOrderItem poi = (PurchaseOrderItem)this;
            if (!poi.isItemActiveIndicator()) {
                return false;
            }
        }
        if (!getItemType().isItemTypeAboveTheLineIndicator()) {
            if ( (ObjectUtils.isNull(getItemUnitPrice())) && (StringUtils.isBlank(getItemDescription())) && (getSourceAccountingLines().isEmpty()) ) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isEmpty() {
        return ! ( StringUtils.isNotEmpty(getItemUnitOfMeasureCode()) ||
                   StringUtils.isNotEmpty(getItemCatalogNumber()) ||
                   StringUtils.isNotEmpty(getItemDescription()) ||
                   StringUtils.isNotEmpty(getItemCapitalAssetNoteText()) ||
                   StringUtils.isNotEmpty(getItemAuxiliaryPartIdentifier()) ||
                   ObjectUtils.isNotNull(getItemQuantity()) ||
                   (ObjectUtils.isNotNull(getItemUnitPrice()) && (getItemUnitPrice().compareTo(BigDecimal.ZERO) != 0)) ||
                   ObjectUtils.isNotNull(getCapitalAssetTransactionType()) ||
                   (!this.isAccountListEmpty()));                  
    }

    public boolean isItemDetailEmpty() {
        boolean empty = true;
        empty &= ObjectUtils.isNull(getItemQuantity()) || StringUtils.isEmpty(getItemQuantity().toString());
        empty &= StringUtils.isEmpty(getItemUnitOfMeasureCode());
        empty &= StringUtils.isEmpty(getItemCatalogNumber());
        empty &= StringUtils.isEmpty(getItemDescription());
        empty &= ObjectUtils.isNull(getItemUnitPrice()) || (getItemUnitPrice().compareTo(BigDecimal.ZERO) == 0);
        return empty;
    }
}
