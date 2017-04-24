package edu.arizona.kfs.pdp.businessobject.options;

import org.kuali.kfs.sys.businessobject.options.ParameterValuesFinder;

import edu.arizona.kfs.pdp.businessobject.ShippingAccount;
import edu.arizona.kfs.sys.KFSParameterKeyConstants;

public class ShippingAcctTypeValuesFinder extends ParameterValuesFinder {
	
	public ShippingAcctTypeValuesFinder() {
		super(ShippingAccount.class, KFSParameterKeyConstants.ShippingConstants.SHIPPING_COMPANIES);
	}
}
