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

import java.sql.Date;

import org.kuali.kfs.gl.batch.service.impl.OriginEntryOffsetPair;
import org.kuali.kfs.gl.batch.service.impl.exception.FatalErrorException;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;

/**
 * Service which generates encumbrance closing origin entries
 */
public interface EncumbranceClosingOriginEntryGenerationService {
    /**
     * Create a pair of cost share entries, one explicit and one offset to carry forward an encumbrance after validating the
     * encumbrance.
     * 
     * @param encumbrance the encumbrance to create origin entry and offset for
     * @param transactionDate the date all origin entries should have as their transaction date
     * @return a cost share entry/offset pair to carry forward the given encumbrance.
     */
    public abstract OriginEntryOffsetPair createCostShareBeginningBalanceEntryOffsetPair(Encumbrance encumbrance, Date transactionDate);
    
    /**
     * Create a pair of OriginEntries, one explicit and one offset to carry forward an encumbrance.
     * 
     * @param encumbrance the encumbrance to create origin entries for
     * @param closingFiscalYear the fiscal year that's closing
     * @param transactionDate the transaction date these entries should have
     * @return a entry/offset pair for the given encumbrance
     */
    public abstract OriginEntryOffsetPair createBeginningBalanceEntryOffsetPair(Encumbrance encumbrance, Integer closingFiscalYear, Date transactionDate);
    
    /**
     * Determine whether or not an encumbrance should be carried forward from one fiscal year to the next.
     * 
     * @param encumbrance the encumbrance to qualify
     * @return true if the encumbrance should be rolled forward from the closing fiscal year to the opening fiscal year.
     */
    public abstract boolean shouldForwardEncumbrance(Encumbrance encumbrance);
    
    /**
     * Do some validation and make sure that the encumbrance A21SubAccount is a cost share sub-account.
     * 
     * @param entry not used in this implementation
     * @param offset not used in this implementation
     * @param encumbrance the encumbrance whose A21SubAccount must be qualified
     * @param objectTypeCode the object type code of the generated entries
     * @return true if the encumbrance is eligible for cost share.
     * @throws FatalErrorException thrown if a given A21SubAccount, SubFundGroup, or PriorYearAccount record is not found in the database
     */
    public abstract boolean shouldForwardCostShareForEncumbrance(OriginEntryFull entry, OriginEntryFull offset, Encumbrance encumbrance, String objectTypeCode) throws FatalErrorException;
    
    /**
     * Determine whether or not the encumbrance has been fully relieved.
     * 
     * @param encumbrance the encumbrance to qualify
     * @return true if the amount closed on the encumbrance is NOT equal to the amount of the encumbrance itself, e.g. if the
     *         encumbrance has not yet been paid off.
     */
    public abstract boolean isEncumbranceClosed(Encumbrance encumbrance);
}
