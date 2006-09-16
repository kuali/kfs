/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.batch;

import java.util.Collection;
import java.sql.Date;

import org.kuali.core.batch.Step;
import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.NightlyOutService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.ReportService;

/**
 * @author Bin Gao from Michigan State University
 */
public class NightlyOutStep implements Step {

    private NightlyOutService nightlyOutService;
    private ReportService reportService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.core.batch.Step#performStep()
     */
    public boolean performStep() {
        nightlyOutService.copyApprovedPendingLedgerEntries();
        reportService.generatePendingEntryReport();
        
        Date runDate = new java.sql.Date(dateTimeService.getCurrentDate().getTime());
        Collection groups = originEntryGroupService.getGroupsFromSourceForDate(OriginEntrySource.GENERATE_BY_EDOC, runDate);
        reportService.generateLedgerSummaryReport(runDate, groups);
        
        return true;
    }

    /**
     * @see org.kuali.core.batch.Step#getName()
     */
    public String getName() {
        return "Nighly Out Job";
    }

    /**
     * Sets the nightlyOutService attribute value.
     * 
     * @param nightlyOutService The nightlyOutService to set.
     */
    public void setNightlyOutService(NightlyOutService nightlyOutService) {
        this.nightlyOutService = nightlyOutService;
    }
    
    /**
     * Sets the reportService attribute value.
     * 
     * @param reportService
     */
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
    
    
}
