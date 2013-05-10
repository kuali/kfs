/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.report;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Keeps track of statistics for the enterprise feeder job
 */
public class EnterpriseFeederReportData {
	private int numberOfRecordsRead = 0;
	private int numberOfBalanceTypeActualsRead = 0;
	private int numberOfBalanceTypeEncumbranceRead = 0;
	private int numberOfFringeActualsGenerated = 0;
	private int numberOfFringeEncumbrancesGenerated = 0;
	private int numberOfErrorEncountered = 0;

	private int numberOfRecordsWritten = 0;
	private KualiDecimal totalAmountRead = KualiDecimal.ZERO;
	private KualiDecimal totalAmountWritten = KualiDecimal.ZERO;

	public EnterpriseFeederReportData() {
	}

	public int getNumberOfRecordsRead() {
		return numberOfRecordsRead;
	}

	public int getNumberOfBalanceTypeActualsRead() {
		return numberOfBalanceTypeActualsRead;
	}

	public int getNumberOfBalanceTypeEncumbranceRead() {
		return numberOfBalanceTypeEncumbranceRead;
	}

	public int getNumberOfFringeActualsGenerated() {
		return numberOfFringeActualsGenerated;
	}

	public int getNumberOfFringeEncumbrancesGenerated() {
		return numberOfFringeEncumbrancesGenerated;
	}

	public int getNumberOfErrorEncountered() {
		return numberOfErrorEncountered;
	}

	public int getNumberOfRecordsWritten() {
		return numberOfRecordsWritten;
	}

	public KualiDecimal getTotalAmountRead() {
		return totalAmountRead;
	}

	public KualiDecimal getTotalAmountWritten() {
		return totalAmountWritten;
	}

	public void addToTotalAmountRead(KualiDecimal amount) {
		totalAmountRead = totalAmountRead.add(amount);
	}

	public void addToTotalAmountWritten(KualiDecimal amount) {
		totalAmountWritten = totalAmountWritten.add(amount);
	}

	public void incrementNumberOfRecordsWritten() {
		numberOfRecordsWritten++;
	}

	public void incrementNumberOfRecordsRead() {
		numberOfRecordsRead++;
	}

	public void incrementNumberOfBalanceTypeActualsRead() {
		numberOfBalanceTypeActualsRead++;
	}

	public void incrementNumberOfBalanceTypeEncumbranceRead() {
		numberOfBalanceTypeEncumbranceRead++;
	}

	public void incrementNumberOfFringeActualsGenerated() {
		numberOfFringeActualsGenerated++;
	}

	public void incrementNumberOfFringeEncumbrancesGenerated() {
		numberOfFringeEncumbrancesGenerated++;
	}

	public void incrementNumberOfErrorEncountered() {
		numberOfErrorEncountered++;
	}

}
