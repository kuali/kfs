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
package org.kuali.kfs.pdp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class AchTransactionType extends PersistableBusinessObjectBase {
    private String transactionType;
    private String description;

    /**
     * Constructs a AchTransactionType.java.
     */
    public AchTransactionType() {
        super();
    }


    /**
     * Gets the transactionType attribute.
     * 
     * @return Returns the transactionType.
     */
    public String getTransactionType() {
        return transactionType;
    }


    /**
     * Sets the transactionType attribute value.
     * 
     * @param transactionType The transactionType to set.
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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


    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PdpPropertyConstants.TRANSACTION_TYPE, this.transactionType);

        return m;
    }

}
