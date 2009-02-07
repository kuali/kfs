/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class DefaultPrincipalAddress extends PersistableBusinessObjectBase {

    private  String principalId;
    private  String campusCode;
    private  String buildingCode;
    private  String buildingRoomNumber;
    
    
    public String getBuildingCode() {
        return buildingCode;
    }


    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }


    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }


    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }


    public String getCampusCode() {
        return campusCode;
    }


    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    public String getPrincipalId() {
        return principalId;
    }


    public void setPrincipalId(String principleId) {
        this.principalId = principleId;
    }


    protected LinkedHashMap toStringMapper() {
            LinkedHashMap m = new LinkedHashMap();
            m.put("principalId", this.principalId);
            return m;
        }
}
