/*
 * Copyright 2009 The Kuali Foundation
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
