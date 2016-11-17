package edu.arizona.kfs.tax.businessobject.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.core.api.util.ConcreteKeyValue;

public class PaymentTypeFinder extends KeyValuesBase {
    
    public PaymentTypeFinder() {
       super();
       Map<String, String> map = this.getKeyLabelMap();
       map.put("1", "Rents");
       map.put("2", "Royalties");
       map.put("3", "Other Income");
       map.put("4", "Fed. Income Tax Witheld");
       map.put("5", "Fishing Boat");
       map.put("6", "Medical and Health Care");
       map.put("7", "Nonemployee Compensation");
       map.put("8", "Dividends or Interest");
       map.put("A", "Crop Insurance");
       map.put("B", "Golden Parachute");
       map.put("C", "Legal Services");
       map.put("D", "Sec. 409A Deferrals");
       map.put("E", "Sec. 409A Income");
    }
    
    @Override
    public List getKeyValues() {
        List<ConcreteKeyValue> keyValuesList = new ArrayList<ConcreteKeyValue>();
        
        keyValuesList.add( new ConcreteKeyValue("1", "Rents") );
        keyValuesList.add( new ConcreteKeyValue("2", "Royalties") );
        keyValuesList.add( new ConcreteKeyValue("3", "Other Income") );
        keyValuesList.add( new ConcreteKeyValue("4", "Fed. Income Tax Witheld") );
        keyValuesList.add( new ConcreteKeyValue("5", "Fishing Boat") );
        keyValuesList.add( new ConcreteKeyValue("6", "Medical Health Care") );
        keyValuesList.add( new ConcreteKeyValue("7", "Nonemployee Compensation") );
        keyValuesList.add( new ConcreteKeyValue("8", "Dividends or Interest") );
        keyValuesList.add( new ConcreteKeyValue("A", "Crop Insurance") );
        keyValuesList.add( new ConcreteKeyValue("B", "Golden Parachute") );
        keyValuesList.add( new ConcreteKeyValue("C", "Legal Services") );
        keyValuesList.add( new ConcreteKeyValue("D", "Sec. 409A Deferrals") );
        keyValuesList.add( new ConcreteKeyValue("E", "Sec. 409A Income") );
        
        return keyValuesList;
    }
}
