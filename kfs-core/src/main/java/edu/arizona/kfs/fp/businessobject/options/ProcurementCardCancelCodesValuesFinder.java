package edu.arizona.kfs.fp.businessobject.options;

import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.ConcreteKeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class returns a list with the corresponding descriptions for the Procurement Card Cancel Status Codes (hardcoded values for now...)
 * Created by nataliac on 9/08/16.
 */
public class ProcurementCardCancelCodesValuesFinder extends KeyValuesBase {

    static private List<KeyValue> cancelCodes = new ArrayList();
    static {
        cancelCodes.add(new ConcreteKeyValue("B", "Canceled - Bank Request"));
        cancelCodes.add(new ConcreteKeyValue("C", "Canceled - Collections"));
        cancelCodes.add(new ConcreteKeyValue("E", "Canceled"));
        cancelCodes.add(new ConcreteKeyValue("I", "Canceled - Institution Specific"));
        cancelCodes.add(new ConcreteKeyValue("P", "Canceled - Temporary"));
        cancelCodes.add(new ConcreteKeyValue("M", "Canceled - Permanent"));
        cancelCodes.add(new ConcreteKeyValue("T", "Canceled - Cardholder Terminated"));
        cancelCodes.add(new ConcreteKeyValue("N", "Canceled - No Longer Needed"));
        cancelCodes.add(new ConcreteKeyValue("R", "Canceled - Customer Request"));
        cancelCodes.add(new ConcreteKeyValue("F", "Transferred"));
        cancelCodes.add(new ConcreteKeyValue("L", "Lost/Stolen"));
        cancelCodes.add(new ConcreteKeyValue("D", "Lost/Stolen Fraud"));
        cancelCodes.add(new ConcreteKeyValue("A", "Canceled - PA Requests Close"));
        cancelCodes.add(new ConcreteKeyValue("O", "Canceled Within Five Days of Opening"));
        cancelCodes.add(new ConcreteKeyValue("X", " "));
    }


   /*
   * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
   *
   */
    public List<KeyValue> getKeyValues() {
        return  cancelCodes;
    }
}
