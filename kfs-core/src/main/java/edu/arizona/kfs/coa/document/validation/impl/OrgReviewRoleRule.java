package edu.arizona.kfs.coa.document.validation.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import edu.arizona.kfs.sys.KFSConstants;
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
		
		for(String roleName : orr.getRoleNamesToConsider()) {
			if(StringUtils.equals(roleName, KFSConstants.SysKimApiConstants.ORGANIZATION_FUND_REVIEWER_ROLE_NAME) && StringUtils.isBlank(orr.getFundGroupCode()) && StringUtils.isBlank(orr.getSubFundGroupCode())) {
				putFieldError(KfsKimAttributes.SUB_FUND_GROUP_CODE, "error.member.fundgroup.subfundgroup.onerequired");
				valid = false;
			}
		}
		return valid;
	}
	
	@Override
    protected boolean validateRoleMember(OrgReviewRole orr){
        boolean valid = true;
        if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
            if (orr.getPerson() == null) {
                putFieldError(OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME, "error.document.orgReview.invalidPrincipal"
                        , getDataDictionaryService().getAttributeLabel(OrgReviewRole.class, OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME) );
                valid = false;
            }
        }
        if(StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
            if ( StringUtils.equals( KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME, orr.getRoleMemberRoleName())
                    || StringUtils.equals( KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME, orr.getRoleMemberRoleName() )
                    || StringUtils.equals(KFSConstants.SysKimApiConstants.ORGANIZATION_FUND_REVIEWER_ROLE_NAME, orr.getRoleMemberRoleName())) {
                putFieldError(OrgReviewRole.ROLE_NAME_FIELD_NAME, "error.document.orgReview.recursiveRole" );
            } else {
                if(orr.getRole() == null){
                    putFieldError(OrgReviewRole.ROLE_NAME_FIELD_NAME, "error.document.orgReview.invalidRole"
                            , new String[] {
                                      getDataDictionaryService().getAttributeLabel(OrgReviewRole.class, OrgReviewRole.ROLE_NAME_FIELD_NAME)
                                    , getDataDictionaryService().getAttributeLabel(OrgReviewRole.class, OrgReviewRole.ROLE_NAME_FIELD_NAMESPACE_CODE)
                                    } );
                    valid = false;
                }
            }
        }
        if(StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
            if( orr.getGroup() == null ){
                putFieldError(OrgReviewRole.GROUP_NAME_FIELD_NAME, "error.document.orgReview.invalidGroup"
                        , new String[] {
                                  getDataDictionaryService().getAttributeLabel(OrgReviewRole.class, OrgReviewRole.GROUP_NAME_FIELD_NAME)
                                , getDataDictionaryService().getAttributeLabel(OrgReviewRole.class, OrgReviewRole.GROUP_NAME_FIELD_NAMESPACE_CODE)
                                } );
                valid = false;
            }
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
