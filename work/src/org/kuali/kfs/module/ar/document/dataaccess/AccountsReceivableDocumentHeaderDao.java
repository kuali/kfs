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
package org.kuali.kfs.module.ar.document.dataaccess;

import java.util.Collection;

import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;

public interface AccountsReceivableDocumentHeaderDao {
    
    /**
     * This method retrieves all AccountReceivableDocumentHeader objects for the customerNumber
     * @return AccountReceivableDocumentHeader objects
     */
    public Collection getARDocumentHeadersByCustomerNumber(String customerNumber);
    
    /**
     * This method retrieves all AccountsReceivableDocumentHeader objects for the customerNumber, processingChartOfAccountCode, and processingOrganizationCode
     * @return AccountReceivableDocumentHeader objects
     */
    public Collection getARDocumentHeadersByCustomerNumberByProcessingOrgCodeAndChartCode(String customerNumber, String processingChartOfAccountCode, String processingOrganizationCode);
    
    /**
     * 
     * This method retrieves all AccountReceivableDocumentHeader objects for the customerNumber
     * @param customerNumber
     * @return
     */
    public Collection<AccountsReceivableDocumentHeader> getARDocumentHeadersIncludingHiddenApplicationByCustomerNumber(String customerNumber);
}
