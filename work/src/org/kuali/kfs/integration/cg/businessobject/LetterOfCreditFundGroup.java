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

package org.kuali.kfs.integration.cg.businessobject;

import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFundGroup;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;


/**
 * Integration Class for LetterOfCreditFundGroup
 */
public class LetterOfCreditFundGroup implements ContractsAndGrantsLetterOfCreditFundGroup, MutableInactivatable {

    private String letterOfCreditFundGroupCode;
    private String letterOfCreditFundGroupDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public LetterOfCreditFundGroup() {
    }

    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     *
     * @return Returns the letterOfCreditFundGroupCode
     */
    @Override
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    /**
     * Sets the letterOfCreditFundGroupCode attribute.
     *
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }


    /**
     * Gets the letterOfCreditFundGroupDescription attribute.
     *
     * @return Returns the letterOfCreditFundGroupDescription
     */
    @Override
    public String getLetterOfCreditFundGroupDescription() {
        return letterOfCreditFundGroupDescription;
    }

    /**
     * Sets the letterOfCreditFundGroupDescription attribute.
     *
     * @param letterOfCreditFundGroupDescription The letterOfCreditFundGroupDescription to set.
     */
    public void setLetterOfCreditFundGroupDescription(String letterOfCreditFundGroupDescription) {
        this.letterOfCreditFundGroupDescription = letterOfCreditFundGroupDescription;
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

    @Override
    public void refresh() {
    }

}
