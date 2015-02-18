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
