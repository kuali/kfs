/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
