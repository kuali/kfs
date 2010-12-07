/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow;

import org.apache.bcel.generic.NEW;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.util.JSTLConstants;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

public class EndowTestConstants extends JSTLConstants {
    public static final KualiDecimal ZERO_AMOUNT = KualiDecimal.ZERO;
    public static final KualiDecimal NEGATIVE_AMOUNT = new KualiDecimal("-1.00");
    public static final KualiDecimal POSITIVE_AMOUNT = new KualiDecimal("2.00");
    public static final KualiDecimal NEGATIVE_UNITS = new KualiDecimal("-1.00");
    public static final KualiDecimal POSITIVE_UNITS = new KualiDecimal("2.00");

    public static final String REFERENCE_DOCUMENT_NUMBER = "123456";
    public static final String REFERENCE_DOCUMENT_DESCRIPTION = "Document Description - Unit Test";
    public static final String INVALID_REGISTRATION_CODE = "...";
    public static final String INVALID_SECURITY_ID = "WRONG_ID";
    public static final String ETRAN_CODE = "42020";
    
    public static final KualiInteger FIRST_MONTH_END_DATE_ID = new KualiInteger(1);
    public static final String UNIT_VALUATION_METHOD_CODE = "U";
    public static final String MARKET_VALUATION_METHOD_CODE = "M";
    
}
