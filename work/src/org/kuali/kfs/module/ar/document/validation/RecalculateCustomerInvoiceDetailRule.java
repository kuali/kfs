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
package org.kuali.kfs.module.ar.document.validation;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.sys.document.AccountingDocument;

public interface RecalculateCustomerInvoiceDetailRule<F extends AccountingDocument> extends CustomerInvoiceDetailRule {

    /**
     * Returns true if business rules for recalculating a customer invoice detail return true
     * 
     * @param check
     * @param financialDocument
     * @return true if the business rules pass
     */
    public boolean processRecalculateCustomerInvoiceDetailRules(F financialDocument, CustomerInvoiceDetail customerInvoiceDetail);    
}
