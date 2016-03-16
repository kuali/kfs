package edu.arizona.kfs.coa.document;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;

import edu.arizona.kfs.sys.identity.KfsKimAttributes;

public class OrgReviewRoleMaintainableImpl extends org.kuali.kfs.coa.document.OrgReviewRoleMaintainableImpl {

	@Override
    public void prepareBusinessObject(BusinessObject businessObject){
        OrgReviewRole orr = (OrgReviewRole)businessObject;
        //Assuming that this is the condition when the document is loaded on edit or copy

        // The links on the lookup set barious variables, including the methodToCall on the "bo" class
        // and the delegate or role IDs being edited/copied

        if( (KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL.equals(orr.getMethodToCall()) ||
                KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL.equals(orr.getMethodToCall()))
                &&
                (StringUtils.isNotEmpty(orr.getODelMId()) || StringUtils.isNotEmpty(orr.getORMId())) ){
            // check if we have the information to create a delegation
            if(StringUtils.isNotEmpty(orr.getODelMId()) && !orr.isCreateDelegation()){
                getOrgReviewRoleService().populateOrgReviewRoleFromDelegationMember(orr, orr.getORMId(), orr.getODelMId());

                orr.setDelegate(true);
                if ( KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL.equals(orr.getMethodToCall()) ) {
                    orr.setDelegationMemberId("");
                }
            } else if(StringUtils.isNotEmpty(orr.getORMId())){
                getOrgReviewRoleService().populateOrgReviewRoleFromRoleMember(orr, orr.getORMId());

                if(orr.isCreateDelegation()) {
                    orr.setDelegate(true);
                    if ( KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL.equals(orr.getMethodToCall()) ) {
                        orr.setDelegationMemberId("");
                    }
                } else {
                    orr.setDelegate(false);
                    if ( KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL.equals(orr.getMethodToCall()) ) {
                        orr.setRoleMemberId("");
                    }
                }
            }
            // blank these out, since they are flags to init the object
            orr.setORMId("");
            orr.setODelMId("");
            
            orr.setFundGroupCode(orr.getAttributeValue(KfsKimAttributes.FUND_GROUP_CODE));
            orr.setSubFundGroupCode(orr.getAttributeValue(KfsKimAttributes.SUB_FUND_GROUP_CODE));
            orr.setFinancialObjectSubTypeCode(orr.getAttributeValue(KfsKimAttributes.OBJECT_SUB_TYPE_CODE));
        }
        setBusinessObject(orr);
    }
	
	@Override
	protected void prepareFieldsCommon(Field field, boolean shouldReviewTypesFieldBeReadOnly, boolean hasAccountingOrganizationHierarchy){
        if ( field == null ) {
            throw new IllegalArgumentException( "The Field parameter may not be null." );
        }

        if(!shouldReviewTypesFieldBeReadOnly) {
            return; // nothing to make read only
        }

        if(OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME.equals(field.getPropertyName())) {
            field.setReadOnly(true);
        } else if( !hasAccountingOrganizationHierarchy
                && (OrgReviewRole.FROM_AMOUNT_FIELD_NAME.equals(field.getPropertyName()) ||
                        OrgReviewRole.TO_AMOUNT_FIELD_NAME.equals(field.getPropertyName()) ||
                        OrgReviewRole.FUND_GROUP_FIELD_NAME.equals(field.getPropertyName()) ||
                        OrgReviewRole.SUB_FUND_GROUP_FIELD_NAME.equals(field.getPropertyName()) ||
                        OrgReviewRole.OBJECT_SUB_TYPE_FIELD_NAME.equals(field.getPropertyName()) ||
                        OrgReviewRole.OVERRIDE_CODE_FIELD_NAME.equals(field.getPropertyName()))) {
            field.setReadOnly(true);
        }
    }
	
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
