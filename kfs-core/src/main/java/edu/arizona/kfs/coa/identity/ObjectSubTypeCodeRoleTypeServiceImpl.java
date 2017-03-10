package edu.arizona.kfs.coa.identity;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;
import edu.arizona.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;

public class ObjectSubTypeCodeRoleTypeServiceImpl extends RoleTypeServiceBase {
    private DocumentTypeService documentTypeService;
    
    @Override
    protected boolean performMatch(Map<String, String> qualification, Map<String, String> roleQualifier) {    
        if (KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, KfsKimAttributes.OBJECT_SUB_TYPE_CODE)) {
            Set<String> potentialParentDocumentTypeNames = new HashSet<String>(1);
            if (roleQualifier.containsKey(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) {
                potentialParentDocumentTypeNames.add(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
            }
            return potentialParentDocumentTypeNames.isEmpty() 
                || qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME).equalsIgnoreCase(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) 
                || (KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().getDocumentTypeByName(qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)), potentialParentDocumentTypeNames) != null);
        }
        return false;
    }
    
    public DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
        	documentTypeService = SpringContext.getBean(DocumentTypeService.class); 
        }
        return this.documentTypeService;
    }

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#getAttributeDefinitions(java.lang.String)
     */
    @Override
    public List<KimAttributeField> getAttributeDefinitions(String kimTypId) {
    	List<KimAttributeField> fields = super.getAttributeDefinitions(kimTypId);
    	List<KimAttributeField> updatedFields = new ArrayList<KimAttributeField>( fields.size() );
        
        for (KimAttributeField definition : fields) {
            if (KfsKimAttributes.OBJECT_SUB_TYPE_CODE.equals(definition.getAttributeField().getName())) {
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

