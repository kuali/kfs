package edu.arizona.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import edu.arizona.kfs.module.purap.PurapPropertyConstants;
import edu.arizona.kfs.sys.businessobject.DocumentIncomeType;

public class CreditMemoIncomeType extends PurApIncomeTypeBase implements DocumentIncomeType<Integer> {
    private static final long serialVersionUID = -426914578633130575L;
    private Long creditMemoIncomeTypeIdentifier;

    @Override
    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> retval = super.toStringMapper();

        retval.put(PurapPropertyConstants.CREDIT_MEMO_INCOME_TYPE_IDENTIFIER, creditMemoIncomeTypeIdentifier);

        return retval;
    }

    public Long getCreditMemoIncomeTypeIdentifier() {
        return creditMemoIncomeTypeIdentifier;
    }

    public void setCreditMemoIncomeTypeIdentifier(Long creditMemoIncomeTypeIdentifier) {
        this.creditMemoIncomeTypeIdentifier = creditMemoIncomeTypeIdentifier;
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
