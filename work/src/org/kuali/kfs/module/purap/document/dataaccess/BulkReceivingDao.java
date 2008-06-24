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
package org.kuali.kfs.module.purap.document.dataaccess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.dataaccess.impl.ReceivingDaoOjb;

public interface BulkReceivingDao {

    public List<String> getDocumentNumbersByPurchaseOrderId(Integer id);
    
    public List<String> duplicateVendorDate(Integer poId, java.sql.Date vendorDate);
    
    public List<String> duplicatePackingSlipNumber(Integer poId, String packingSlipNumber);
    
    public List<String> duplicateBillOfLadingNumber(Integer poId, String billOfLadingNumber);
    
}
