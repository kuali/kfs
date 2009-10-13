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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

public interface AccountsReceivableTaxService {
        
    /**
     * This method returns true if customer invoice detail amount can be taxed.
     * 
     * @param customer
     * @param customerInvoiceDetail
     * @return
     */
    public boolean isCustomerInvoiceDetailTaxable( CustomerInvoiceDocument document, CustomerInvoiceDetail customerInvoiceDetail );
    
    /**
     * This method returns the appropriate postal code for taxation
     * @param document
     * @return
     */
    public String getPostalCodeForTaxation( CustomerInvoiceDocument document );
}
