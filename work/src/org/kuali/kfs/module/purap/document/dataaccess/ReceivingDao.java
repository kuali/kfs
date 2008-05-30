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
package org.kuali.module.purap.dao;

import java.util.List;


/**
 * Receiving Line DAO Interface.
 */
public interface ReceivingDao {

    public List<String> getDocumentNumbersByPurchaseOrderId(Integer id);

    public List<String> getReceivingCorrectionDocumentNumbersByPurchaseOrderId(Integer id);
    
    public List<String> getReceivingCorrectionDocumentNumbersByReceivingLineNumber(String receivingDocumentNumber);
    
    public List<String> duplicateVendorDate(Integer poId, java.sql.Date vendorDate);
    
    public List<String> duplicatePackingSlipNumber(Integer poId, String packingSlipNumber);
    
    public List<String> duplicateBillOfLadingNumber(Integer poId, String billOfLadingNumber);
}
