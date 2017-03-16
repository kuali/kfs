package edu.arizona.kfs.coa.document;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
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
	
	/**
     * Override the getSections method on this maintainable so that the document type name field
     * can be set to read-only for
     *
     * KRAD Conversion: Inquirable performs conditionally preparing the fields for different role modes
     * or to display/hide fields on the inquiry.
     * The field definitions are NOT declared in data dictionary.
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        OrgReviewRole orr = (OrgReviewRole)document.getNewMaintainableObject().getBusinessObject();

        String closestOrgReviewRoleParentDocumentTypeName = getOrgReviewRoleService().getClosestOrgReviewRoleParentDocumentTypeName(orr.getFinancialSystemDocumentTypeCode());
        boolean isFSTransDoc = StringUtils.equals( orr.getFinancialSystemDocumentTypeCode(), KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT)
                || StringUtils.equals( KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT, closestOrgReviewRoleParentDocumentTypeName);
        boolean hasAccountingOrganizationHierarchy = isFSTransDoc || getOrgReviewRoleService().hasAccountingOrganizationHierarchy(orr.getFinancialSystemDocumentTypeCode());

        boolean shouldReviewTypesFieldBeReadOnly = isFSTransDoc
                || getOrgReviewRoleService().hasOrganizationHierarchy(orr.getFinancialSystemDocumentTypeCode()) 
                || getOrgReviewRoleService().hasOrganizationFundReview(orr.getFinancialSystemDocumentTypeCode()) 
                || hasAccountingOrganizationHierarchy
                || (StringUtils.isNotBlank(closestOrgReviewRoleParentDocumentTypeName)
                        && StringUtils.equals(closestOrgReviewRoleParentDocumentTypeName, KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT));

        //If oldMaintainable is null, it means we are trying to get sections for the old part
        //If oldMaintainable is not null, it means we are trying to get sections for the new part
        //Refer to KualiMaintenanceForm lines 288-294
        if(oldMaintainable!=null){
            if(orr.isCreateRoleMember() || orr.isCopyRoleMember()){
                for (Section section : sections) {
                    for (Row row : section.getRows()) {
                        for (Field field : row.getFields()) {
                            prepareFieldsForCreateRoleMemberMode(field);
                            prepareFieldsCommon(field, shouldReviewTypesFieldBeReadOnly, hasAccountingOrganizationHierarchy );
                        }
                    }
                }
            } else if(orr.isDelegate() && (orr.isCopy() || StringUtils.isBlank( orr.getDelegationMemberId() )) ){
                for (Section section : sections) {
                    for (Row row : section.getRows()) {
                        for (Field field : row.getFields()) {
                            prepareFieldsForCreateDelegationMode(field);
                            prepareFieldsCommon(field, shouldReviewTypesFieldBeReadOnly, hasAccountingOrganizationHierarchy );
                        }
                    }
                }
            } else if(orr.isEditRoleMember()){
                for (Section section : sections) {
                    for (Row row : section.getRows()) {
                        for (Field field : row.getFields()) {
                            prepareFieldsForEditRoleMember(field);
                            prepareFieldsCommon(field, shouldReviewTypesFieldBeReadOnly, hasAccountingOrganizationHierarchy );
                        }
                    }
                }
            } else if(orr.isEditDelegation()){
                for (Section section : sections) {
                    for (Row row : section.getRows()) {
                        for (Field field : row.getFields()) {
                            prepareFieldsForEditDelegation(field);
                            prepareFieldsCommon(field, shouldReviewTypesFieldBeReadOnly, hasAccountingOrganizationHierarchy );
                        }
                    }
                }
            }
        } else if ( orr.isCreateRoleMember() || orr.isCopyRoleMember() || orr.isEditRoleMember()) {
            // If the member being edited is not a delegate, do not show the delegation type code
            for (Section section : sections) {
                for (Row row : section.getRows()) {
                    for (Field field : row.getFields()) {
                        if(OrgReviewRole.DELEGATION_TYPE_CODE.equals(field.getPropertyName())){
                            field.setFieldType(Field.HIDDEN);
                        }
                    }
                }
            }
        }
        return sections;
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
