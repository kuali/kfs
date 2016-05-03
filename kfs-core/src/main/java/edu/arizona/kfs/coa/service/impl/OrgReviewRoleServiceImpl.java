package edu.arizona.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.KfsKimDocumentAttributeData;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import edu.arizona.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;

public class OrgReviewRoleServiceImpl extends org.kuali.kfs.coa.service.impl.OrgReviewRoleServiceImpl {

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
