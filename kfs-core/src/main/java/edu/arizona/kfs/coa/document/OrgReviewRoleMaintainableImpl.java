package edu.arizona.kfs.coa.document;

import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.rice.kns.web.ui.Field;

public class OrgReviewRoleMaintainableImpl extends org.kuali.kfs.coa.document.OrgReviewRoleMaintainableImpl {

	@Override
	protected void setCommonFieldsToReadOnlyOnEdit(Field field) {
		  if(OrgReviewRole.CHART_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
	                OrgReviewRole.ORG_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
	                OrgReviewRole.FUND_GROUP_FIELD_NAME.equals(field.getPropertyName()) ||
	                OrgReviewRole.SUB_FUND_GROUP_FIELD_NAME.equals(field.getPropertyName()) ||
	                OrgReviewRole.OBJECT_SUB_TYPE_FIELD_NAME.equals(field.getPropertyName()) ||
	                OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
	                OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME.equals(field.getPropertyName()) ||
	                OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
	                OrgReviewRole.ROLE_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
	                OrgReviewRole.ROLE_NAME_FIELD_NAMESPACE_CODE.equals(field.getPropertyName()) ||
	                OrgReviewRole.GROUP_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
	                OrgReviewRole.GROUP_NAME_FIELD_NAMESPACE_CODE.equals(field.getPropertyName())){
	            field.setReadOnly(true);
	        }
		
	}

	@Override
	protected void prepareFieldsForCreateDelegationMode(Field field){
        //TODO: in prepareBusinessObject, populate these fields for create delegation
        if(OrgReviewRole.CHART_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.ORG_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.FUND_GROUP_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.SUB_FUND_GROUP_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.OBJECT_SUB_TYPE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.ACTION_POLICY_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.ACTION_TYPE_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.PRIORITY_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.FORCE_ACTION_FIELD_NAME.equals(field.getPropertyName())){
            field.setReadOnly(true);
        }
    }
	
}
