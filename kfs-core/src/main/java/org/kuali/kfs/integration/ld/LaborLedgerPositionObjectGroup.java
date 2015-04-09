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
