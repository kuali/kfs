package edu.arizona.kfs.module.cr.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

import edu.arizona.kfs.module.cr.businessobject.CheckReconSource;

public class CheckReconSrcValuesFinder extends KeyValuesBase {
    private static final long serialVersionUID = -7674393372938713085L;

    @Override
    public List<KeyValue> getKeyValues() {
        Collection<CheckReconSource> sources = SpringContext.getBean(KeyValuesService.class).findAll(CheckReconSource.class);
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        for (CheckReconSource src : sources) {
            keyValues.add(new ConcreteKeyValue(src.getSourceCode(), src.getSourceName()));
        }
        return keyValues;
    }

}
