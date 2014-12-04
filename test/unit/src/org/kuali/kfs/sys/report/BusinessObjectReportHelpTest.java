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
