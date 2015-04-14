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
package org.kuali.kfs.module.purap.util;

import java.sql.Date;

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.validation.impl.PaymentRequestPayDateNotPastValidation;
import org.kuali.kfs.module.purap.fixture.PaymentRequestInvoiceTabFixture;

public class TestPaymentRequestPayDateNotPastValidation extends PaymentRequestPayDateNotPastValidation {

    private Date testDate;
    
    @Override
    protected PaymentRequestDocument retrievePaymentRequestDocumentFromDatabase(PaymentRequestDocument document) {
        PaymentRequestDocument temp = new PaymentRequestDocument();
        PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(temp);
        temp.setPaymentRequestPayDate(testDate);
        return temp;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }            

}
