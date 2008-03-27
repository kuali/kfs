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

package org.kuali.module.labor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.integration.bo.LaborLedgerPositionObjectGroup;

/**
 * Labor business object for PositionObjectGroup
 */
public class PositionObjectGroup extends PersistableBusinessObjectBase implements LaborLedgerPositionObjectGroup {
    private String positionObjectGroupCode;
    private String positionObjectGroupName;
    private boolean rowActiveIndicator;

    /**
     * Default constructor.
     */
    public PositionObjectGroup() {

    }

    /**
     * Gets the positionObjectGroupCode
     * 
     * @return Returns the positionObjectGroupCode
     */
    public String getPositionObjectGroupCode() {
        return positionObjectGroupCode;
    }

    /**
     * Sets the positionObjectGroupCode
     * 
     * @param positionObjectGroupCode The positionObjectGroupCode to set.
     */
    public void setPositionObjectGroupCode(String positionObjectGroupCode) {
        this.positionObjectGroupCode = positionObjectGroupCode;
    }

    /**
     * Gets the positionObjectGroupName
     * 
     * @return Returns the positionObjectGroupName
     */
    public String getPositionObjectGroupName() {
        return positionObjectGroupName;
    }

    /**
     * Sets the positionObjectGroupName
     * 
     * @param positionObjectGroupName The positionObjectGroupName to set.
     */
    public void setPositionObjectGroupName(String positionObjectGroupName) {
        this.positionObjectGroupName = positionObjectGroupName;
    }

    /**
     * Gets the rowActiveIndicator
     * 
     * @return Returns the rowActiveIndicator
     */
    public boolean isRowActiveIndicator() {
        return rowActiveIndicator;
    }

    /**
     * Sets the rowActiveIndicator
     * 
     * @param rowActiveIndicator The rowActiveIndicator to set.
     */
    public void setRowActiveIndicator(boolean rowActiveIndicator) {
        this.rowActiveIndicator = rowActiveIndicator;
    }

    /**
     * construct the key list of the business object.
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("positionObjectGroupCode", this.positionObjectGroupCode);
        return m;
    }
}
