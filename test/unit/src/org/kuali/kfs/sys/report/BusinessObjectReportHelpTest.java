/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.report;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class BusinessObjectReportHelpTest extends KualiTestBase {

    private static final Logger LOG = Logger.getLogger(BusinessObjectReportHelpTest.class);
    
    private BusinessObjectReportHelper summaryReportHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        summaryReportHelper = SpringContext.getBean(BusinessObjectReportHelper.class,"summaryReportHelperForTesting");
    }

    public void testGetTableDefintion() throws Exception {
        Map<String, String> tableDefintion = summaryReportHelper.getTableDefinition();

        LOG.info(tableDefintion.get(KFSConstants.ReportConstants.TABLE_HEADER_LINE_KEY));
        LOG.info(tableDefintion.get(KFSConstants.ReportConstants.SEPARATOR_LINE_KEY));
        LOG.info(tableDefintion.get(KFSConstants.ReportConstants.TABLE_CELL_FORMAT_KEY));
    }
    
    public void testApplyColumnSpanOnCellWidth() throws Exception {        
        List<Integer> cellWidthList = summaryReportHelper.getTableCellWidth();
        
        LOG.info("===before: " + cellWidthList);        
        summaryReportHelper.applyColspanOnCellWidth(cellWidthList);        
        LOG.info("===after : " + cellWidthList);
    }
    
    public void testGetTableCellFormat() throws Exception {        
        String tabelCellFormatWithoutColspan = summaryReportHelper.getTableCellFormat(false, true, null);
        LOG.info("===Without Colspan: " + tabelCellFormatWithoutColspan);
        
        String tabelCellFormatWithColspan = summaryReportHelper.getTableCellFormat(true, true, null);
        LOG.info("===With Colspan:    " + tabelCellFormatWithColspan);
    }
}
