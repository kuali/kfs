package edu.arizona.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.sys.businessobject.IncomeType;

public class IncomeTypeValuesFinder extends KeyValuesBase {
    private static final long serialVersionUID = -2811732634356220724L;

    @Override
    public List<KeyValue> getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<IncomeType> incomeTypeCodes = boService.findAllOrderBy(IncomeType.class, KFSPropertyConstants.IncomeTypeFields.INCOME_TYPE_CODE, true);
        List<KeyValue> retval = new ArrayList<KeyValue>();
        retval.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        for (IncomeType incomeType : incomeTypeCodes) {
            if (incomeType.isActive()) {
                retval.add(new ConcreteKeyValue(incomeType.getIncomeTypeCode(), incomeType.getFullDescription()));
            }
        }
        return retval;
    }

}
