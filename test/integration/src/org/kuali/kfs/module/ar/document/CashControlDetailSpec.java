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
package org.kuali.kfs.module.ar.document;

import java.sql.Date;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class CashControlDetailSpec {
    
    static public CashControlDetailSpec specFor(KualiDecimal amount) {
        return new CashControlDetailSpec("ABB2","9999",new Date(System.currentTimeMillis()),amount);        
    }
    
    public CashControlDetailSpec() {}
    public CashControlDetailSpec(String customerNumber, String customerPaymentMediumIdentifier, Date customerPaymentDate, KualiDecimal financialDocumentLineAmount) {
        this.customerNumber = customerNumber;
        this.customerPaymentMediumIdentifier = customerPaymentMediumIdentifier;
        this.customerPaymentDate = customerPaymentDate;
        this.financialDocumentLineAmount = financialDocumentLineAmount;
    }
    
    public String customerNumber;
    public String customerPaymentMediumIdentifier;
    public Date customerPaymentDate; 
    public KualiDecimal financialDocumentLineAmount;
}
