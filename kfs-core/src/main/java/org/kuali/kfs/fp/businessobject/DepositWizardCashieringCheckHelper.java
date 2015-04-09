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
package org.kuali.kfs.fp.businessobject;

/**
 * This class works with the Deposit Wizard form to figure out which cashiering checks are to be included in a given deposit
 */
public class DepositWizardCashieringCheckHelper {
    private Integer sequenceId;

    public DepositWizardCashieringCheckHelper() {
        sequenceId = new Integer(-1);
    }

    /**
     * Gets the sequenceId attribute.
     * 
     * @return Returns the sequenceId.
     */
    public Integer getSequenceId() {
        return sequenceId;
    }

    /**
     * Sets the sequenceId attribute value.
     * 
     * @param sequenceId The sequenceId to set.
     */
    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }
}
