package edu.arizona.kfs.coa.identity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.OrganizationHierarchyAwareRoleTypeServiceBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.util.KimCommonUtils;

import edu.arizona.kfs.sys.identity.KfsKimAttributes;

public class OrganizationFundReviewRoleTypeServiceImpl extends OrganizationHierarchyAwareRoleTypeServiceBase {

	private DocumentTypeService documentTypeService;

    /**
     * Attributes: Chart Code (required) Organization Code Document Type Name Requirement and either the Fund or SubFund - 
     * Traverse the org hierarchy but not the document type hierarchy
     */
    @Override
    protected boolean performMatch(Map<String, String> qualification, Map<String, String> roleQualifier) {
        if (isParentOrg(qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), qualification.get(KfsKimAttributes.ORGANIZATION_CODE), roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE), true)) {
            if (doesFundGroupMatch(qualification, roleQualifier) && doesSubFundGroupMatch(qualification, roleQualifier)) {
                Set<String> potentialParentDocumentTypeNames = new HashSet<String>();
                if (roleQualifier.containsKey(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) {
                    potentialParentDocumentTypeNames.add(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
                }
                return potentialParentDocumentTypeNames.isEmpty() 
                        || StringUtils.equalsIgnoreCase( qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME), roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) 
                        || (KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().getDocumentTypeByName(qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)), potentialParentDocumentTypeNames) != null);
            }             
        }
        return false;
    }
    
    /**
     * comparison for role FG vs. document FG
     */
    protected boolean doesFundGroupMatch(Map<String, String> qualification, Map<String, String> roleQualifier) {
        if (roleQualifier == null || StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.FUND_GROUP_CODE))) {
            return true;
        }

        String roleFundGroup = roleQualifier.get(KfsKimAttributes.FUND_GROUP_CODE);        

        if (qualification == null) {
            return false;
        }
        String documentFundGroup = qualification.get(KfsKimAttributes.FUND_GROUP_CODE);

        // check for an exact match
        return StringUtils.equals(roleFundGroup, documentFundGroup);
    }

    /**
     * comparison for role SFG vs. document SFG
     */
    protected boolean doesSubFundGroupMatch(Map<String, String> qualification, Map<String, String> roleQualifier) {
        if (roleQualifier == null || StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.SUB_FUND_GROUP_CODE))) {
            return true;
        }
        String roleSubFund = roleQualifier.get(KfsKimAttributes.SUB_FUND_GROUP_CODE);        
        
        if (qualification == null) {
            return false;
        }
        String documentSubFund = qualification.get(KfsKimAttributes.SUB_FUND_GROUP_CODE);

        // check for an exact match
        if ( StringUtils.equals(roleSubFund, documentSubFund) ) {
            return true;
        } else {
        	// if contains an "*", convert to regex and compare again
            if (StringUtils.contains(roleSubFund, '*')) {
                String subFundPattern = StringUtils.replace(roleSubFund, "*", ".*");
                return documentSubFund.matches(subFundPattern);
            }
        }        
        return false;
    }
    
    public DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        }
        return this.documentTypeService;
    }
    
    @Override
    public List<KimAttributeField> getAttributeDefinitions(String kimTypeId) {
    	List<KimAttributeField> fields = super.getAttributeDefinitions(kimTypeId);
    	List<KimAttributeField> updatedFields = new ArrayList<KimAttributeField>(fields.size());
    	
        for (KimAttributeField definition : fields) {      	
        	//if field is organization code, fund group code, sub fund group code or object sub type code
            if (KfsKimAttributes.ORGANIZATION_CODE.equals(definition.getAttributeField().getName()) || StringUtils.equals(definition.getAttributeField().getName(), KfsKimAttributes.FUND_GROUP_CODE)
                    || StringUtils.equals(definition.getAttributeField().getName(), KfsKimAttributes.SUB_FUND_GROUP_CODE) || StringUtils.equals(definition.getAttributeField().getName(), KfsKimAttributes.OBJECT_SUB_TYPE_CODE)) {
            	KimAttributeField.Builder updatedField = KimAttributeField.Builder.create(definition);
            	updatedField.getAttributeField().setRequired(false);
            	updatedFields.add(updatedField.build());
            } else {
            	updatedFields.add(definition);
            }
        }
        return fields;
    }
}