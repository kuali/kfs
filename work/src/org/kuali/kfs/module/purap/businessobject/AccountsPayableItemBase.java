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

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;

public abstract class AccountsPayableItemBase extends PurApItemBase implements AccountsPayableItem {
    private KualiDecimal extendedPrice;

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
    
}
