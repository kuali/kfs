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
package org.kuali.kfs.gl.batch.service;

import java.io.File;
import java.io.PrintStream;

import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.service.impl.EnterpriseFeederStatusAndErrorMessagesWrapper;


/**
 * Implementations of this interface are responsible for reconciliation of origin entry data in a file and loading them into the
 * origin entry table. Note that implementations of this class may have special useful transactional properties. See implementation
 * description for more details.
 */
public interface FileEnterpriseFeederHelperService {

    /**
     * Reconciles and loads a file of origin entries into the origin entry table. This method DOES NOT handle the deletion of the
     * done file
     * 
     * @param doneFile the done file. Must exist and be non-empty
     * @param dataFile the data file. A connection to this file may be opened multiple times by this method.
     * @param reconFile the reconciliation file. See implementations of
     *        {@link org.kuali.kfs.gl.batch.service.ReconciliationParserService} to determine the format of the data in a file.
     * @param originEntryGroup the group in which to place the origin entries
     * @param feederProcessName the name of the process that's invoking this method.
     * @param reconciliationTableId the name of the reconciliation block to use within the reconciliation file
     * @param statusAndErrors a class with references to a {@link org.kuali.kfs.gl.batch.service.impl.EnterpriseFeederStatus} object and a list
     *        of error messages. Implementations of this method may need to throw an exception to force a transaction rollback,
     *        which means that it can't return a value. This parameter allows the method to output status/error information
     */
    public void feedOnFile(File doneFile, File dataFile, File reconFile, PrintStream enterpriseFeedPs, String feederProcessName, String reconciliationTableId, EnterpriseFeederStatusAndErrorMessagesWrapper statusAndErrors, LedgerSummaryReport ledgerSummaryReport);
}
