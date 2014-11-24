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
package org.kuali.kfs.pdp.businessobject;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class FormatSelection extends TransientBusinessObjectBase {
    String campus;
    Date startDate;
    List customerList;
    List rangeList;

    public FormatSelection() {
        super();
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public List getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List customerList) {
        this.customerList = customerList;
    }

    public List getRangeList() {
        return rangeList;
    }

    public void setRangeList(List rangeList) {
        this.rangeList = rangeList;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap(); 
        
        m.put(PdpPropertyConstants.FormatSelection.CAMPUS, this.campus);
        m.put(PdpPropertyConstants.FormatSelection.START_DATE, this.startDate);
        m.put(PdpPropertyConstants.FormatSelection.CUSTOMER_LIST, this.customerList);
        m.put(PdpPropertyConstants.FormatSelection.RANGE_LIST, this.rangeList);
        
        return m;
    }
}
