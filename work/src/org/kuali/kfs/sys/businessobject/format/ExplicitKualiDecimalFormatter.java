/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject.format;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.BigDecimalFormatter;


/**
 * This class is used to format explicit decimal value to BigDecimal objects.
 */
public class ExplicitKualiDecimalFormatter extends BigDecimalFormatter {
	private static Logger LOG = Logger.getLogger(ExplicitKualiDecimalFormatter.class);

	/**
	 * Converts the given String into a KualiDecimal with the final two characters being behind the decimal place
	 */
	@Override
    protected Object convertToObject(String target) {
		BigDecimal value = (BigDecimal)super.convertToObject(addDecimalPoint(target));
		return new KualiDecimal(value);
	}

	/**
	 * Adds the decimal point to the String
	 * @param amount the String representing the amount
	 * @return a new String, with a decimal inserted in the third to last place
	 */
	private String addDecimalPoint (String amount) {
        if (!amount.contains(".")) {  //have to add decimal point if it's missing
            int length = amount.length();
            amount = amount.substring(0, length - 2) + "." + amount.substring(length - 2, length);
        }
        return amount;
    }
}
