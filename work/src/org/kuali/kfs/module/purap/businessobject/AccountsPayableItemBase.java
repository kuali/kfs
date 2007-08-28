/*
 * Copyright 2007 The Kuali Foundation.
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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.util.PurApItemUtils;

public abstract class AccountsPayableItemBase extends PurApItemBase implements AccountsPayableItem {
    private KualiDecimal extendedPrice;
    
    /**
     * Method defaults to {@link #isConsideredEnteredWithZero()}
     * 
     * @see org.kuali.module.purap.bo.PurchasingApItem#isConsideredEntered()
     */
    public boolean isConsideredEntered() {
        return isConsideredEnteredWithZero();
    }
    
    public boolean isEligibleDisplay() {
        return isConsideredEnteredWithZero();
    }
    
    public boolean isConsideredEnteredWithZero() {
        return isConsideredEntered(true);
    }
    
    public boolean isConsideredEnteredWithoutZero() {
        return isConsideredEntered(false);
    }

    /**
     * This method is used to determine whether an item has been entered
     * that is we are satisfied there's enough info to continue processing 
     * that particular item. It is currently used by the rules class to 
     * determine when it's necessary to run rules on items (so that lines
     * processors don't touch won't be validated) and to determine when to
     * show items (in combination with the full entry mode)
     * @param allowsZero if this is true zero will be considered the same as null
     * @return true if the item is considered entered false otherwise
     */
    private boolean isConsideredEntered(boolean allowsZero) {
        if (getItemType().isItemTypeAboveTheLineIndicator()) {
            if ( (getItemType().isQuantityBasedGeneralLedgerIndicator())) {
                if ( (ObjectUtils.isNull(getItemQuantity())) && 
                     (ObjectUtils.isNull(getExtendedPrice()) || (allowsZero && getExtendedPrice().isZero())) ) {
                    return false;
                }
            }
            else {
                if ( ObjectUtils.isNull(getExtendedPrice()) || (allowsZero && getExtendedPrice().isZero()) ) {
                    return false;
                }
            }
        }
        else {
            if ( (ObjectUtils.isNull(getItemUnitPrice()) || (allowsZero && this.getItemUnitPrice().compareTo(new BigDecimal(0)) == 0)) && (StringUtils.isBlank(getItemDescription())) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the extendedPrice attribute. 
     * this override is necessary because extended price needs to be set based
     * on the unit price for below the line(without this it would always be empty)
     * NOTE: this should always return zero instead of null.
     * @return Returns the extendedPrice.
     */
    public KualiDecimal getExtendedPrice() {
        if(!this.getItemType().isItemTypeAboveTheLineIndicator() &&
           ObjectUtils.isNotNull(this.getItemUnitPrice())) {
           extendedPrice = new KualiDecimal(this.getItemUnitPrice().toString());
        }
        
        if(ObjectUtils.isNull(extendedPrice)) {
           extendedPrice = KualiDecimal.ZERO;
        }
        
        return extendedPrice;
    }

    /**
     * Sets the extendedPrice attribute value.
     * @param extendedPrice The extendedPrice to set.
     */
    public void setExtendedPrice(KualiDecimal extendedPrice) {
        this.extendedPrice = extendedPrice;
    }
    
    public boolean isNonZeroAmount() {
        return PurApItemUtils.isNonZeroExtended(this);
    }
    
}
