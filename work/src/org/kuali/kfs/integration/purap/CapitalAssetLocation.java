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
package org.kuali.kfs.integration.purap;

import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;


public interface CapitalAssetLocation  extends ExternalizableBusinessObject{

    public Integer getCapitalAssetSystemIdentifier();

    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier);

    public Integer getCapitalAssetLocationIdentifier();

    public void setCapitalAssetLocationIdentifier(Integer capitalAssetLocationIdentifier);

    public KualiDecimal getItemQuantity();

    public void setItemQuantity(KualiDecimal itemQuantity);

    public String getCampusCode();

    public void setCampusCode(String campusCode);

    public boolean isOffCampusIndicator();

    public void setOffCampusIndicator(boolean offCampusIndicator);

    public String getBuildingCode();

    public void setBuildingCode(String buildingCode);

    public String getBuildingRoomNumber();

    public void setBuildingRoomNumber(String buildingRoomNumber);

    public String getCapitalAssetLine1Address();

    public void setCapitalAssetLine1Address(String capitalAssetLine1Address);

    public String getCapitalAssetCityName();

    public void setCapitalAssetCityName(String capitalAssetCityName);

    public String getCapitalAssetStateCode();

    public void setCapitalAssetStateCode(String capitalAssetStateCode);

    public String getCapitalAssetPostalCode();

    public void setCapitalAssetPostalCode(String capitalAssetPostalCode);

    public String getCapitalAssetCountryCode();

    public void setCapitalAssetCountryCode(String capitalAssetCountryCode);

    public CampusParameter getCampus();

    public void setCampus(CampusParameter campus);
    
    public void templateBuilding(Building building);

}
