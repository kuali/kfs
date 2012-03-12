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
package org.kuali.kfs.module.purap.service;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignment;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignmentDetail;

public interface SensitiveDataService {
        
    public List<SensitiveData> getSensitiveDatasAssignedByRelatedDocId(Integer accountsPayablePurchasingDocumentLinkIdentifier);
    
    /**
     * Returns a sensitive data record associated with the given code.
     * @param sensitiveDataCode the code of the sensitive data
     * @return the sensitive data object with the give code
     */
    public SensitiveData getSensitiveDataByCode(String sensitiveDataCode);

    /**
     * Returns all sensitive data records stored in the business object table.
     * @return a list of all sensitive data objects
     */
    public List<SensitiveData> getAllSensitiveDatas();
    
    /**
     * Gets all sensitive data entries assigned to the specified purchase order.
     * @param poId the ID of the specified purchase order
     * @return a list of sensitive data entries assigned to the PO
     */
    public List<SensitiveData> getSensitiveDatasAssignedByPoId(Integer poId);
    
    /**
     * Gets all sensitive data entries assigned to the specified purchase order.
     * @param reqId the ID of the requisition that's associated with the purchase order
     * @return a list of sensitive data entries assigned to the PO
     */
    public List<SensitiveData> getSensitiveDatasAssignedByReqId(Integer reqId);
    
    /**
     * Deletes all sensitive data entries assigned to the specified purchase order.
     * @param poId the ID of the PO
     */
    public void deletePurchaseOrderSensitiveDatas(Integer poId);

    /**
     * Saves the specified list of PurchaseOrderSensitiveData objects into the database.
     * @param posds the PurchaseOrderSensitiveData objects to be saved
     */
    public void savePurchaseOrderSensitiveDatas(List<PurchaseOrderSensitiveData> posds);
    
    /**
     * Gets the latest sensitive data assignment for the specified purchase order.
     * @param poId the ID of the specified PO
     * @return the latest sensitive data assignment for the PO
     */
    public SensitiveDataAssignment getLastSensitiveDataAssignment(Integer poId);

    /**
     * Gets the latest sensitive data assignment ID for the specified purchase order.
     * @param poId the ID of the specified PO
     * @return the latest sensitive data assignment ID for the PO
     */
    public Integer getLastSensitiveDataAssignmentId(Integer poId);

    /**
     * Saves the specified SensitiveDataAssignment object into the database.
     * @param sda the SensitiveDataAssignment object to be saved
     */
    public void saveSensitiveDataAssignment(SensitiveDataAssignment sda);

    /**
     * Gets the latest sensitive data assignment details for the specified purchase order.
     * @param poId the ID of the specified PO
     * @return the latest sensitive data assignment details for the PO
     */
    public List<SensitiveDataAssignmentDetail> getLastSensitiveDataAssignmentDetails(Integer poId);
    
    /**
     * Saves the specified list of SensitiveDataAssignmentDetail objects into the database.
     * @param sdads the SensitiveDataAssignmentDetail objects to be saved
     */
    public void saveSensitiveDataAssignmentDetails(List<SensitiveDataAssignmentDetail> sdads);    

    //public void savePurchaseOrderSensitiveData(String poId, String reqId, String sensitiveDataCode);   
    //public void savePurchaseOrderSensitiveData(PurchaseOrderSensitiveData posd);
    //public void saveSensitiveDataAssignment(String sdaId, String poId, String userId, Date date);
    //public void saveSensitiveDataAssignmentDetail(String sdaId, String sensitiveDataCode);
    //public void saveSensitiveDataAssignmentDetail(SensitiveDataAssignmentDetail sdad);
        
}
