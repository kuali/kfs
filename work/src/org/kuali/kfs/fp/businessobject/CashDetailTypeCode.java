/*
 * Copyright 2005-2006 The Kuali Foundation
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
