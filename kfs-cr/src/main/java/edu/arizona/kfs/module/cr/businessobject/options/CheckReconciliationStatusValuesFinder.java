package edu.arizona.kfs.module.cr.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import edu.arizona.kfs.module.cr.CrConstants;

public class CheckReconciliationStatusValuesFinder extends KeyValuesBase {
    private static final long serialVersionUID = -2486515348600810121L;

    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        keyValues.add(new ConcreteKeyValue(CrConstants.CheckReconciliationStatusCodes.CLEARED, CrConstants.CheckReconciliationStatusName.CLEARED));
        keyValues.add(new ConcreteKeyValue(CrConstants.CheckReconciliationStatusCodes.CANCELLED, CrConstants.CheckReconciliationStatusName.CANCELLED));
        keyValues.add(new ConcreteKeyValue(CrConstants.CheckReconciliationStatusCodes.ISSUED, CrConstants.CheckReconciliationStatusName.ISSUED));
        keyValues.add(new ConcreteKeyValue(CrConstants.CheckReconciliationStatusCodes.VOIDED, CrConstants.CheckReconciliationStatusName.VOIDED));
        keyValues.add(new ConcreteKeyValue(CrConstants.CheckReconciliationStatusCodes.STALE, CrConstants.CheckReconciliationStatusName.STALE));
        keyValues.add(new ConcreteKeyValue(CrConstants.CheckReconciliationStatusCodes.STOP, CrConstants.CheckReconciliationStatusName.STOP));
        keyValues.add(new ConcreteKeyValue(CrConstants.CheckReconciliationStatusCodes.EXCP, CrConstants.CheckReconciliationStatusName.EXCP));
        return keyValues;
    }

}
