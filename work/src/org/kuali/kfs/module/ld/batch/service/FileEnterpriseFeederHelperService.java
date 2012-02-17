/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.batch.service;

import java.io.File;
import java.io.PrintStream;

import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.service.impl.EnterpriseFeederStatusAndErrorMessagesWrapper;
import org.kuali.kfs.module.ld.report.EnterpriseFeederReportData;
import org.kuali.kfs.sys.service.ReportWriterService;

/**
 * Implementations of this interface are responsible for reconciliation of
 * origin entry data in a file and loading them into the origin entry table.
 * Note that implementations of this class may have special useful transactional
 * properties. See implementation description for more details.
 */
public interface FileEnterpriseFeederHelperService {

	/**
	 * Reconciles and loads a file of origin entries into the origin entry
	 * table. This method DOES NOT handle the deletion of the done file
	 * 
	 * @param doneFile
	 *            the done file. Must exist and be non-empty
	 * @param dataFile
	 *            the data file. A connection to this file may be opened
	 *            multiple times by this method.
	 * @param reconFile
	 *            the reconciliation file. See implementations of
	 *            {@link org.kuali.kfs.gl.batch.service.ReconciliationParserService}
	 *            to determine the format of the data in a file.
	 * @param originEntryGroup
	 *            the group in which to place the origin entries
	 * @param feederProcessName
	 *            the name of the process that's invoking this method.
	 * @param reconciliationTableId
	 *            the name of the reconciliation block to use within the
	 *            reconciliation file
	 * @param statusAndErrors
	 *            a class with references to a
	 *            {@link org.kuali.kfs.gl.batch.service.impl.EnterpriseFeederStatus}
	 *            object and a list of error messages. Implementations of this
	 *            method may need to throw an exception to force a transaction
	 *            rollback, which means that it can't return a value. This
	 *            parameter allows the method to output status/error information
	 *   @param errorStatisticsReport
	 *           holds information about errors encountered while generating benefit entries
	 *   @param feederReportData
	 *           keeps track of statistic counts
	 */
	public void feedOnFile(File doneFile, File dataFile, File reconFile, PrintStream enterpriseFeedPs,
			String feederProcessName, String reconciliationTableId,
			EnterpriseFeederStatusAndErrorMessagesWrapper statusAndErrors, LedgerSummaryReport ledgerSummaryReport,
			ReportWriterService errorStatisticsReport, EnterpriseFeederReportData feederReportData);
}
