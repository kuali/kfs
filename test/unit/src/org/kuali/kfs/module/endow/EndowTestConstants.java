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
package org.kuali.kfs.module.endow;

import java.math.BigDecimal;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;

public class EndowTestConstants {
    public static final KualiDecimal ZERO_AMOUNT = KualiDecimal.ZERO;
    public static final KualiDecimal NEGATIVE_AMOUNT = new KualiDecimal("-1.00");
    public static final KualiDecimal POSITIVE_AMOUNT = new KualiDecimal("2.00");
    public static final KualiDecimal NEGATIVE_UNITS = new KualiDecimal("-1.00");
    public static final KualiDecimal POSITIVE_UNITS = new KualiDecimal("2.00");

    public static final String REFERENCE_DOCUMENT_NUMBER = "123456";
    public static final String REFERENCE_DOCUMENT_DESCRIPTION = "Document Description - Unit Test";
    public static final String INVALID_REGISTRATION_CODE = "...";
    public static final String INVALID_SECURITY_ID = "WRONG_ID";
    public static final String INVALID_KEMID = "WRONG_ID";
    public static final String INVALID_TRANSACTION_CODE = "NTRAN";
    public static final String NOT_APPLICABLE_TYPE_RESTRICTION_CODE = "NA";
    public static final String ETRAN_CODE = "42020";
    public static final String LIABILITY_CLASS_TYPE_CODE = "L";
    
    public static final KualiInteger FIRST_MONTH_END_DATE_ID = new KualiInteger(1);
    public static final String UNIT_VALUATION_METHOD_CODE = "U";
    public static final String MARKET_VALUATION_METHOD_CODE = "M";
    
    public static final String TEST_KEMID = "TESTKEMID";
    public static final String TEST_SEC_ID = "TESTSECID";
    public static final String TEST_REGISTRATION_CD = "TEST";
    public static final String TEST_REGISTRATION_CD_COMMITTED = "2TST";
    public static final String FREQ_CD_SEMIANUALLY_MARCH_15 = "IM15";
    public static final String SEPT_15_2010_TEST_DATE = "2010-09-15";
    public static final int NR_OF_DAY_IN_SEMIANNUAL_INTERVAL = 184; 
    public static final BigDecimal HOLDING_UNITS = new BigDecimal(20);
    public static final BigDecimal SECURITY_RATE = new BigDecimal(20);
    public static final BigDecimal SECURITY_DVND_AMT = new BigDecimal(20);
   
    
}
