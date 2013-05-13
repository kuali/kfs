/*
 * Copyright 2009 The Kuali Foundation.
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
