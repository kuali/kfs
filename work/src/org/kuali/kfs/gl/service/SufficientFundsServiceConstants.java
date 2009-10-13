/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.service;


/**
 * Holds constants for transfer of funds document.
 */
public interface SufficientFundsServiceConstants {
    /**
     * Security grouping constants used to do application parameter lookups
     */
    public static final String KUALI_TRANSACTION_PROCESSING_SUFFICIENT_FUNDS_SECURITY_GROUPING = "Kuali.FinancialTransactionProcessing.SufficientFundsService";

    
    /**
     * Application parameter lookup constants to be used in conjunction with the grouping constants above
     */
    public static final String SUFFICIENT_FUNDS_OBJECT_CODE_CASH_IN_BANK = "SufficientFundsServiceFinancialObjectCodeForCashInBank";
    
    /**
     * Application parameter lookup constants to be used in conjunction with the grouping constants above
     */
    public static final String SUFFICIENT_FUNDS_OBJECT_CODE_SPECIALS = "SUFFICIENT_FUNDS_CURRENT_LIABILITIES_OBJECT_CODES";
}
