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
package org.kuali.kfs.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Unit Of Measure Business Object.
 */
public class UnitOfMeasure extends PersistableBusinessObjectBase {

    private String itemUnitOfMeasureCode;
    private String itemUnitOfMeasureDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public UnitOfMeasure() {

    }

    public String getItemUnitOfMeasureCode() {
        return itemUnitOfMeasureCode;
    }

    public void setItemUnitOfMeasureCode(String itemUnitOfMeasureCode) {
        this.itemUnitOfMeasureCode = itemUnitOfMeasureCode;
    }

    public String getItemUnitOfMeasureDescription() {
        return itemUnitOfMeasureDescription;
    }

    public void setItemUnitOfMeasureDescription(String itemUnitOfMeasureDescription) {
        this.itemUnitOfMeasureDescription = itemUnitOfMeasureDescription;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("itemUnitOfMeasureCode", this.itemUnitOfMeasureCode);
        return m;
    }

}
