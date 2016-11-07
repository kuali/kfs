package edu.arizona.kfs.fp.businessobject.options;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class returns a list with the corresponding descriptions for the Procurement Card Status Codes (hardcoded values for now...)
 * Created by nataliac on 9/16/16.
 */
public class ProcurementCardStatusValuesFinder extends KeyValuesBase {

    static private List<KeyValue> statusCodes = new ArrayList();
    static {
        statusCodes.add(new ConcreteKeyValue("A", "Active"));
        statusCodes.add(new ConcreteKeyValue("C", "Closed"));
        statusCodes.add(new ConcreteKeyValue("T", "Card Activation"));
        statusCodes.add(new ConcreteKeyValue("V", "Card Activation - Monitoring"));
        statusCodes.add(new ConcreteKeyValue("M", "Monitoring"));
        statusCodes.add(new ConcreteKeyValue("H", "Card Held"));
        statusCodes.add(new ConcreteKeyValue("E", "Chargeoff Account"));
        statusCodes.add(new ConcreteKeyValue("N", "New"));
        statusCodes.add(new ConcreteKeyValue("O", "Overlimit"));
        statusCodes.add(new ConcreteKeyValue("P", "Pending"));
        statusCodes.add(new ConcreteKeyValue("L", "Temporary Lost/Stolen"));
        statusCodes.add(new ConcreteKeyValue("B", "Temporarily Blocked Stolen"));
    }


   /*
   * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
   *
   */
    public List<KeyValue> getKeyValues() {
        return  statusCodes;
    }
}
