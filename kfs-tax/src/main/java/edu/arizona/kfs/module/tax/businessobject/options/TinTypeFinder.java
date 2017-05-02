package edu.arizona.kfs.module.tax.businessobject.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import edu.arizona.kfs.module.tax.TaxConstants;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TinTypeFinder extends KeyValuesBase {

    private static final long serialVersionUID = 3301459238916314021L;

    public TinTypeFinder() {
        super();
        Map map = this.getKeyLabelMap();
        map.put(TaxConstants.TinTypeFinder.EIN_CODE, TaxConstants.TinTypeFinder.EIN);
        map.put(TaxConstants.TinTypeFinder.SSN_CODE, TaxConstants.TinTypeFinder.SSN);
    }

    @Override
    public List getKeyValues() {
        List<ConcreteKeyValue> keyValuesList = new ArrayList<ConcreteKeyValue>();

        keyValuesList.add(new ConcreteKeyValue(TaxConstants.TinTypeFinder.EIN_CODE, TaxConstants.TinTypeFinder.EIN));
        keyValuesList.add(new ConcreteKeyValue(TaxConstants.TinTypeFinder.SSN_CODE, TaxConstants.TinTypeFinder.SSN));

        return keyValuesList;
    }
}
