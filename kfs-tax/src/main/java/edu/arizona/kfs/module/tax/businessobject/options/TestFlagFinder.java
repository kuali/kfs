package edu.arizona.kfs.module.tax.businessobject.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import edu.arizona.kfs.module.tax.TaxConstants;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TestFlagFinder extends KeyValuesBase {

    private static final long serialVersionUID = 5337027760062185203L;

    public TestFlagFinder() {
        super();
        Map map = this.getKeyLabelMap();
        map.put(TaxConstants.TestTypeFinder.T_CODE, TaxConstants.TestTypeFinder.T);
    }

    @Override
    public List getKeyValues() {
        List<ConcreteKeyValue> keyValuesList = new ArrayList<ConcreteKeyValue>();
        keyValuesList.add(new ConcreteKeyValue(TaxConstants.TestTypeFinder.T_CODE, TaxConstants.TestTypeFinder.T));
        return keyValuesList;
    }
}
