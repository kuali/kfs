/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.report;

import java.util.ArrayList;
import java.util.List;

public class ReportHeaderTest {

    /**
     * @see junit.framework.TestCase#setUp()
     */
//    @Override
//    protected void setUp() throws Exception 
//    {
//        super.setUp();
//
//        // Initialize service objects.
//        //accountCreationService = SpringContext.getBean(AccountCreationService.class);
//    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
//    @Override
//    protected void tearDown() throws Exception 
//    {
//        super.tearDown();
//    }
    
    public static void main(String[] args) {
        
        List<String> me = new ArrayList<String>();
        List<String> a = new ArrayList<String>();
        a.add("A");
        a.add("B");
        List<String> b = new ArrayList<String>();
        b.add("C");
        b.add("B");
        
        me.addAll(a);
        me.addAll(b);
        
        for (String s : me) {
            System.out.println(s);
        }
        
//        BigDecimal a = BigDecimal.ZERO;
//        
//        a = a.add(new BigDecimal(100));
//        a = a.add(new BigDecimal(100));
//        
//        System.out.println(a.intValue());
        
//        OutputStream file = null;
//        Document document = new Document();
//        document.setPageSize(PageSize.LETTER);
//        document.addTitle("Hello World PDF"); 
//
//        try {
//            file = new FileOutputStream(new File("c:\\temp\\test.pdf"));
//            PdfWriter.getInstance(document, file);
//            document.open();
//            
////            HeaderFooter header = new HeaderFooter(new Phrase(new Date().toString(), null), true);
////            document.setHeader(header);
//
//            Phrase header = new Paragraph(new Date().toString());
//            document.add(header);
//            
//            Paragraph title = new Paragraph("KEMID Trial Balance");
//            title.setAlignment(Element.ALIGN_CENTER);
//            title.add("\nAs of <Date>\n\n\n");
//            document.add(title);
//                       
//            PdfPTable table = new PdfPTable(7);
//            // titles
//            table.addCell("KEMID");
//            table.addCell("KEMID Name");
//            table.addCell("Income Cash Balance");
//            table.addCell("Principal\nCash\nBalance");
//            table.addCell("KEMID Total\nMarket Value");
//            table.addCell("Available\nExpendable\nFunds");
//            table.addCell("FY Remainder\nEstimated\nIncome");
//            //body
//            table.addCell("999999999");
//            table.addCell("KEMID test");
//            table.addCell("2000.00");
//            table.addCell("2300.00");
//            table.addCell("54,000.00");
//            table.addCell("4,000");
//            table.addCell("340.00");
//            //totals
//            table.addCell("TOTALS");
//            table.addCell("");
//            table.addCell("SUM2");
//            table.addCell("SUM4");
//            table.addCell("SUM5");
//            table.addCell("SUM6");
//            table.addCell("SUM7");
//            document.add(table);
//            
//        } catch (Exception e) {
//            System.out.println("Error: " + e.getMessage());
//        } finally {
//            if (file != null) {
//                try {
//                    document.close();
//                    file.close();
//                } catch (Exception e) {}
//                
//            }
//        }
//        
//        System.out.println("Done");
    }
}
