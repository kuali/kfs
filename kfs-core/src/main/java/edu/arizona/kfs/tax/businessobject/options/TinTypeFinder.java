package edu.arizona.kfs.tax.businessobject.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class TinTypeFinder extends KeyValuesBase {
    
    /**
     * 
     * Constructs a TinTypeFinder.java.
     */
    public TinTypeFinder() {
       super();
       Map map = this.getKeyLabelMap();
       map.put("1", "EIN");
       map.put("2", "SSN");
    }
    
    /**
     * 
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValuesList = new ArrayList();
        
        keyValuesList.add( new ConcreteKeyValue("1", "EIN") );
        keyValuesList.add( new ConcreteKeyValue("2", "SSN") );
        
        return keyValuesList;
    }
}