package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Building;

/**
 * Building Parameter Business Object. Maintenance document for building parameters.
 */
public class BuildingParameter extends PersistableBusinessObjectBase {

    private String campusCode;
    private String buildingCode;
    private String buildingOverrideCode;
    private String buildingOverrideName;
    private String buildingOverrideStreetAddress;
    private String buildingOverrideAddressCityName;
    private String buildingOverrideAddressStateCode;
    private String buildingOverrideAddressZipCode;

    private Campus campus;
    private Building building;

    /**
     * Default constructor.
     */
    public BuildingParameter() {

    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getBuildingOverrideCode() {
        return buildingOverrideCode;
    }

    public void setBuildingOverrideCode(String buildingOverrideCode) {
        this.buildingOverrideCode = buildingOverrideCode;
    }

    public String getBuildingOverrideName() {
        return buildingOverrideName;
    }

    public void setBuildingOverrideName(String buildingOverrideName) {
        this.buildingOverrideName = buildingOverrideName;
    }

    public String getBuildingOverrideStreetAddress() {
        return buildingOverrideStreetAddress;
    }

    public void setBuildingOverrideStreetAddress(String buildingOverrideStreetAddress) {
        this.buildingOverrideStreetAddress = buildingOverrideStreetAddress;
    }

    public String getBuildingOverrideAddressCityName() {
        return buildingOverrideAddressCityName;
    }

    public void setBuildingOverrideAddressCityName(String buildingOverrideAddressCityName) {
        this.buildingOverrideAddressCityName = buildingOverrideAddressCityName;
    }

    public String getBuildingOverrideAddressStateCode() {
        return buildingOverrideAddressStateCode;
    }

    public void setBuildingOverrideAddressStateCode(String buildingOverrideAddressStateCode) {
        this.buildingOverrideAddressStateCode = buildingOverrideAddressStateCode;
    }

    public String getBuildingOverrideAddressZipCode() {
        return buildingOverrideAddressZipCode;
    }

    public void setBuildingOverrideAddressZipCode(String buildingOverrideAddressZipCode) {
        this.buildingOverrideAddressZipCode = buildingOverrideAddressZipCode;
    }

    public Campus getCampus() {
        return campus;
    }

    /**
     * @deprecated
     */
    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    public Building getBuilding() {
        return building;
    }

    /**
     * @deprecated
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("campusCode", this.campusCode);
        m.put("buildingCode", this.buildingCode);
        m.put("buildingOverrideCode", this.buildingOverrideCode);
        return m;
    }
}
