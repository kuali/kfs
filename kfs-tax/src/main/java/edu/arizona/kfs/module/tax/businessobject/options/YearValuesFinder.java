package edu.arizona.kfs.module.tax.businessobject.options;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class YearValuesFinder extends KeyValuesBase {
    private static final long serialVersionUID = 1L;

    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValuesList = new ArrayList<KeyValue>();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        for (int i = 2008; i <= year; i++) {
            keyValuesList.add(new ConcreteKeyValue(new Integer(i).toString(), i + KFSConstants.EMPTY_STRING));
        }

        return keyValuesList;
    }

}
