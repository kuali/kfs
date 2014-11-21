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
package org.kuali.kfs.gl.businessobject;

import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * A class that defines all the source codes for all types of Origin Entry groups
 */

public class OriginEntrySource extends KualiCodeBase {
    /**
     * A general ledger backup group
     */
    public static final String BACKUP = "BACK";
    /**
     * a general ledger group created by the collector
     */
    public static final String COLLECTOR = "COLL";
    /**
     * a general ledger group made of origin entries from processed documents; created by nightly out
     */
    public static final String GENERATE_BY_EDOC = "EDOC";
    /**
     * 
     */
    public static final String EXTERNAL = "EXT";
    /**
     * a general ledger group creacted by the GLCP
     */
    public static final String GL_CORRECTION_PROCESS_EDOC = "GLCP";
    /**
     * a general ledger group of indirect cost recovery origin entries
     */
    public static final String ICR_TRANSACTIONS = "ICR";
    /**
     * a general ledger group of indirect cost recovery origin entries that resulted in poster errors
     */
    public static final String ICR_POSTER_ERROR = "ICRE";
    /**
     * a general ledger group of indirect cost recovery origin entries that the poster considered valid
     */
    public static final String ICR_POSTER_VALID = "ICRV";
    /**
     * a general ledger group of origin entries that the poster reports as errors
     */
    public static final String MAIN_POSTER_ERROR = "MPE";
    /**
     * a general ledger group of origin entries that the poster considered valid and posted
     */
    public static final String MAIN_POSTER_VALID = "MPV";
    /**
     * a general ledger group of origin entries that the reversal poster reported as errors
     */
    public static final String REVERSAL_POSTER_ERROR = "RPE";
    /**
     * a general ledger group of origin entries that the reversal poster considered valid and posted
     */
    public static final String REVERSAL_POSTER_VALID = "RPV";
    /**
     * a general ledger group of origin entries the scrubber reported were in error
     */
    public static final String SCRUBBER_ERROR = "SCE";
    /**
     * a general ledger group of origin entries that the scrubber considered valid (and therefore are ready to be posted)
     */
    public static final String SCRUBBER_VALID = "SCV";
    /**
     * a general ledger group of origin entries that the scrubber reported as having expired accounts
     */
    public static final String SCRUBBER_EXPIRED = "SCX";
    /**
     * a general ledger group created by the balance forwards year end job with still open accounts
     */
    public static final String YEAR_END_BEGINNING_BALANCE = "YEBB";
    /**
     * a general ledger group created by the balance forwards year end job with still closed accounts
     */
    public static final String YEAR_END_BEGINNING_BALANCE_PRIOR_YEAR = "YEBC";
    /**
     * a general ledger group created by the nominal activity closing year end job
     */
    public static final String YEAR_END_CLOSE_NOMINAL_BALANCES = "YECN";
    /**
     * a general ledger group created by the forward encumbrances year end job
     */
    public static final String YEAR_END_ENCUMBRANCE_CLOSING = "YEEC";
    /**
     * a general ledger group created by the organization reversion year end job
     */
    public static final String YEAR_END_ORG_REVERSION = "YEOR";
    /**
     * a general ledger group created by the post disbursement processor
     */
    public static final String PDP = "PDP";
    /**
     * a general ledger group created by the enterprise feeder
     */
    public static final String ENTERPRISE_FEED = "ENTP";

    // Origin entry source codes that are used by Labor Distribution
    /**
     * a labor ledger group of origin entries that the labor poster reports as errors
     */
    public static final String LABOR_MAIN_POSTER_ERROR = "LMPE";
    /**
     * a labor ledger group of origin entries that the labor poster considered valid and posted
     */
    public static final String LABOR_MAIN_POSTER_VALID = "LMPV";
    /**
     * a labor ledger group of origin entries that the labor scrubber reports as errors
     */
    public static final String LABOR_SCRUBBER_ERROR = "LSCE";
    /**
     * a labor ledger group of origin entries that the labor scrubber considers valid (and therefore can be posted by the labor
     * poster)
     */
    public static final String LABOR_SCRUBBER_VALID = "LSCV";
    /**
     * a backup labor ledger group
     */
    public static final String LABOR_BACKUP = "LBAK";
    /**
     * a labor ledger group of origin entries that the labor scrubber reported as having expired accounts
     */
    public static final String LABOR_SCRUBBER_EXPIRED = "LSCX";
    /**
     * a labor ledger group created by labor processing documents
     */
    public static final String LABOR_EDOC = "LDOC";
    /**
     * a labor ledger group of origin entries created by a Labor Ledger Correction Process document
     */
    public static final String LABOR_CORRECTION_PROCESS_EDOC = "LLCP";
    /**
     * a labor ledger group created by the labor balance forwards year end job
     */
    public static final String LABOR_YEAR_END_BALANCE_FORWARD = "LBF";

    /**
     * a labor ledger group of origin entries for payroll accrual
     */
    public static final String LABOR_PAYROLL_ACCRUAL = "ACCR";
    /**
     * a labor ledger group of origin entries
     */
    public static final String LABOR_LEDGER_GENERAL_LEDGER = "LLGL";
    
}
