package edu.arizona.kfs.module.cr.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.pdp.businessobject.PayeeType;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

public class PayeeIdTypeCodeValuesFinder extends KeyValuesBase {
    private static final long serialVersionUID = 7249857044261861206L;

    @Override
    public List<KeyValue> getKeyValues() {
        Collection<PayeeType> boList = SpringContext.getBean(KeyValuesService.class).findAll(PayeeType.class);
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        for (PayeeType element : boList) {
            keyValues.add(new ConcreteKeyValue(element.getCode(), element.getName()));
        }
        return keyValues;
    }

}
