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

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurApGenericAttributes;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.KFSDocumentSearchResultProcessor;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.UnknownDocumentTypeException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.ui.Column;

public class PurAPDocumentSearchResultProcessor extends KFSDocumentSearchResultProcessor {
    @Override
    public DocumentSearchResultComponents processIntoFinalResults(
            List<DocSearchDTO> docSearchResultRows,
            DocSearchCriteriaDTO criteria, String principalId) {
        DocumentSearchResultComponents searchResultComponents = super.processIntoFinalResults(docSearchResultRows, criteria, principalId);
        
        String docStatus = "";
        String namespaceCode = KFSConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE;
        String permissionName = KFSConstants.PermissionNames.FULL_UNMASK_FIELD;

        IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        Boolean isAuthorized = identityManagementService.hasPermission(principalId, namespaceCode, permissionName, null);
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        
        String documentHeaderId = criteria.getRouteHeaderId();
        Document document = findDocument(documentHeaderId);
        docStatus = document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus();
        
        for (DocSearchDTO searchResultRow : docSearchResultRows) {
            for (KeyValueSort keyValueSort : searchResultRow.getSearchableAttributes()) {
                if (keyValueSort.getkey().equalsIgnoreCase("purapDocumentIdentifier")) {
                    //KFSMI-4576 masking PO number...
                    if (!docStatus.equalsIgnoreCase(KEWConstants.ROUTE_HEADER_FINAL_CD) &&
                            !isAuthorized) {
                        String poIDstr = "";
                        int strLength = dataDictionaryService.getAttributeMaxLength(PurApGenericAttributes.class.getName(), "purapDocumentIdentifier");
                        for (int i = 0; i < strLength; i++) {
                            poIDstr = poIDstr.concat("*");
                        }
                        
                        keyValueSort.setvalue(poIDstr);
                        keyValueSort.setSortValue(poIDstr);
                    }
                }
            }
        }
        
        return searchResultComponents;
    }
    
    /**
     * This method finds the document for the given document header id
     * @param documentHeaderId
     * @return document The document in the workflow that matches the document header id.
     */
    protected Document findDocument(String documentHeaderId) {
        Document document = null;
        
        try {
            document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentHeaderId);
        }
        catch (WorkflowException ex) {
            throw new RuntimeException("Exception encountered on finding the document: " + documentHeaderId, ex);
        } catch ( UnknownDocumentTypeException ex ) {
            // don't blow up just because a document type is not installed (but don't return it either)
            throw new RuntimeException("Exception encountered on finding the document: " + documentHeaderId, ex);
        }
        
        return document;
    }
    
    @Override
    public DocumentSearchResult generateSearchResult(DocSearchDTO docCriteriaDTO, List<Column> columns) {
        DocumentSearchResult docSearchResult = super.generateSearchResult(docCriteriaDTO, columns);
        
        String docStatus = docCriteriaDTO.getDocRouteStatusCode();
        String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        String namespaceCode = KFSConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE;
        String permissionName = KFSConstants.PermissionNames.FULL_UNMASK_FIELD;

        IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        Boolean isAuthorized = identityManagementService.hasPermission(principalId, namespaceCode, permissionName, null);
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        
        for (KeyValueSort keyValueSort : docSearchResult.getResultContainers()) {
            if (keyValueSort.getkey().equalsIgnoreCase("purapDocumentIdentifier")) {
                //KFSMI-4576 masking PO number...
                if (!docStatus.equalsIgnoreCase(KEWConstants.ROUTE_HEADER_FINAL_CD) &&
                        !isAuthorized) {
                    String poIDstr = "";
                    int strLength = dataDictionaryService.getAttributeMaxLength(PurApGenericAttributes.class.getName(), "purapDocumentIdentifier");
                    for (int i = 0; i < strLength; i++) {
                        poIDstr = poIDstr.concat("*");
                    }
                    
                    keyValueSort.setvalue(poIDstr);
                    keyValueSort.setSortValue(poIDstr);
                }
            }
        }
        
        return docSearchResult;
    }
}
