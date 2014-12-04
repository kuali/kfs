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
