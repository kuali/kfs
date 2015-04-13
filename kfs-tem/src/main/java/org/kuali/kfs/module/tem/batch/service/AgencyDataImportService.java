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
package org.kuali.kfs.module.tem.batch.service;


import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.tem.businessobject.AgencyImportData;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.krad.util.ErrorMessage;

public interface AgencyDataImportService {

    /**
     * Import Agency data
     *
     * @return
     */
    public boolean importAgencyData();

    /**
     * Import agency data for file name and input file type
     *
     * @param dataFileName
     * @param inputFileType
     * @return
     */
    public boolean importAgencyDataFile(String dataFileName, BatchInputFileType inputFileType);

    /**
     *
     * @param agencyData
     * @param dataFileName
     * @return
     */
    public List<AgencyStagingData> validateAgencyData(AgencyImportData agencyData, String dataFileName);

    /**
     *
     * This method gets all valid Agency Staging data and moves it to the historical expense table.
     * @return
     */
    boolean moveAgencyDataToHistoricalExpenseTable();

    /**
     *
     * @param agency
     * @param sequenceHelper
     */
    List<ErrorMessage> processAgencyStagingExpense(AgencyStagingData agency, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * Gets the currently existing GLPEs for the document we're going to add GLPEs to
     * @param agencyStagingData
     * @return
     */
    public Collection<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntriesForDocumentNumber(AgencyStagingData agencyStagingData);

}
