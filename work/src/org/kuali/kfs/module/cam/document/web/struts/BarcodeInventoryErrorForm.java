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

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.FormatException;
import org.kuali.core.web.format.Formatter;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;

public class BarcodeInventoryErrorForm extends FinancialSystemTransactionalDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorForm.class);
    private int[] rowCheckbox; 
    private boolean selectAllCheckbox;
    
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
    
    
    public BarcodeInventoryErrorForm() {
        super();
        setDocument(new BarcodeInventoryErrorDocument());
        
        Map<String, String> editModeMap = new HashMap<String, String>();
        editModeMap.put(AuthorizationConstants.EditMode.FULL_ENTRY, "TRUE");
        setEditingMode(editModeMap);
    }


    public void populate(HttpServletRequest request) {
//        LOG.info("****************Request Parameters*************");
//        Enumeration paramNames;
//        paramNames = request.getParameterNames();
//        while (paramNames.hasMoreElements()) {
//            String name = (String) paramNames.nextElement();            
//            String[] values = request.getParameterValues(name);
//            
//            for (int x=0;x < values.length;x++)
//                LOG.info("******Request Parameters: "+name +"["+x+"]: "+values[x]);
//                        
//        }
//        LOG.info("**********************************************");                    
        
        
        super.populate(request);

//        BarcodeInventoryErrorDocument document = getBarcodeInventoryErrorDocument();
//        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail(); 
//
//        for(BarcodeInventoryErrorDetail detail : barcodeInventoryErrorDetails) {
//                LOG.info("*******AFTER TIMESTAMP!!! :"+detail.getUploadScanTimestamp());
//        }
    }
    
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
        this.currentTagNumber = currentTagNumber;
    }


    public String getCurrentScanCode() {
        return currentScanCode;
    }


    public void setCurrentScanCode(String currentScanCode) {
        this.currentScanCode = currentScanCode;
    }


    public String getCurrentCampusCode() {
        return currentCampusCode;
    }


    public void setCurrentCampusCode(String currentCampusCode) {
        this.currentCampusCode = currentCampusCode;
    }


    public String getCurrentBuildingNumber() {
        return currentBuildingNumber;
    }


    public void setCurrentBuildingNumber(String currentBuildingNumber) {
        this.currentBuildingNumber = currentBuildingNumber;
    }


    public String getCurrentRoom() {
        return currentRoom;
    }


    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }


    public String getCurrentSubroom() {
        return currentSubroom;
    }


    public void setCurrentSubroom(String currentSubroom) {
        this.currentSubroom = currentSubroom;
    }


    public String getCurrentConditionCode() {
        return currentConditionCode;
    }


    public void setCurrentConditionCode(String currentConditionCode) {
        this.currentConditionCode = currentConditionCode;
    }


    public String getNewTagNumber() {
        return newTagNumber;
    }


    public void setNewTagNumber(String newTagNumber) {
        this.newTagNumber = newTagNumber;
    }


    public String getNewScanCode() {
        return newScanCode;
    }


    public void setNewScanCode(String newScanCode) {
        this.newScanCode = newScanCode;
    }


    public String getNewCampusCode() {
        return newCampusCode;
    }


    public void setNewCampusCode(String newCampusCode) {
        this.newCampusCode = newCampusCode;
    }


    public String getNewBuildingNumber() {
        return newBuildingNumber;
    }


    public void setNewBuildingNumber(String newBuildingNumber) {
        this.newBuildingNumber = newBuildingNumber;
    }


    public String getNewRoom() {
        return newRoom;
    }


    public void setNewRoom(String newRoom) {
        this.newRoom = newRoom;
    }


    public String getNewSubroom() {
        return newSubroom;
    }


    public void setNewSubroom(String newSubroom) {
        this.newSubroom = newSubroom;
    }


    public String getNewConditionCode() {
        return newConditionCode;
    }


    public void setNewConditionCode(String newConditionCode) {
        this.newConditionCode = newConditionCode;
    }


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
    
    public void resetCheckBoxes() {
        this.rowCheckbox = new int[rowCheckbox.length];
        this.selectAllCheckbox = false;
    }
    
    
}
