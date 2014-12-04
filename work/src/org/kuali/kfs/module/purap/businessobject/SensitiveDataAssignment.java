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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class SensitiveDataAssignment extends PersistableBusinessObjectBase {

    private  Integer sensitiveDataAssignmentIdentifier;
    private  Integer purapDocumentIdentifier;
    private  String sensitiveDataAssignmentReasonText;
    private  String sensitiveDataAssignmentPersonIdentifier;
    private  Date sensitiveDataAssignmentChangeDate;
    
    private List<SensitiveDataAssignmentDetail> sensitiveDataAssignmentDetails;
    
    public SensitiveDataAssignment() {
        super();
    }
    
    public SensitiveDataAssignment(Integer poId, String sensitiveDataAssignmentReasonText, String sensitiveDataAssignmentPersonIdentifier, List<SensitiveData> sensitiveDataToAssign) {
        super();
        setPurapDocumentIdentifier(poId);
        setSensitiveDataAssignmentReasonText(sensitiveDataAssignmentReasonText);
        setSensitiveDataAssignmentPersonIdentifier(sensitiveDataAssignmentPersonIdentifier);
        setSensitiveDataAssignmentChangeDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        
        sensitiveDataAssignmentDetails = new ArrayList<SensitiveDataAssignmentDetail>();
        for (SensitiveData sd : sensitiveDataToAssign) {
            sensitiveDataAssignmentDetails.add(new SensitiveDataAssignmentDetail(sd.getSensitiveDataCode(), this));
        }
    }
    

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    public Date getSensitiveDataAssignmentChangeDate() {
        return sensitiveDataAssignmentChangeDate;
    }

    public void setSensitiveDataAssignmentChangeDate(Date sensitiveDataAssignmentChangeDate) {
        this.sensitiveDataAssignmentChangeDate = sensitiveDataAssignmentChangeDate;
    }

    public Integer getSensitiveDataAssignmentIdentifier() {
        return sensitiveDataAssignmentIdentifier;
    }

    public void setSensitiveDataAssignmentIdentifier(Integer sensitiveDataAssignmentIdentifier) {
        this.sensitiveDataAssignmentIdentifier = sensitiveDataAssignmentIdentifier;
    }

    public String getSensitiveDataAssignmentPersonIdentifier() {
        return sensitiveDataAssignmentPersonIdentifier;
    }

    public void setSensitiveDataAssignmentPersonIdentifier(String sensitiveDataAssignmentPersonIdentifier) {
        this.sensitiveDataAssignmentPersonIdentifier = sensitiveDataAssignmentPersonIdentifier;
    }

    public String getSensitiveDataAssignmentReasonText() {
        return sensitiveDataAssignmentReasonText;
    }


    public void setSensitiveDataAssignmentReasonText(String sensitiveDataAssignmentReasonText) {
        this.sensitiveDataAssignmentReasonText = sensitiveDataAssignmentReasonText;
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("sensitiveDataAssignmentIdentifier", this.sensitiveDataAssignmentIdentifier);
        return m;
    }

    public List<SensitiveDataAssignmentDetail> getSensitiveDataAssignmentDetails() {
        return sensitiveDataAssignmentDetails;
    }

    public void setSensitiveDataAssignmentDetails(List<SensitiveDataAssignmentDetail> sensitiveDataAssignmentDetails) {
        this.sensitiveDataAssignmentDetails = sensitiveDataAssignmentDetails;
    }   
}
