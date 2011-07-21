package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerBillingStatement;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.service.ParameterService;

public class IncludeZeroBalanceCustomersOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the default value for this ValueFinder
     * @return a String with the default key
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return ArConstants.INCLUDE_ZERO_BALANCE_NO;
    }

    /**
     * Returns a list of possible values for this ValueFinder
     * @return a List of key/value pairs to populate radio buttons
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        labels.add(new KeyLabelPair(ArConstants.INCLUDE_ZERO_BALANCE_YES, ArConstants.INCLUDE_ZERO_BALANCE_YES));
        labels.add(new KeyLabelPair(ArConstants.INCLUDE_ZERO_BALANCE_NO, ArConstants.INCLUDE_ZERO_BALANCE_NO));
        return labels;
    }
}
