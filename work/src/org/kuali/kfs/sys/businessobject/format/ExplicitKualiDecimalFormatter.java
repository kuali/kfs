package org.kuali.kfs.sys.businessobject.format;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.web.format.BigDecimalFormatter;


/**
 * This class is used to format explicit decimal value to BigDecimal objects.
 */
public class ExplicitKualiDecimalFormatter extends BigDecimalFormatter {
	private static Logger LOG = Logger.getLogger(ExplicitKualiDecimalFormatter.class);

	/**
	 * Converts the given String into a KualiDecimal with the final two characters being behind the decimal place
	 */
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
