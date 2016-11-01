package edu.arizona.kfs.tax.businessobject.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class TestFlagFinder extends KeyValuesBase {
    
    /**
     * 
     * Constructs a Test Flg Finder.java.
     */
    public TestFlagFinder() {
       super();
       Map map = this.getKeyLabelMap();
       map.put("T", "T");
    }
    
    /**
     * 
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValuesList = new ArrayList();
        
        keyValuesList.add( new ConcreteKeyValue("T", "T") );
        
        return keyValuesList;
    }
}