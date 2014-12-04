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
