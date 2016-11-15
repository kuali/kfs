package edu.arizona.kfs.tax.businessobject.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class TinTypeFinder extends KeyValuesBase {

    public TinTypeFinder() {
       super();
       Map map = this.getKeyLabelMap();
       map.put("1", "EIN");
       map.put("2", "SSN");
    }
    
    @Override
    public List getKeyValues() {
        List<ConcreteKeyValue> keyValuesList = new ArrayList<ConcreteKeyValue>();
        
        keyValuesList.add( new ConcreteKeyValue("1", "EIN") );
        keyValuesList.add( new ConcreteKeyValue("2", "SSN") );
        
        return keyValuesList;
    }
}