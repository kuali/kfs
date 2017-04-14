package edu.arizona.kfs.module.cr.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

public class BankNameValuesFinder extends KeyValuesBase {
    private static final long serialVersionUID = -9180794276697311881L;

    @Override
    public List<KeyValue> getKeyValues() {
        Collection<Bank> banks = SpringContext.getBean(KeyValuesService.class).findAll(Bank.class);
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        for (Bank bank : banks) {
            if (bank.isActive()) {
                keyValues.add(new ConcreteKeyValue(bank.getBankShortName(), bank.getBankName()));
            }
        }
        return keyValues;
    }

}
