/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.workflow;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.KFSDocumentSearchResultProcessor;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.SearchAttributeCriteriaComponent;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchResultProcessor;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.SearchingAttribute;
import org.kuali.rice.kns.datadictionary.SearchingTypeDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.ui.Column;

public class PurAPDocumentSearchResultProcessor extends KFSDocumentSearchResultProcessor {

    /**
     * Customizes the result set for purap document identified attribute value.  After getting the 
     * customized result set, if key exists for purapDocumentIdentifier, then check the permission for
     * the principal id.  If the permission exists and document status is FINAL, then unmask the field value else
     * mask field with ********. 
     * 
     * @see org.kuali.rice.kew.docsearch.StandardDocumentSearchResultProcessor#generateSearchResult(org.kuali.rice.kew.docsearch.DocSearchDTO, java.util.List)
     */
    @Override
    public DocumentSearchResult generateSearchResult(DocSearchDTO docCriteriaDTO, List<Column> columns) {
        DocumentSearchResult docSearchResult = super.generateSearchResult(docCriteriaDTO, columns);
        
        //do not mask the purapDocumentIdentifier field if the document is not PO or POSP..
        if (!KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER.equalsIgnoreCase(docCriteriaDTO.getDocTypeName()) &&
                !KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_SPLIT.equalsIgnoreCase(docCriteriaDTO.getDocTypeName())) {
            return docSearchResult;
        }
        
        IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        
        for (KeyValueSort keyValueSort : docSearchResult.getResultContainers()) {
            if (keyValueSort.getkey().equalsIgnoreCase(KFSPropertyConstants.PURAP_DOC_ID)) {
                //KFSMI-4576 masking PO number...
                String docStatus = docCriteriaDTO.getDocRouteStatusCode();
                if (!docStatus.equalsIgnoreCase(KEWConstants.ROUTE_HEADER_FINAL_CD)) {
                    //if document status is not FINAL then check for permission to see
                    //the value needs to be masked....
                    String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
                    String namespaceCode = KFSConstants.ParameterNamespaces.KNS;
                    String permissionTemplateName = KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD;
                    
                    AttributeSet roleQualifiers = new AttributeSet();
                    
                    AttributeSet permissionDetails = new AttributeSet();
                    permissionDetails.put(KfsKimAttributes.COMPONENT_NAME, KFSPropertyConstants.PURCHASE_ORDER_DOCUMENT_SIMPLE_NAME);
                    permissionDetails.put(KfsKimAttributes.PROPERTY_NAME, KFSPropertyConstants.PURAP_DOC_ID);
                    
                    Boolean isAuthorized = identityManagementService.isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, roleQualifiers);
                    //the principalId is not authorized to view the value in purapDocumentIdentifier field...so mask the value...
                    if (!isAuthorized) {
                        //not authorized to see... create a string 
                        keyValueSort.setvalue("********");
                        keyValueSort.setSortValue("********");
                    }
                }
            }
        }
        
        return docSearchResult;
    }
}
