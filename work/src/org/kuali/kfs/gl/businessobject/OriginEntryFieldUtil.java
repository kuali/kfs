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
