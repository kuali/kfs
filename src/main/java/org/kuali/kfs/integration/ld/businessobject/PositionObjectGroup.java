/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
