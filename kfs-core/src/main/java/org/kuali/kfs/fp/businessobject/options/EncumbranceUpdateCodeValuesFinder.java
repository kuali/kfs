package org.kuali.kfs.fp.businessobject.options;

import java.util.ArrayList;
import java.util.List;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;


public class EncumbranceUpdateCodeValuesFinder extends KeyValuesBase {

	/**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
	@Override
	public List<KeyValue> getKeyValues() {
		List<KeyValue> keyValues = new ArrayList<KeyValue>();
		keyValues.add(new ConcreteKeyValue("R", "R"));
		keyValues.add(new ConcreteKeyValue("D", "D"));
		return keyValues;
	}
}