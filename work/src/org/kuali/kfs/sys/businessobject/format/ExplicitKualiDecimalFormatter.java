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
