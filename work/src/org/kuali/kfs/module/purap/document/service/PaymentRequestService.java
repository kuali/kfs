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
package org.kuali.module.purap.service;

import java.util.List;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.NoteService;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.module.purap.dao.PaymentRequestDao;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.vendor.service.VendorService;

public interface PaymentRequestService {

    public void setBusinessObjectService(BusinessObjectService boService);

    public void setDateTimeService(DateTimeService dateTimeService) ;
   

    public void setDocumentService(DocumentService documentService) ;
    
    public void setNoteService(NoteService noteService);
    

    public void setGeneralLedgerService(GeneralLedgerService generalLedgerService) ;
    
    public void setPurapService(PurapService purapService) ;

    public void setPaymentRequestDao(PaymentRequestDao paymentRequestDao) ;

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) ;
    
    public void setVendorService(VendorService vendorService) ;

    public void save(PaymentRequestDocument paymentRequestDocument);
    
    public List<PaymentRequestDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId);
}
