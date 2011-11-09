/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.BusinessObjectStringParserFieldUtils;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This class has utility methods for parsing OriginEntries from Strings
 */
public class OriginEntryFieldUtil extends BusinessObjectStringParserFieldUtils {

    /**
     * Returns the class to parse into - OriginEntryFull
     * @see org.kuali.kfs.sys.businessobject.BusinessObjectStringParserFieldUtils#getBusinessObjectClass()
     */
    @Override
    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return OriginEntryFull.class;
    }

    /**
     * Returns the fields to be parsed from a String, in order, to form an OriginEntryFull
     * @see org.kuali.kfs.sys.businessobject.BusinessObjectStringParserFieldUtils#getOrderedProperties()
     */
    @Override
    public String[] getOrderedProperties() {
        return new String[] {
                KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                KFSPropertyConstants.ACCOUNT_NUMBER,
                KFSPropertyConstants.SUB_ACCOUNT_NUMBER,
                KFSPropertyConstants.FINANCIAL_OBJECT_CODE,
                KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE,
                KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE,
                KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE,
                KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE,
                KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE,
                KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE,
                KFSPropertyConstants.DOCUMENT_NUMBER,
                KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER,
                KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC,
                KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT,
                KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE,
                KFSPropertyConstants.TRANSACTION_DATE,
                KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER,
                KFSPropertyConstants.PROJECT_CODE,
                KFSPropertyConstants.ORGANIZATION_REFERENCE_ID,
                KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE,
                KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE,
                KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR,
                KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE,
                KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD
            };
    }
}
