package edu.arizona.kfs.module.tax.businessobject.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import edu.arizona.kfs.module.tax.TaxConstants;
import edu.arizona.kfs.sys.KFSConstants;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class PaymentTypeFinder extends KeyValuesBase {
    public static final long serialVersionUID = -8076852195281948396L;

    public PaymentTypeFinder() {
        super();
        Map<String, String> map = this.getKeyLabelMap();
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_1, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_1_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_2, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_2_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_3, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_3_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_4, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_4_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_5, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_5_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_6, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_6_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_7, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_7_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_8, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_8_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_A, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_A_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_B, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_B_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_C, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_C_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_D, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_D_TYPE);
        map.put(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_E, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_E_TYPE);
    }

    @Override
    public List getKeyValues() {
        List<ConcreteKeyValue> keyValuesList = new ArrayList<ConcreteKeyValue>();

        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_1, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_1_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_2, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_2_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_3, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_3_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_4, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_4_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_5, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_5_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_6, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_6_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_7, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_7_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_8, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_8_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_A, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_A_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_B, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_B_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_C, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_C_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_D, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_D_TYPE));
        keyValuesList.add(new ConcreteKeyValue(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_E, TaxConstants.PaymentTypeFinder.AMOUNT_CODE_E_TYPE));

        return keyValuesList;
    }
}
