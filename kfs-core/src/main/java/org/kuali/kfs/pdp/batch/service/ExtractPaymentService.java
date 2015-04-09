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
package org.kuali.kfs.pdp.batch.service;

import java.util.List;

import org.kuali.kfs.sys.batch.InitiateDirectory;

public interface ExtractPaymentService extends InitiateDirectory {
    /**
     * Extract checks to be cut into a file to be processed by the check printing service
     */
    public void extractChecks();

    /**
     * Extract ach payments to be sent to the bank for processing
     */
    public void extractAchPayments();

    /**
     * Extract canceled checks to be used for a bank's positive pay program.
     */
    public void extractCanceledChecks();

    /**
     * Format the given check note so that we can handle
     * notes with text that is longer than the max length in the
     * database.
     *
     * @param checkNote
     * @return
     */
    public List<String> formatCheckNoteLines(String checkNote);
}
