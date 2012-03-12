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

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReasonType;
import org.kuali.kfs.module.purap.service.impl.ElectronicInvoiceOrderHolder;

public interface ElectronicInvoiceMatchingService {
    
    public ElectronicInvoiceRejectReasonType getElectronicInvoiceRejectReasonType(String rejectReasonTypeCode);
    
    public void doMatchingProcess(ElectronicInvoiceOrderHolder orderWrapper);
    
    public ElectronicInvoiceRejectReason createRejectReason(String rejectReasonTypeCode, String extraDescription, String fileName);
    
}
