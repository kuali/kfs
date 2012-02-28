package org.kuali.kfs.sys.businessobject.format;

import java.math.BigDecimal;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.BigDecimalFormatter;

/**
 * This class is used to format value to Kuali Decimal objects.
 */
public class KualiDecimalFormatter extends BigDecimalFormatter {

    /**
     * Converts the given String to a KualiDecimal
     */
    @Override
    protected Object convertToObject(String target) {
        BigDecimal value = (BigDecimal)super.convertToObject(target);
        return new KualiDecimal(value);
    }
}
