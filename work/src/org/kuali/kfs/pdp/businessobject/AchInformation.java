/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.TransientBusinessObjectBase;

/**
 * @author jsissom
 */
public class AchInformation extends TransientBusinessObjectBase {
    private String idType;
    private String payeeId;
    private String departmentCode;
    private String achBankRoutingNbr;
    private String achBankAccountNbr;
    private String achAccountType;
    private String adviceEmailAddress;

    public AchInformation() {
        super();
    }

    public String getAchBankAccountNbr() {
        return achBankAccountNbr;
    }

    public void setAchBankAccountNbr(String achBankAccountNbr) {
        this.achBankAccountNbr = achBankAccountNbr;
    }

    public String getAchBankRoutingNbr() {
        return achBankRoutingNbr;
    }

    public void setAchBankRoutingNbr(String achBankRoutingNbr) {
        this.achBankRoutingNbr = achBankRoutingNbr;
    }

    public String getAdviceEmailAddress() {
        return adviceEmailAddress;
    }

    public void setAdviceEmailAddress(String adviceEmailAddress) {
        this.adviceEmailAddress = adviceEmailAddress;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public String getAchAccountType() {
        return achAccountType;
    }

    public void setAchAccountType(String achAccountType) {
        this.achAccountType = achAccountType;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put("idType", this.idType);
        m.put("payeeId", this.payeeId);
        m.put("departmentCode", this.departmentCode);
        m.put("achBankRoutingNbr", this.achBankRoutingNbr);
        m.put("achBankAccountNbr", this.achBankAccountNbr);
        m.put("achAccountType", this.achAccountType);
        m.put("adviceEmailAddress", this.adviceEmailAddress);
        
        return m;
    }
}
