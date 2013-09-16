/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.integration.ld;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public interface LaborLedgerPositionObjectGroup extends ExternalizableBusinessObject {

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
    abstract boolean isActive();

    /**
     * This method makes up for some sillyness in DataDictionary
     * @return
     */
    abstract boolean getActive();

    /**
     * Sets the rowActiveIndicator
     *
     * @param rowActiveIndicator The rowActiveIndicator to set.
     */
    abstract void setActive(boolean rowActiveIndicator);

}
