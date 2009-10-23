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
package org.kuali.kfs.module.purap.util;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext(session = khuntley, shouldCommitTransactions=false)
public class ElectronicInvoiceUtilsTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceUtilsTest.class);
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testGetDate() throws Exception{
        //TestCase 1 - cdw.xml 
        String invoiceDate = "2008-08-11T00:00:00-06:00";
        Date formattedDate = ElectronicInvoiceUtils.getDate(invoiceDate);
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + formattedDate);
        assertEquals("2008-08-11", formattedDate.toString());
        
        //TestCase 2 - vwr.xml
        invoiceDate = "2008-07-29";
        formattedDate = ElectronicInvoiceUtils.getDate(invoiceDate);
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + formattedDate);
        assertEquals("2008-07-29", formattedDate.toString());
        
        //TestCase 3 - guybrown.xml
        invoiceDate = "2008-07-29T12:00:00";
        formattedDate = ElectronicInvoiceUtils.getDate(invoiceDate);
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + formattedDate);
        assertEquals("2008-07-29", formattedDate.toString());
        
        //TestCase 4 - barnesandnoble.xml
        invoiceDate = "2008-07-23T12:00:00-12:00";
        formattedDate = ElectronicInvoiceUtils.getDate(invoiceDate);
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + formattedDate);
        assertEquals("2008-07-23", formattedDate.toString());
        
        //TestCase 5 - For reject doc date (in kuali format)
        invoiceDate = "07/23/2008";
        formattedDate = ElectronicInvoiceUtils.getDate(invoiceDate);
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + formattedDate + "  (KualiFormat check) ");
        assertEquals("2008-07-23", formattedDate.toString());
        
        //TestCase 6 - For invalid format 1
        invoiceDate = "2008|07|23";
        formattedDate = ElectronicInvoiceUtils.getDate(invoiceDate);
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + formattedDate + "  (InvalidFormat check) ");
        assertNull(formattedDate);
        
        //TestCase 7 - For invalid format 2
        invoiceDate = null;
        formattedDate = ElectronicInvoiceUtils.getDate(invoiceDate);
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + formattedDate + "  (InvalidFormat check) ");
        assertNull(formattedDate);
        
        //Invoice Id Check
        String rawInvoiceId = "A1!B2#C3$D4%";
        System.out.println("Processed InvId " + ElectronicInvoiceUtils.stripSplChars(rawInvoiceId));
        
        
        BigDecimal d1 = new BigDecimal("0");
        BigDecimal d2 = new BigDecimal("-50");
        
        System.out.println(d2.compareTo(d1) < 0);
        if (d2.compareTo(d1) < 0) {
            System.out.println("D2 greater");
        }
        else {
            System.out.println("D1 greater");
        }

    }
    
}

