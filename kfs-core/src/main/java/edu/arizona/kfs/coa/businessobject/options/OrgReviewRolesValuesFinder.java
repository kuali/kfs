package edu.arizona.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import edu.arizona.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

public class OrgReviewRolesValuesFinder extends org.kuali.kfs.coa.businessobject.options.OrgReviewRolesValuesFinder {

	
	/**
     * Creates a list of {@link BasicAccountingCategory} with their code as the key and their code and description as the display
     * value
     */
	@Override
    public List getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));
        labels.add(new ConcreteKeyValue(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE, KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_TEXT));
        labels.add(new ConcreteKeyValue(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_CODE, KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_TEXT));
        labels.add(new ConcreteKeyValue(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_FUND_ONLY_CODE, KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_FUND_ONLY_TEXT));
        labels.add(new ConcreteKeyValue(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_BOTH_CODE, KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_BOTH_TEXT));
        return labels;
    }
}
