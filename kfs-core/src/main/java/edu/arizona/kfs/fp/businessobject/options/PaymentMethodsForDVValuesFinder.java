package edu.arizona.kfs.fp.businessobject.options;

import java.util.HashMap;
import java.util.Map;
import edu.arizona.kfs.sys.businessobject.options.PaymentMethodValuesFinder;

/**
 * This class returns list of payment method key value pairs.
 * 
 * Customization for UA: addition of "A" type for credit card payments.
 */
public class PaymentMethodsForDVValuesFinder extends PaymentMethodValuesFinder {
    static private Map<String,String> filterCriteria = new HashMap<String, String>();
    static {
        filterCriteria.put("active", "Y");
        filterCriteria.put("displayOnDisbursementVoucherDocument", "Y");
    }    
    
    protected Map<String,String> getFilterCriteria() {
        return filterCriteria;
    }
}
