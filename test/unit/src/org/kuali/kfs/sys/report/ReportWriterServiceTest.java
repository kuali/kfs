/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.GlSummary;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.impl.ReportWriterTextServiceImpl;

@ConfigureContext
public class ReportWriterServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportWriterServiceTest.class);
    
    private ReportWriterService tableReportWriterService;
    private ReportWriterService colspanTableReportWriterService;
    Map<String, ReportWriterService> businessObjectReportHelperBeans;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectReportHelperBeans = SpringContext.getBeansOfType(ReportWriterService.class);
    }
    
    public void testWriteTable() throws Exception{        
        tableReportWriterService = businessObjectReportHelperBeans.get("tableReportWriterService");
        ((ReportWriterTextServiceImpl) tableReportWriterService).initialize();
        
        List<GlSummary> summaryList = this.getTestData(40);
        tableReportWriterService.writeTable(summaryList, true, false);
    }
    
    public void testWriteRowWithColspan() throws Exception{
        colspanTableReportWriterService = businessObjectReportHelperBeans.get("colspanTableReportWriterService");
        ((ReportWriterTextServiceImpl) colspanTableReportWriterService).initialize();
        
        List<GlSummary> summaryList = this.getTestData(20);
        colspanTableReportWriterService.writeTableHeader(summaryList.get(0));
        
        int index = 1;
        for(GlSummary summary : summaryList) {
            colspanTableReportWriterService.writeTableRow(summary);
            
            if(index % 5 == 0) {
                GlSummary subTotal = new GlSummary();
                subTotal.setFundGroup("Sub Totals (AC):");
                
                colspanTableReportWriterService.writeTableRowWithColspan(subTotal);                
                colspanTableReportWriterService.writeTableRowSeparationLine(summary);
            }
            
            index++;
        }
        
        GlSummary grandTotal = new GlSummary();
        grandTotal.setFundGroup("Grand Totals (AC):");
        colspanTableReportWriterService.writeTableRowWithColspan(grandTotal);
    }

    private List<GlSummary> getTestData(int countOfData) {
        List<GlSummary> summaryList = new ArrayList<GlSummary>();
        for(int i = 0; i < countOfData; i++) {
            GlSummary summary = new GlSummary();
            summary.setFundGroup("G" + i);
            
            summaryList.add(summary);
        }
        
        return summaryList;
    }
}
