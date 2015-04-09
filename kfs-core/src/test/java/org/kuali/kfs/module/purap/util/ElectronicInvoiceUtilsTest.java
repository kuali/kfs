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

