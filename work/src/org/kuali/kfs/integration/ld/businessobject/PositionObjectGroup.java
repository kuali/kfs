/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.integration.ld.businessobject;

import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectGroup;


/**
 * Labor business object for PositionObjectGroup
 */
public class PositionObjectGroup implements LaborLedgerPositionObjectGroup {

    private String positionObjectGroupCode;
    private String positionObjectGroupName;
    private boolean active;

    /**
     * Gets the positionObjectGroupCode
     *
     * @return Returns the positionObjectGroupCode
     */
    @Override
    public String getPositionObjectGroupCode() {
        return positionObjectGroupCode;
    }

    /**
     * Sets the positionObjectGroupCode
     *
     * @param positionObjectGroupCode The positionObjectGroupCode to set.
     */
    @Override
    public void setPositionObjectGroupCode(String positionObjectGroupCode) {
        this.positionObjectGroupCode = positionObjectGroupCode;
    }

    /**
     * Gets the positionObjectGroupName
     *
     * @return Returns the positionObjectGroupName
     */
    @Override
    public String getPositionObjectGroupName() {
        return positionObjectGroupName;
    }

    /**
     * Sets the positionObjectGroupName
     *
     * @param positionObjectGroupName The positionObjectGroupName to set.
     */
    @Override
    public void setPositionObjectGroupName(String positionObjectGroupName) {
        this.positionObjectGroupName = positionObjectGroupName;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborLedgerPositionObjectGroup#getActive()
     */
    @Override
    public boolean getActive() {
        return active;
    }

    @Override
    public void refresh() {}

}
