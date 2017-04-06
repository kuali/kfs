package edu.arizona.kfs.pdp.businessobject;

public class PayeeACHAccount extends org.kuali.kfs.pdp.businessobject.PayeeACHAccount {

	private static final long serialVersionUID = 1L;

	private String payeeEmailAddress;
	private String payeeName;

	@Override
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	@Override
	public void setPayeeEmailAddress(String payeeEmailAddress) {
		this.payeeEmailAddress = payeeEmailAddress;
	}

	@Override
	public String getPayeeEmailAddress() {
		return payeeEmailAddress;
	}

	@Override
	public String getPayeeName() {
		return payeeName;
	}
	
	
}
