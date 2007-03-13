/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.document;


/**
 * Accounts Payable Document Base
 * 
 */
public abstract class AccountsPayableDocumentBase extends PurchasingAccountsPayableDocumentBase implements AccountsPayableDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableDocumentBase.class);
    
    //  NOT PERSISTED IN DB
    private String vendorNumber;

    /**
     * Gets the vendorNumber attribute. 
     * @return Returns the vendorNumber.
     */
    public String getVendorNumber() {
        return vendorNumber;
    }

    /**
     * Sets the vendorNumber attribute value.
     * @param vendorNumber The vendorNumber to set.
     */
    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    } 

}
