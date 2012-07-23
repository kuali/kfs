/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Purchasing Accounts Payable Item Utilities.
 * This class contains item utilities.
 */
public class PurApItemUtils {

    /**
     * Checks if an item is active. It is used mainly when were dealing with generic items (which may be po) And need to
     * make sure the active rules are applied if it is a poitem
     * 
     * @param item the purap item passed in
     * @return true if item is active
     */
    public static boolean checkItemActive(PurApItem item) {
        boolean active = true;
        if (item instanceof PurchaseOrderItem) {
            PurchaseOrderItem poi = (PurchaseOrderItem) item;
            active = poi.isItemActiveIndicator();
        }
        return active;
    }

    public static boolean isNonZeroExtended(PurApItem item) {
        return (ObjectUtils.isNotNull(item) && ObjectUtils.isNotNull(item.getExtendedPrice()) && !item.getExtendedPrice().isZero());
    }

    /**
     * Helper to get aboveTheLineItems only from an item list
     * 
     * @param items a list of items including above and below the line
     * @return below the line items only
     */
    public static List<PurApItem> getAboveTheLineOnly(List<PurApItem> items) {
        List<PurApItem> returnItems = new ArrayList<PurApItem>();
        for (PurApItem item : items) {
            if (ObjectUtils.isNotNull(item.getItemType()) && item.getItemType().isLineItemIndicator()) {
                returnItems.add((PurApItem) ObjectUtils.deepCopy(item));
            }
        }
        return returnItems;
    }

    /**
     * Counts the below the line, currently it relies on below the line being at the bottom
     * 
     * @return a count of below the line items
     */
    public static int countBelowTheLineItems(List<PurApItem> items) {
        int count = 0;
        for (int i = items.size() - 1; i > 0; i--) {
            PurApItem item = items.get(i);
            // will have to change if we stop putting below the line at bottom
            if (ObjectUtils.isNotNull(item.getItemType()) && item.getItemType().isLineItemIndicator()) {
                break;
            }
            else {
                count++;
            }
        }
        return count;
    }

}
