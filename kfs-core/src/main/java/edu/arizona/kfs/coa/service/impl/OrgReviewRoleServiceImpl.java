package edu.arizona.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.KfsKimDocumentAttributeData;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import edu.arizona.kfs.sys.KFSConstants;

import edu.arizona.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.krad.exception.ValidationException;
import org.springframework.cache.annotation.Cacheable;

public class OrgReviewRoleServiceImpl extends org.kuali.kfs.coa.service.impl.OrgReviewRoleServiceImpl {

	/**
     *  1. Check WorkflowInfo.hasNode(documentTypeName, nodeName) to see if the document type selected has
     *  OrganizationHierarchy and/or AccountingOrganizationHierarchy - if it has either or both,
     *  set the Review Types radio group appropriately and make it read only.
     *  2. Else, if KFS is the document type selected, set the Review Types radio group to both and leave it editable.
     *  3. Else, if FinancialSystemTransactionalDocument is the closest parent (per KimCommonUtils.getClosestParent),
     *  set the Review Types radio group to Organization Accounting Only and leave it editable.
     *  4. Else, if FinancialSystemComplexMaintenanceDocument is the closest parent (per KimCommonUtils.getClosestParent),
     *  set the Review Types radio group to Organization Only and make read-only.
     *  5. Else, if FinancialSystemSimpleMaintenanceDocument is the closest parent (per KimCommonUtils.getClosestParent),
     *  this makes no sense and should generate an error.
     * @param documentTypeName
     * @param hasOrganizationHierarchy
     * @param hasOrganizationFundReview
     * @param hasAccountingOrganizationHierarchy
     * @param closestParentDocumentTypeName
     * @return
     */
    @Override
    @Cacheable(value=OrgReviewRole.CACHE_NAME,key="'{getRolesToConsider}'+#p0")
    public List<String> getRolesToConsider(String documentTypeName) throws ValidationException {
        List<String> rolesToConsider = new ArrayList<String>();
        if(StringUtils.isBlank(documentTypeName) || KFSConstants.ROOT_DOCUMENT_TYPE.equals(documentTypeName) ){
            rolesToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
            rolesToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            rolesToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_FUND_REVIEWER_ROLE_NAME);
        } else {
            String closestParentDocumentTypeName = getClosestOrgReviewRoleParentDocumentTypeName(documentTypeName);
            if(documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT)
                    || KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT.equals(closestParentDocumentTypeName)) {
                rolesToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            } else {
            	boolean hasOrganizationFundReview = hasOrganizationFundReview(documentTypeName);
                if(hasOrganizationFundReview) {
                	rolesToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_FUND_REVIEWER_ROLE_NAME);
                } else {
                	boolean hasOrganizationHierarchy = hasOrganizationHierarchy(documentTypeName);
                	boolean hasAccountingOrganizationHierarchy = hasAccountingOrganizationHierarchy(documentTypeName);
                	if(hasOrganizationHierarchy || documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT)
                			|| KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT.equals(closestParentDocumentTypeName) ) {
                		rolesToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
                	}
                	if(hasAccountingOrganizationHierarchy) {
                		rolesToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
                	}
                }
            }
        }
        return rolesToConsider;
    }
    
    @Override
    protected List<String> getRolesToSaveFor(List<String> roleNamesToConsider, String reviewRolesIndicator){
        if(roleNamesToConsider!=null){
            List<String> roleToSaveFor = new ArrayList<String>();
            if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE.equals(reviewRolesIndicator)){
                roleToSaveFor.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            } else if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_CODE.equals(reviewRolesIndicator)){
                roleToSaveFor.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
            } else if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_FUND_ONLY_CODE.equals(reviewRolesIndicator)) {
            	roleToSaveFor.add(KFSConstants.SysKimApiConstants.ORGANIZATION_FUND_REVIEWER_ROLE_NAME);
            } else{
                roleToSaveFor.addAll(roleNamesToConsider);
            }
            return roleToSaveFor;
        } else {
            return Collections.emptyList();
        }
    }
    
	@Override
	protected Map<String,String> getAttributes(OrgReviewRole orr, String kimTypeId){
        if( StringUtils.isBlank(kimTypeId) ) {
            return Collections.emptyMap();
        }

        List<KfsKimDocumentAttributeData> attributeDataList = new ArrayList<KfsKimDocumentAttributeData>();
        KfsKimDocumentAttributeData attributeData = getAttribute(kimTypeId, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, orr.getChartOfAccountsCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.ORGANIZATION_CODE, orr.getOrganizationCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }
        
        attributeData = getAttribute(kimTypeId, KfsKimAttributes.FUND_GROUP_CODE, orr.getFundGroupCode());
        if ( attributeData != null) {
        	attributeDataList.add(attributeData);
        }
        
        attributeData = getAttribute(kimTypeId, KfsKimAttributes.SUB_FUND_GROUP_CODE, orr.getSubFundGroupCode());
        if ( attributeData != null) {
        	attributeDataList.add(attributeData);
        }
        
        attributeData = getAttribute(kimTypeId, KfsKimAttributes.OBJECT_SUB_TYPE_CODE, orr.getFinancialObjectSubTypeCode());
        if ( attributeData != null) {
        	attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, orr.getFinancialSystemDocumentTypeCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE, orr.getOverrideCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.FROM_AMOUNT, orr.getFromAmountStr());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.TO_AMOUNT, orr.getToAmountStr());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        return orr.getQualifierAsAttributeSet(attributeDataList);
    }
}
