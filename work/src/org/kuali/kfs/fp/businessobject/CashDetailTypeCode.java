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

import org.kuali.rice.krad.bo.KualiCodeBase;


/**
 * This class represents a CashDetailTypeCode, which is used to differentiate records of the same general data structure from one
 * another. For example, a CashReceiptDocument has checks and so does a BursarDepositDocument. To be able to re-use the same table
 * and data structure, we need a field that can act as a flag. Another example involves the fact that a CashReceipt contains a coin
 * record, a currency record, and a change out record (aggregated coin and currency together). Since we use the same tables to
 * represent this, we need an attribute to help differentiate between the bunch especially when they all three must co-exist within
 * the same parent CashReceiptDocument instance.
 */
public class CashDetailTypeCode extends KualiCodeBase {
    private static final long serialVersionUID = -5228907091959656216L;
    private String description;

    /**
     * Constructs a CashDetailTypeCode business object.
     */
    public CashDetailTypeCode() {
        super();
    }

    /**
     * Gets the description attribute.
     * 
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute value.
     * 
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
