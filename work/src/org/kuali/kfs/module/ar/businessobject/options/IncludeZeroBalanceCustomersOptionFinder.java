package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerBillingStatement;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.KeyValue; import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;
import org.kuali.rice.core.framework.parameter.ParameterService;

public class IncludeZeroBalanceCustomersOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the default value for this ValueFinder
     * @return a String with the default key
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        return ArConstants.INCLUDE_ZERO_BALANCE_NO;
    }

    /**
     * Returns a list of possible values for this ValueFinder
     * @return a List of key/value pairs to populate radio buttons
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue(ArConstants.INCLUDE_ZERO_BALANCE_YES, ArConstants.INCLUDE_ZERO_BALANCE_YES));
        labels.add(new ConcreteKeyValue(ArConstants.INCLUDE_ZERO_BALANCE_NO, ArConstants.INCLUDE_ZERO_BALANCE_NO));
        return labels;
    }
}
