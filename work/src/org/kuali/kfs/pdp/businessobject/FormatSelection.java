/*
 * Copyright 2007 The Kuali Foundation
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
