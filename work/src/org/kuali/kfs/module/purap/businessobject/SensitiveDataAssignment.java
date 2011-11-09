/*
 * Copyright 2008 The Kuali Foundation
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
