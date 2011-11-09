/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class LineItemReceivingStatus extends PersistableBusinessObjectBase {

    private  String lineItemReceivingStatusCode;
    private  String lineItemReceivingStatusDescription;
    
    public String getLineItemReceivingStatusCode() {
        return lineItemReceivingStatusCode;
    }

    public void setLineItemReceivingStatusCode(String lineItemReceivingStatusCode) {
        this.lineItemReceivingStatusCode = lineItemReceivingStatusCode;
    }

    public String getLineItemReceivingStatusDescription() {
        return lineItemReceivingStatusDescription;
    }

    public void setLineItemReceivingStatusDescription(String receivingLineStatusDescription) {
        this.lineItemReceivingStatusDescription = receivingLineStatusDescription;
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("lineItemReceivingStatusCode", this.lineItemReceivingStatusCode);
        m.put("lineItemReceivingStatusDescription", this.lineItemReceivingStatusDescription);
        return m;
    }
}
