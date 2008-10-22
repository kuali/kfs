/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.service;

import java.util.List;

import org.kuali.kfs.integration.purap.PurchasingAccountsPayableSensitiveData;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignment;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignmentDetail;

public interface SensitiveDataService {
        
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
    
    public List<SensitiveData> getSensitiveDatasAssignedByPoId(Integer poId);
    
    public List<SensitiveData> getSensitiveDatasAssignedByReqId(Integer reqId);
    
    public void deletePurchaseOrderSensitiveDatas(Integer poId);

    public void savePurchaseOrderSensitiveDatas(List<PurchaseOrderSensitiveData> posds);
    
    public SensitiveDataAssignment getLastSensitiveDataAssignment(Integer poId);

    public Integer getLastSensitiveDataAssignmentId(Integer poId);

    public void saveSensitiveDataAssignment(SensitiveDataAssignment sda);

    public void saveSensitiveDataAssignmentDetails(List<SensitiveDataAssignmentDetail> sdads);    

    //public void savePurchaseOrderSensitiveData(String poId, String reqId, String sensitiveDataCode);   
    //public void savePurchaseOrderSensitiveData(PurchaseOrderSensitiveData posd);
    //public void saveSensitiveDataAssignment(String sdaId, String poId, String userId, Date date);
    //public void saveSensitiveDataAssignmentDetail(String sdaId, String sensitiveDataCode);
    //public void saveSensitiveDataAssignmentDetail(SensitiveDataAssignmentDetail sdad);
        
}
