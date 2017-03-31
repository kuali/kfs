package edu.arizona.kfs.module.ld.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;

import edu.arizona.kfs.module.ld.LaborConstants;
import edu.arizona.kfs.sys.KFSConstants;

public class BalanceTypeCodeOptionFinder extends org.kuali.kfs.module.ld.businessobject.options.BalanceTypeCodeOptionFinder {
	private static final long serialVersionUID = 1L;

	@Override
	public List getKeyValues() {
		List<ConcreteKeyValue> labels = super.getKeyValues();;
		
		labels.add(new ConcreteKeyValue(LaborConstants.BALANCE_TYPE_CURRENT_BUDGET, LaborConstants.BALANCE_TYPE_CURRENT_BUDGET_LABEL_DESCRIPTION));
		
		return labels;
	}

}
