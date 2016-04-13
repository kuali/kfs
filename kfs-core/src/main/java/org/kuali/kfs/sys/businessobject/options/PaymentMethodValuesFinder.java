package org.kuali.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of payment method key value pairs.
 */
public class PaymentMethodValuesFinder extends KeyValuesBase {

    static List<KeyValue> activeLabels = new ArrayList<KeyValue>();
    static {
        activeLabels.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        activeLabels.add(new ConcreteKeyValue(KFSConstants.PaymentMethod.ACH_CHECK.getCode(), KFSConstants.PaymentMethod.ACH_CHECK.getCodeAndName()));
        activeLabels.add(new ConcreteKeyValue(KFSConstants.PaymentMethod.FOREIGN_DRAFT.getCode(), KFSConstants.PaymentMethod.FOREIGN_DRAFT.getCodeAndName()));
        activeLabels.add(new ConcreteKeyValue(KFSConstants.PaymentMethod.WIRE_TRANSFER.getCode(), KFSConstants.PaymentMethod.WIRE_TRANSFER.getCodeAndName()));
    }
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        return activeLabels;
    }

}
