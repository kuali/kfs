package edu.arizona.kfs.fp.businessobject.options;

import java.util.HashMap;
import java.util.Map;
import edu.arizona.kfs.sys.businessobject.options.PaymentMethodValuesFinder;

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
