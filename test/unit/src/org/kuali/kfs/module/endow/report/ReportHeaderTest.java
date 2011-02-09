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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@ConfigureContext(session = khuntley)
public class ReportHeaderTest extends KualiTestBase {

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception 
    {
        super.setUp();
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception 
    {
        super.tearDown();
    }
    
    public void testABC() {
        
    }
    
    /*
    public static void main(String[] args) {
               
        OutputStream file = null;
        Document document = new Document();
        document.setPageSize(PageSize.LETTER);
        document.addTitle("Hello World PDF"); 

        
        try {
            file = new FileOutputStream(new File("c:\\temp\\test.pdf"));
            PdfWriter.getInstance(document, file);
            document.open();
            
            Phrase header = new Paragraph(new Date().toString());
            Paragraph title = new Paragraph("University of Maryland\n");
            title.setAlignment(Element.ALIGN_CENTER);
            title.add("Report Request Header Sheet\n\n");
            document.add(title);

            // 
            PdfPTable requestTable = new PdfPTable(2);
            requestTable.setWidthPercentage(80);
                        
            Paragraph rePortRequested = new Paragraph("University of Maryland", FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph dateRequested = new Paragraph(new Date().toString(), FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph requestedBy = new Paragraph("khuntley", FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph endowmentOption = new Paragraph("Endowment", FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph reportOption = new Paragraph("Both", FontFactory.getFont(FontFactory.HELVETICA, 10));
            
            requestTable.addCell("Report Requested:");
            requestTable.addCell(rePortRequested);
            requestTable.addCell("Date Requested:");
            requestTable.addCell(dateRequested);
            requestTable.addCell("Reqeusted by:");
            requestTable.addCell(requestedBy);
            requestTable.addCell("Endowment Option:");
            requestTable.addCell(endowmentOption);
            requestTable.addCell("Report Option:");
            requestTable.addCell(reportOption);
            document.add(requestTable);
          
            Chunk chunk = new Chunk("");
            chunk.setNewPage();
            Paragraph newPage= new Paragraph (chunk);
            document.add(newPage);


            // Criteria
            Paragraph criteria = new Paragraph("\nCriteria:\n\n");
            document.add(criteria);
            
            PdfPTable criteriaTable = new PdfPTable(2);
            criteriaTable.setWidthPercentage(80);

            Paragraph benefittingCampus = new Paragraph("UA BL", FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph benefittingChart = new Paragraph("UA BL", FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph benefittingOrganization = new Paragraph("University of Maryland", FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph kemidTypeCode = new Paragraph("306 017 399", FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph kemidPurposeCode = new Paragraph("P12 P23", FontFactory.getFont(FontFactory.HELVETICA, 10));            
            Paragraph combinationGroupCode = new Paragraph("G1 G2 G3", FontFactory.getFont(FontFactory.HELVETICA, 10));
            
            criteriaTable.addCell("Benefitting Campus:");
            criteriaTable.addCell(benefittingCampus);
            criteriaTable.addCell("Benefitting Chart:");
            criteriaTable.addCell(benefittingChart);
            criteriaTable.addCell("Benefitting Organization:");
            criteriaTable.addCell(benefittingOrganization);
            criteriaTable.addCell("KEMID Type Code:");
            criteriaTable.addCell(kemidTypeCode);
            criteriaTable.addCell("KEMID Purpose Code:");
            criteriaTable.addCell(kemidPurposeCode);
            criteriaTable.addCell("Combine Group Code:");
            criteriaTable.addCell(combinationGroupCode);
            document.add(criteriaTable);
            
            // kemids with multiple benefitting organization
            Paragraph kemidWithMultipleBenefittingOrganization = new Paragraph("\nKEMDIs with Multiple Benefitting Organizations:\n\n");
            document.add(kemidWithMultipleBenefittingOrganization);
            
            PdfPTable kemidWithMultipleBenefittingOrganizationTable = new PdfPTable(5);
            kemidWithMultipleBenefittingOrganizationTable.setWidthPercentage(100);
            
            Paragraph kemid = new Paragraph("KEMID", FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph campus = new Paragraph("Campus", FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph chart = new Paragraph("Chart", FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph organization = new Paragraph("Organziation", FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph percent = new Paragraph("Percent", FontFactory.getFont(FontFactory.HELVETICA, 10));
            
            kemidWithMultipleBenefittingOrganizationTable.addCell(kemid);
            kemidWithMultipleBenefittingOrganizationTable.addCell("0123456789");
            kemidWithMultipleBenefittingOrganizationTable.addCell(campus);
            kemidWithMultipleBenefittingOrganizationTable.addCell("BL");
            kemidWithMultipleBenefittingOrganizationTable.addCell(chart);
            kemidWithMultipleBenefittingOrganizationTable.addCell("BL");
            kemidWithMultipleBenefittingOrganizationTable.addCell(organization);
            kemidWithMultipleBenefittingOrganizationTable.addCell("BL");
            kemidWithMultipleBenefittingOrganizationTable.addCell(percent);
            kemidWithMultipleBenefittingOrganizationTable.addCell("100");
            
            document.add(kemidWithMultipleBenefittingOrganizationTable);
            
            // kemids
            Paragraph kemidsSelected = new Paragraph("\nKEMDIs Selected:\n\n");
            document.add(kemidsSelected);
            
            List<String> kemids = new ArrayList<String>();
            kemids.add("0123456789");
            kemids.add("0123456789");
            kemids.add("0123456789");
            kemids.add("0123456789");
            kemids.add("0123456789");
            kemids.add("0123456789");
            kemids.add("0123456789");
            kemids.add("0123456789");
            kemids.add("0123456789");
            kemids.add("0123456789");
            kemids.add("0123456789");
            
            PdfPTable kemidsTable = new PdfPTable(5);
            kemidsTable.setWidthPercentage(100);
            
            for (String kemidSelected : kemids) {
                kemidsTable.addCell(new Paragraph(kemidSelected, FontFactory.getFont(FontFactory.HELVETICA, 10)) );                
            }
            document.add(kemidsTable);
                        
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (file != null) {
                try {
                    document.close();
                    file.close();
                } catch (Exception e) {}
                
            }
        }
        
        System.out.println("Done");
    }
    */
}
