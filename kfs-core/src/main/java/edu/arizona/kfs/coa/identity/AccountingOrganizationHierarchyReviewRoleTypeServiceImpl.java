package edu.arizona.kfs.coa.identity;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import edu.arizona.kfs.sys.identity.KfsKimAttributes;

public class AccountingOrganizationHierarchyReviewRoleTypeServiceImpl extends org.kuali.kfs.coa.identity.AccountingOrganizationHierarchyReviewRoleTypeServiceImpl {

	@Override
	public boolean performMatch(Map<String, String> qualification, Map<String, String> roleQualifier) {
		return doesFundGroupMatch(qualification, roleQualifier)
				&& doesSubFundGroupMatch(qualification, roleQualifier)
				&& doesObjectSubTypeMatch(qualification, roleQualifier)
				&& super.performMatch(qualification, roleQualifier);
	}

	protected boolean doesFundGroupMatch(Map<String, String> qualification, Map<String, String> roleQualifier) {
		if (roleQualifier == null || StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.FUND_GROUP_CODE))) {
			return true;
		}
		String roleFundGroup = roleQualifier.get(KfsKimAttributes.FUND_GROUP_CODE);
		
		if (qualification == null) {
			return false;
		}
		String documentFundGroup = qualification.get(KfsKimAttributes.FUND_GROUP_CODE);
		
		return StringUtils.equals(roleFundGroup, documentFundGroup);
	}
	
	protected boolean doesSubFundGroupMatch(Map<String, String> qualification, Map<String, String> roleQualifier) {
		if (roleQualifier == null || StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.SUB_FUND_GROUP_CODE))) {
			return true;
		}
		String roleSubFund = roleQualifier.get(KfsKimAttributes.SUB_FUND_GROUP_CODE);
		
		if (qualification == null) {
			return false;
		}
		String documentSubFund = qualification.get(KfsKimAttributes.SUB_FUND_GROUP_CODE);
		
		if (StringUtils.equals(roleSubFund, documentSubFund)) {
			return true;
		} else {
			if (StringUtils.contains(roleSubFund, '*')) {
				String subFundPattern = StringUtils.replace(roleSubFund, "*", ".*");
				return documentSubFund.matches(subFundPattern);
			}
		}
		return false;
	}
	
	protected boolean doesObjectSubTypeMatch(Map<String, String> qualification, Map<String, String> roleQualifier) {
		if (roleQualifier == null || StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.OBJECT_SUB_TYPE_CODE))) {
			return true;
		}
		String roleObjectSubType = roleQualifier.get(KfsKimAttributes.OBJECT_SUB_TYPE_CODE);
		
		if (qualification == null) {
			return false;
		}
		String documentObjectSubType = qualification.get(KfsKimAttributes.OBJECT_SUB_TYPE_CODE);
		
		return StringUtils.equals(roleObjectSubType, documentObjectSubType);
    }
}
