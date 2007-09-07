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
package org.kuali.module.purap.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurApItem;

/**
 * This class contains item utilities
 */
public class PurApItemUtils {
    
    /**
     * 
     * This method checks if an item is active.  It is used mainly when were dealing with generic items (which may be po)  
     * And need to make sure the active rules are applied if it is a poitem
     * @param item
     * @return
     */
    public static boolean checkItemActive(PurApItem item) {
        boolean active = true;
        if(item instanceof PurchaseOrderItem) {
            PurchaseOrderItem poi = (PurchaseOrderItem)item;
            active = poi.isItemActiveIndicator();
        }
        return active;
    }
    
    public static boolean isNonZeroExtended(PurApItem item) {
        return(ObjectUtils.isNotNull(item) && ObjectUtils.isNotNull(item.getExtendedPrice()) && !item.getExtendedPrice().isZero());
    }
    
    /**
     * 
     * This method is a helper to get aboveTheLineItems only from an item list
     * @param items
     * @return
     */
    public static List<PurApItem> getAboveTheLineOnly(List<PurApItem>items) {
        List<PurApItem> returnItems = new ArrayList<PurApItem>();
        for (PurApItem item : items) {
            if(item.getItemType().isItemTypeAboveTheLineIndicator()) {
                returnItems.add((PurApItem)ObjectUtils.deepCopy(item));
            }
        }
        return returnItems;
    }
    
}
