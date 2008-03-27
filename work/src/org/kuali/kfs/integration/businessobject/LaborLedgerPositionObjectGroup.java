/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.integration.bo;

import org.kuali.core.bo.PersistableBusinessObject;


public interface LaborLedgerPositionObjectGroup extends PersistableBusinessObject{

    /**
     * Gets the positionObjectGroupCode
     * 
     * @return Returns the positionObjectGroupCode
     */
    abstract String getPositionObjectGroupCode();

    /**
     * Sets the positionObjectGroupCode
     * 
     * @param positionObjectGroupCode The positionObjectGroupCode to set.
     */
    abstract void setPositionObjectGroupCode(String positionObjectGroupCode);

    /**
     * Gets the positionObjectGroupName
     * 
     * @return Returns the positionObjectGroupName
     */
    abstract String getPositionObjectGroupName();

    /**
     * Sets the positionObjectGroupName
     * 
     * @param positionObjectGroupName The positionObjectGroupName to set.
     */
    abstract void setPositionObjectGroupName(String positionObjectGroupName);

    /**
     * Gets the rowActiveIndicator
     * 
     * @return Returns the rowActiveIndicator
     */
    abstract boolean isRowActiveIndicator();

    /**
     * Sets the rowActiveIndicator
     * 
     * @param rowActiveIndicator The rowActiveIndicator to set.
     */
    abstract void setRowActiveIndicator(boolean rowActiveIndicator);

}