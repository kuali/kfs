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
package org.kuali.kfs.module.cam.document.web.struts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * 
 * Action form for the asset barcode inventory error document
 */
public class BarcodeInventoryErrorForm extends FinancialSystemTransactionalDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorForm.class);
    
    private int[] rowCheckbox; 
    private boolean selectAllCheckbox;
    private HashMap barcodeInventoryStatuses; 
    
    //global replace - search fields
    //*** Old values **************
    private String currentTagNumber;
    private String currentScanCode;
    private String currentCampusCode;
    private String currentBuildingNumber;
    private String currentRoom;
    private String currentSubroom;
    private String currentConditionCode;

    //*** New values **************
    private String newTagNumber;
    private String newScanCode;
    private String newCampusCode;
    private String newBuildingNumber;
    private String newRoom;
    private String newSubroom;
    private String newConditionCode;
    
    
    /**
     * 
     * Constructs a BarcodeInventoryErrorForm.java.
     */
    public BarcodeInventoryErrorForm() {
        super();
        setDocument(new BarcodeInventoryErrorDocument());
        
        Map<String, String> editModeMap = new HashMap<String, String>();
        editModeMap.put(AuthorizationConstants.EditMode.FULL_ENTRY, "TRUE");
        setEditingMode(editModeMap);
    }


/*    public void populate(HttpServletRequest request) {
       LOG.info("****************Request Parameters*************");
        Enumeration paramNames;
        paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = (String) paramNames.nextElement();            
            String[] values = request.getParameterValues(name);
            
            for (int x=0;x < values.length;x++)
                LOG.info("******Request Parameters: "+name +"["+x+"]: "+values[x]);
                        
        }
        LOG.info("**********************************************");                
        
        
        super.populate(request);
    }*/

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        //DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(this.getBarcodeInventoryErrorDocument());
    }
    
    /**
     *  @returns BCIE document 
     */
    public BarcodeInventoryErrorDocument getBarcodeInventoryErrorDocument() {
        return (BarcodeInventoryErrorDocument) getDocument();
    }
    
    /**
     * Get rowCheckbox
     * @return String
     */
    public int[] getRowCheckbox() {
        return rowCheckbox;
    }

    /**
     * Set rowCheckbox
     * @param <code>String</code>
     */
    public void setRowCheckbox(int r[]) {
        this.rowCheckbox = r;
    }


    public boolean isSelectAllCheckbox() {
        return selectAllCheckbox;
    }


    public void setSelectAllCheckbox(boolean selectAllCheckbox) {
        this.selectAllCheckbox = selectAllCheckbox;
    }


    public String getCurrentTagNumber() {
        return currentTagNumber;
    }


    public void setCurrentTagNumber(String currentTagNumber) {
        this.currentTagNumber = (currentTagNumber == null ? "" : currentTagNumber.toUpperCase());
    }


    public String getCurrentScanCode() {
        return currentScanCode;
    }


    public void setCurrentScanCode(String currentScanCode) {
        this.currentScanCode = (currentScanCode == null ? "" : currentScanCode.toUpperCase());
    }


    public String getCurrentCampusCode() {
        return currentCampusCode;
    }


    public void setCurrentCampusCode(String currentCampusCode) {
        this.currentCampusCode = (currentCampusCode == null ? "" : currentCampusCode.toUpperCase());
    }


    public String getCurrentBuildingNumber() {
        return currentBuildingNumber;
    }


    public void setCurrentBuildingNumber(String currentBuildingNumber) {
        this.currentBuildingNumber = (currentBuildingNumber == null ? "" : currentBuildingNumber.toUpperCase());
    }


    public String getCurrentRoom() {
        return currentRoom;
    }


    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = (currentRoom==null ? "" : currentRoom.toUpperCase());
    }


    public String getCurrentSubroom() {
        return currentSubroom;
    }


    public void setCurrentSubroom(String currentSubroom) {
        this.currentSubroom = (currentSubroom == null ? "" : currentSubroom.toUpperCase());
    }


    public String getCurrentConditionCode() {
        return currentConditionCode;
    }


    public void setCurrentConditionCode(String currentConditionCode) {
        this.currentConditionCode = (currentConditionCode == null ? "" : currentConditionCode.toUpperCase());
    }


    public String getNewTagNumber() {
        return newTagNumber;
    }


    public void setNewTagNumber(String newTagNumber) {
        this.newTagNumber = (newTagNumber == null ? "" : newTagNumber.toUpperCase());
    }


    public String getNewScanCode() {
        return newScanCode;
    }


    public void setNewScanCode(String newScanCode) {
        this.newScanCode = (newScanCode == null ? "" : newScanCode.toUpperCase());
    }


    public String getNewCampusCode() {
        return newCampusCode;
    }


    public void setNewCampusCode(String newCampusCode) {
        this.newCampusCode = ( newCampusCode == null ? "" : newCampusCode.toUpperCase());
    }


    public String getNewBuildingNumber() {
        return newBuildingNumber;
    }


    public void setNewBuildingNumber(String newBuildingNumber) {
        this.newBuildingNumber = (newBuildingNumber == null ? "" : newBuildingNumber.toUpperCase());
    }


    public String getNewRoom() {
        return newRoom;
    }


    public void setNewRoom(String newRoom) {
        this.newRoom = (newRoom == null ? "" : newRoom.toUpperCase());
    }


    public String getNewSubroom() {
        return newSubroom;
    }


    public void setNewSubroom(String newSubroom) {
        this.newSubroom = ( newSubroom == null ? "" : newSubroom.toUpperCase());
    }


    public String getNewConditionCode() {
        return newConditionCode;
    }


    public void setNewConditionCode(String newConditionCode) {
        this.newConditionCode = (newConditionCode == null ? "" : newConditionCode.toUpperCase());
    }

    
    /**
     * 
     * Sets the global search fields with empty string
     */
    public void resetSearchFields() {
        currentTagNumber="";
        currentScanCode="";
        currentCampusCode="";
        currentBuildingNumber="";
        currentRoom="";
        currentSubroom="";
        currentConditionCode="";
        newTagNumber="";
        newScanCode="";
        newCampusCode="";
        newBuildingNumber="";
        newRoom="";
        newSubroom="";
        newConditionCode="";        
    }
    
    
    /**
     * 
     * Reset the BCIE document checkboxes. 
     */
    public void resetCheckBoxes() {
        if (rowCheckbox == null)
            return;
            
        this.rowCheckbox = new int[rowCheckbox.length];
        this.selectAllCheckbox = false;
    }
    
    
}
