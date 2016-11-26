package edu.arizona.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import edu.arizona.kfs.tax.businessobject.DocumentIncomeType;

public class PaymentRequestIncomeType extends PurApIncomeTypeBase implements DocumentIncomeType<Integer> {
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

		retval.put("paymentRequestIncomeTypeIdentifier", paymentRequestIncomeTypeIdentifier);

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
