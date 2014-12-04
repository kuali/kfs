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
package org.kuali.kfs.module.ld.businessobject;

import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.BusinessObjectStringParserFieldUtils;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This class has utility methods for OriginEntry
 */
public class LaborOriginEntryFieldUtil extends BusinessObjectStringParserFieldUtils {

    /**
     * Returns the class to parse into LaborOriginEntry
     * @see org.kuali.kfs.sys.businessobject.BusinessObjectStringParserFieldUtils#getBusinessObjectClass()
     */
    @Override
    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return LaborOriginEntry.class;
    }

    /**
     * Returns the fields, in order, to parse to create a LaborOriginEntry
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
                KFSPropertyConstants.POSITION_NUMBER,
                KFSPropertyConstants.PROJECT_CODE,
                KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC,
                KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT,
                KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE,
                
                KFSPropertyConstants.TRANSACTION_DATE,
                KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER,
                KFSPropertyConstants.ORGANIZATION_REFERENCE_ID,
                KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE,
                KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE,
                KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR,
                KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE,
                KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD,
                
                KFSPropertyConstants.TRANSACTION_POSTING_DATE,
                KFSPropertyConstants.PAY_PERIOD_END_DATE,
                KFSPropertyConstants.TRANSACTION_TOTAL_HOURS,
                KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR,
                LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE,
                KFSPropertyConstants.EMPLID, 
                KFSPropertyConstants.EMPLOYEE_RECORD, 
                KFSPropertyConstants.EARN_CODE, 
                KFSPropertyConstants.PAY_GROUP, 
                LaborPropertyConstants.SALARY_ADMINISTRATION_PLAN, 
                LaborPropertyConstants.GRADE, 
                LaborPropertyConstants.RUN_IDENTIFIER, 
                LaborPropertyConstants.LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE, 
                LaborPropertyConstants.LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER, 
                LaborPropertyConstants.LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER, 
                LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE, 
                LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE, 
                LaborPropertyConstants.HRMS_COMPANY, 
                LaborPropertyConstants.SET_ID
                };
    }
}
