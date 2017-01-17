package edu.arizona.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import edu.arizona.kfs.module.purap.PurapPropertyConstants;
import edu.arizona.kfs.sys.businessobject.DocumentIncomeType;

public class PaymentRequestIncomeType extends PurApIncomeTypeBase implements DocumentIncomeType<Integer> {
    private static final long serialVersionUID = -1237037829530510726L;

    private Long paymentRequestIncomeTypeIdentifier;

    public Long getPaymentRequestIncomeTypeIdentifier() {
        return paymentRequestIncomeTypeIdentifier;
    }

    public void setPaymentRequestIncomeTypeIdentifier(Long paymentRequestIncomeTypeIdentifier) {
        this.paymentRequestIncomeTypeIdentifier = paymentRequestIncomeTypeIdentifier;
    }

    @Override
    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> retval = super.toStringMapper();

        retval.put(PurapPropertyConstants.PAYMENT_REQUEST_INCOME_TYPE_IDENTIFIER, paymentRequestIncomeTypeIdentifier);

        return retval;
    }

    @Override
    public Integer getLineNumber() {
        return getIncomeTypeLineNumber();
    }

    @Override
    public void setLineNumber(Integer lineNumber) {
        setIncomeTypeLineNumber(lineNumber);
    }

    @Override
    public Integer getDocumentIdentifier() {
        return getPurapDocumentIdentifier();
    }

    @Override
    public void setDocumentIdentifier(Integer documentIdentifier) {
        setPurapDocumentIdentifier(documentIdentifier);
    }

}
