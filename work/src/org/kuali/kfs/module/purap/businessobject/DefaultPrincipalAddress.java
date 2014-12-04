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

import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class DefaultPrincipalAddress extends PersistableBusinessObjectBase {

    private  String principalId;
    private  String campusCode;
    private  String buildingCode;
    private  String buildingRoomNumber;
    
    private Building building;
    
    public DefaultPrincipalAddress() {
        super();
    }
    
    public DefaultPrincipalAddress(String principalId) {
        super();
        setPrincipalId(principalId);
    }
    
    public DefaultPrincipalAddress(String principalId, String campusCode, String buildingCode, String buildingRoomNumber) {
        super();
        setPrincipalId(principalId);
        setCampusCode(campusCode);
        setBuildingCode(buildingCode);
        setBuildingRoomNumber(buildingRoomNumber);
    }
    
    public void setDefaultBuilding(String campusCode, String buildingCode, String buildingRoomNumber) {
        setCampusCode(campusCode);
        setBuildingCode(buildingCode);
        setBuildingRoomNumber(buildingRoomNumber);
    }
    
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


    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
            LinkedHashMap m = new LinkedHashMap();
            m.put("principalId", this.principalId);
            return m;
        }


    public Building getBuilding() {
        return building;
    }


    public void setBuilding(Building building) {
        this.building = building;
    }
}
