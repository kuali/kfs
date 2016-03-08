package edu.arizona.kfs.coa.document.validation.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;

import edu.arizona.kfs.sys.identity.KfsKimAttributes;

public class OrgReviewRoleRule extends org.kuali.kfs.coa.document.validation.impl.OrgReviewRoleRule {

	@Override
	protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
		boolean valid = super.processCustomRouteDocumentBusinessRules(document);
	    OrgReviewRole orr = (OrgReviewRole)document.getNewMaintainableObject().getBusinessObject();
	    valid &= validateFundGroupSubFundGroupCode(orr);
	    return valid;
	}
	
	protected boolean validateFundGroupSubFundGroupCode(OrgReviewRole orr) {
		boolean valid = true;
		if(!StringUtils.isBlank(orr.getFundGroupCode()) && !StringUtils.isBlank(orr.getSubFundGroupCode())) {
			putFieldError(OrgReviewRole.SUB_FUND_GROUP_FIELD_NAME, "error.member.fundgroup.subfundgroup.bothentered");
			valid = false;
		}
		return valid;
	}
	
	@Override
	protected boolean doAttributesMatch(OrgReviewRole orr, Map<String,String> attributeSet){
        String docTypeName = orr.getFinancialSystemDocumentTypeCode();
        String chartOfAccountCode = orr.getChartOfAccountsCode();
        String organizationCode = orr.getOrganizationCode();
        return     StringUtils.equals(docTypeName, attributeSet.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME))
                && StringUtils.equals(chartOfAccountCode, attributeSet.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE))
                && StringUtils.equals(organizationCode, attributeSet.get(KfsKimAttributes.ORGANIZATION_CODE))
                && StringUtils.equals(orr.getFundGroupCode(), attributeSet.get(KfsKimAttributes.FUND_GROUP_CODE))
                && StringUtils.equals(orr.getSubFundGroupCode(), attributeSet.get(KfsKimAttributes.SUB_FUND_GROUP_CODE))
                && StringUtils.equals(orr.getFinancialObjectSubTypeCode(), attributeSet.get(KfsKimAttributes.OBJECT_SUB_TYPE_CODE));
    }
	
}
