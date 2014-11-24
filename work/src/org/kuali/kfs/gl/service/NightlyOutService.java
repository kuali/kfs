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
package org.kuali.kfs.gl.service;

/**
 * This interface defines the batch jobs that would be run nightly against the pending general ledger entries. These jobs are
 * performed to ensure the correctness of the entries. As to date, the jobs involve two functions: finding unbalanced approved
 * documents and finding bad approved documents.
 */
public interface NightlyOutService {

    /**
     * Deletes all the records that were copied to the GL.
     */
    public void deleteCopiedPendingLedgerEntries();

    /**
     * Copies the approved pending ledger entries to orign entry table
     */
    public void copyApprovedPendingLedgerEntries();
}
