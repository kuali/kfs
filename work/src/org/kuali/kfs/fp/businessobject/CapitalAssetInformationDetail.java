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
package org.kuali.kfs.fp.businessobject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;

public class CapitalAssetInformationDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer itemLineNumber;
    private String campusCode;
    private String buildingCode;
    private String buildingRoomNumber;
    private String buildingSubRoomNumber;
    private String capitalAssetTagNumber;
    private String capitalAssetSerialNumber;

    //new properties...
    private Integer sequenceNumber; // relative to the grouping of accounting lines
    private String financialDocumentLineTypeCode;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private Integer capitalAssetLineNumber;
    
    private Campus campus;
    private Building building;
    private Room room;
    private CapitalAssetInformation capitalAssetInformation;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put(KFSPropertyConstants.ITEM_LINE_NUMBER, this.itemLineNumber);
        m.put(KFSPropertyConstants.SEQUENCE_NUMBER, this.getSequenceNumber());
        m.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_TYPE_CODE, this.getFinancialDocumentLineTypeCode());
        m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.getChartOfAccountsCode());
        m.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.getAccountNumber());
        m.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.getFinancialObjectCode());
        
        return m;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the itemLineNumber attribute.
     * 
     * @return Returns the itemLineNumber.
     */
    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    /**
     * Sets the itemLineNumber attribute value.
     * 
     * @param itemLineNumber The itemLineNumber to set.
     */
    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode.
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute value.
     * 
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
     * Gets the buildingCode attribute.
     * 
     * @return Returns the buildingCode.
     */
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Sets the buildingCode attribute value.
     * 
     * @param buildingCode The buildingCode to set.
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    /**
     * Gets the buildingRoomNumber attribute.
     * 
     * @return Returns the buildingRoomNumber.
     */
    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }

    /**
     * Sets the buildingRoomNumber attribute value.
     * 
     * @param buildingRoomNumber The buildingRoomNumber to set.
     */
    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }

    /**
     * Gets the capitalAssetTagNumber attribute.
     * 
     * @return Returns the capitalAssetTagNumber.
     */
    public String getCapitalAssetTagNumber() {
        return capitalAssetTagNumber;
    }

    /**
     * Sets the capitalAssetTagNumber attribute value.
     * 
     * @param capitalAssetTagNumber The capitalAssetTagNumber to set.
     */
    public void setCapitalAssetTagNumber(String capitalAssetTagNumber) {
        this.capitalAssetTagNumber = capitalAssetTagNumber;
    }

    /**
     * Gets the capitalAssetSerialNumber attribute.
     * 
     * @return Returns the capitalAssetSerialNumber.
     */
    public String getCapitalAssetSerialNumber() {
        return capitalAssetSerialNumber;
    }

    /**
     * Sets the capitalAssetSerialNumber attribute value.
     * 
     * @param capitalAssetSerialNumber The capitalAssetSerialNumber to set.
     */
    public void setCapitalAssetSerialNumber(String capitalAssetSerialNumber) {
        this.capitalAssetSerialNumber = capitalAssetSerialNumber;
    }

    /**
     * Gets the campus attribute.
     * 
     * @return Returns the campus.
     */
    public Campus getCampus() {
        return campus = StringUtils.isBlank( campusCode)?null:((campus!=null && campus.getCode().equals( campusCode))?campus:SpringContext.getBean(CampusService.class).getCampus( campusCode));
    }

    /**
     * Sets the campus attribute value.
     * 
     * @param campus The campus to set.
     */
    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    /**
     * Gets the building attribute.
     * 
     * @return Returns the building.
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Sets the building attribute value.
     * 
     * @param building The building to set.
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * Gets the room attribute.
     * 
     * @return Returns the room.
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Sets the room attribute value.
     * 
     * @param room The room to set.
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Gets the capitalAssetInformation attribute.
     * 
     * @return Returns the capitalAssetInformation.
     */
    public CapitalAssetInformation getCapitalAssetInformation() {
        return capitalAssetInformation;
    }

    /**
     * Sets the capitalAssetInformation attribute value.
     * 
     * @param capitalAssetInformation The capitalAssetInformation to set.
     */
    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }

    /**
     * Gets the buildingSubRoomNumber attribute. 
     * @return Returns the buildingSubRoomNumber.
     */
    public String getBuildingSubRoomNumber() {
        return buildingSubRoomNumber;
    }

    /**
     * Sets the buildingSubRoomNumber attribute value.
     * @param buildingSubRoomNumber The buildingSubRoomNumber to set.
     */
    public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
        this.buildingSubRoomNumber = buildingSubRoomNumber;
    }
    
    /**
     * Gets the sequenceNumber attribute.
     * 
     * @return Returns the sequenceNumber
     */
    
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /** 
     * Sets the sequenceNumber attribute.
     * 
     * @param sequenceNumber The sequenceNumber to set.
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Gets the financialDocumentLineTypeCode attribute.
     * 
     * @return Returns the financialDocumentLineTypeCode
     */
    
    public String getFinancialDocumentLineTypeCode() {
        return financialDocumentLineTypeCode;
    }

    /** 
     * Sets the financialDocumentLineTypeCode attribute.
     * 
     * @param financialDocumentLineTypeCode The financialDocumentLineTypeCode to set.
     */
    public void setFinancialDocumentLineTypeCode(String financialDocumentLineTypeCode) {
        this.financialDocumentLineTypeCode = financialDocumentLineTypeCode;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /** 
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    
    public String getAccountNumber() {
        return accountNumber;
    }

    /** 
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     */
    
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /** 
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }
    
    /**
     * Gets the capitalAssetLineNumber attribute. 
     * @return Returns the capitalAssetLineNumber.
     */
    public Integer getCapitalAssetLineNumber() {
        return capitalAssetLineNumber;
    }

    /**
     * Sets the capitalAssetLineNumber attribute value.
     * @param capitalAssetLineNumber The capitalAssetLineNumber to set.
     */
    public void setCapitalAssetLineNumber(Integer capitalAssetLineNumber) {
        this.capitalAssetLineNumber = capitalAssetLineNumber;
    }
    
    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     * 
     * @return Map a map with the primitive field names as the key and the primitive values as the map value.
     */
    public Map<String, Object> getValuesMap() {
        Map<String, Object> simpleValues = new HashMap<String, Object>();

        simpleValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        simpleValues.put(KFSPropertyConstants.ITEM_LINE_NUMBER, this.getItemLineNumber());
        simpleValues.put(KFSPropertyConstants.SEQUENCE_NUMBER, this.getSequenceNumber());
        simpleValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_TYPE_CODE, this.getFinancialDocumentLineTypeCode());
        simpleValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.getChartOfAccountsCode());
        simpleValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.getAccountNumber());
        simpleValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.getFinancialObjectCode());
        simpleValues.put(KFSPropertyConstants.CAMPUS_CODE, this.getCampusCode());
        simpleValues.put(KFSPropertyConstants.BUILDING_CODE, this.getBuildingCode());
        simpleValues.put(KFSPropertyConstants.BUILDING_ROOM_NUMBER, this.getBuildingRoomNumber());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_LINE_NUMBER, this.getCapitalAssetLineNumber());

        return simpleValues;
    }
}
