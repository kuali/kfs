/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.service;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.DemergerReportData;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.SystemOptions;

/**
 * An interface of methods that allow all of the GL processes generate reports about their runs
 */

public interface ReportService {

    /**
     * Generates the GL Summary report
     * 
     * @param runDate the run date of the poster service that should be reported
     * @param options the options of the fiscal year the poster was run
     * @param reportType the type of the report that should be generated
     */
    public void generateGlSummary(Date runDate, SystemOptions year, String reportType);

    /**
     * Generates GL Encumbrance Summary report
     * 
     * @param runDate the run date of the poster service that should be reported
     * @param options the options of the fiscal year the poster was run
     * @param reportType the type of the report that should be generated
     */
    public void generateGlEncumbranceSummary(Date runDate, SystemOptions year, String reportType);
    
    /**
     * Generates the Balance Forward Year-End job Report
     * 
     * @param reportSummary a List of summarized statistics to report
     * @param runDate the date of the balance forward run
     * @param openAccountOriginEntryGroup the origin entry group with balance forwarding origin entries with open accounts
     * @param closedAccountOriginEntryGroup the origin entry group with balance forwarding origin entries with closed accounts
     */
    public void generateBalanceForwardStatisticsReport(List reportSummary, Date runDate, OriginEntryGroup openAccountOriginEntryGroup, OriginEntryGroup closedAccountOriginEntryGroup);

    /**
     * Generates the encumbrance foward year end job report
     * 
     * @param jobParameters the parameters that were used by the encumbrance forward job
     * @param reportSummary a List of summarized statistics to report
     * @param runDate the date of the encumbrance forward run
     * @param originEntryGroup the origin entry group that the job placed encumbrance forwarding origin entries into
     */
    public void generateEncumbranceClosingStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup originEntryGroup);

    /**
     * Generates the Nominal Activity Closing Report
     * 
     * @param jobParameters the parameters that were used by the nominal activity closing job
     * @param reportSummary a List of summarized statistics to report
     * @param runDate the date of the nominal activity closing job run
     * @param originEntryGroup the origin entry group that the job placed nominal activity closing origin entries into
     */
    public void generateNominalActivityClosingStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup originEntryGroup);

    /**
     * This method generates the statistics report of the organization reversion process.
     * 
     * @param jobParameters the parameters the org reversion process was run with
     * @param reportSummary a list of various counts the job went through
     * @param runDate the date the report was run
     * @param orgReversionOriginEntryGroup the origin entry group that contains the reversion origin entries
     */
    public void generateOrgReversionStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup orgReversionOriginEntryGroup);

}
