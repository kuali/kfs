/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.KFSPropertyConstants;

/**
 * 
 */
public class RoutingFormOtherCostShare extends PersistableBusinessObjectBase {

    private Integer routingFormCostShareSequenceNumber;
    private String documentNumber;
    private String routingFormCostShareSourceName;
    private KualiInteger routingFormCostShareAmount;

    /**
     * Default constructor.
     */
    public RoutingFormOtherCostShare() {

    }

    /**
     * Gets the routingFormCostShareSequenceNumber attribute.
     * 
     * @return Returns the routingFormCostShareSequenceNumber
     */
    public Integer getRoutingFormCostShareSequenceNumber() {
        return routingFormCostShareSequenceNumber;
    }

    /**
     * Sets the routingFormCostShareSequenceNumber attribute.
     * 
     * @param routingFormCostShareSequenceNumber The routingFormCostShareSequenceNumber to set.
     */
    public void setRoutingFormCostShareSequenceNumber(Integer routingFormCostShareSequenceNumber) {
        this.routingFormCostShareSequenceNumber = routingFormCostShareSequenceNumber;
    }


    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the routingFormCostShareSourceName attribute.
     * 
     * @return Returns the routingFormCostShareSourceName
     */
    public String getRoutingFormCostShareSourceName() {
        return routingFormCostShareSourceName;
    }

    /**
     * Sets the routingFormCostShareSourceName attribute.
     * 
     * @param routingFormCostShareSourceName The routingFormCostShareSourceName to set.
     */
    public void setRoutingFormCostShareSourceName(String routingFormCostShareSourceName) {
        this.routingFormCostShareSourceName = routingFormCostShareSourceName;
    }

    /**
     * Gets the routingFormCostShareAmount attribute.
     * 
     * @return Returns the routingFormCostShareAmount
     */
    public KualiInteger getRoutingFormCostShareAmount() {
        return routingFormCostShareAmount;
    }

    /**
     * Sets the routingFormCostShareAmount attribute.
     * 
     * @param routingFormCostShareAmount The routingFormCostShareAmount to set.
     */
    public void setRoutingFormCostShareAmount(KualiInteger routingFormCostShareAmount) {
        this.routingFormCostShareAmount = routingFormCostShareAmount;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.routingFormCostShareSequenceNumber != null) {
            m.put("routingFormCostShareSequenceNumber", this.routingFormCostShareSequenceNumber.toString());
        }
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
}
