package edu.arizona.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.fp.businessobject.PaymentMethod;

/**
 * This class returns list of payment method key value pairs.
 * 
 * Customization for UA: addition of "A" type for credit card payments.
 * 
 * @author jonathan
 * @see org.kuali.kfs.sys.businessobject.options.PaymentMethodValuesFinder
 */
public class PaymentMethodValuesFinder extends KeyValuesBase {
    private static BusinessObjectService businessObjectService;
    static protected Map<String,String> filterCriteria = new HashMap<String, String>();
    static {
        filterCriteria.put("active", "Y");
    }
    
    /*
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        Collection<PaymentMethod> paymentMethods = getBusinessObjectService().findMatchingOrderBy(PaymentMethod.class, getFilterCriteria(), "paymentMethodCode", true);
        List<KeyValue> labels = new ArrayList<>( paymentMethods.size() );       
        for ( PaymentMethod pm : paymentMethods ) {
            labels.add(new ConcreteKeyValue (pm.getPaymentMethodCode(), pm.getPaymentMethodCode() + " - " + pm.getPaymentMethodName()));
        }
        return labels;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if ( businessObjectService == null ) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }
    
    protected Map<String,String> getFilterCriteria() {
        return filterCriteria;
    }
    
}

