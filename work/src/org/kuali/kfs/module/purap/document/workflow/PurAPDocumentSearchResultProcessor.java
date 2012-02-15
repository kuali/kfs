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

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.KFSDocumentSearchResultProcessor;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.UnknownDocumentTypeException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.Column;

public class PurAPDocumentSearchResultProcessor extends KFSDocumentSearchResultProcessor {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurAPDocumentSearchResultProcessor.class);

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
        
        //need to update appDocStatus from docCriteriaDTO
        updateAppDocStatusForLookupResults(docSearchResult.getResultContainers(), docCriteriaDTO);
        
        //Mask the purapDocumentIdentifier field if the document is PO or POSP..
        if (KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER.equalsIgnoreCase(docCriteriaDTO.getDocTypeName()) ||
                KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_SPLIT.equalsIgnoreCase(docCriteriaDTO.getDocTypeName())) {
            //mask the PO Number if needed....
            maskPONumberForLookupResults(docSearchResult.getResultContainers(), docCriteriaDTO);
        }
        
        return docSearchResult;
    }
    
    /**
     * masks the value for the key where key is purapDocumentIdentifier.
     * 
     * @param keyValues
     * @param docCriteriaDTO
     */
    protected boolean maskPONumberForLookupResults(List<KeyValueSort> keyValues, DocSearchDTO docCriteriaDTO) {
        boolean masked = false;
        
        IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        
        for (KeyValueSort keyValueSort : keyValues) {
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
                        return true;
                    }
                }
            }
        }
        
        return masked;
    }
    
    /**
     * looks at the field "statusDescription" and retrieves and sets the 
     * appDocStatus value for display on the lookup results table.
     * 
     * @param keyValues
     */
    protected void updateAppDocStatusForLookupResults(List<KeyValueSort> keyValues, DocSearchDTO docCriteriaDTO) {
        for (KeyValueSort keyValueSort : keyValues) {
            if ("statusDescription".equalsIgnoreCase(keyValueSort.getkey())) {
                //need to get the appDocStatus set on routeHeaderid
                keyValueSort.setvalue(docCriteriaDTO.getAppDocStatus());
                keyValueSort.setSortValue(docCriteriaDTO.getAppDocStatus());
                return;
            }
        } 
    }
//    
//    /**
//     * with the document id the document is retrieved using document service and
//     * the appDocStatus is returned from the routeHeader.
//     * 
//     * @param keyValues
//     * @return appDocStatus from the routeHeader
//     */
//    protected String retrieveAppDocStatus(List<KeyValueSort> keyValues) {
//        String appDocStatus = "Not Available";
//        
//        for (KeyValueSort keyValueSort : keyValues) {
//            if (KEWPropertyConstants.ROUTE_HEADER_ID.equalsIgnoreCase(keyValueSort.getkey())) {
//                Document poDocument = findDocument(keyValueSort.getUserDisplayValue());
//                if (ObjectUtils.isNotNull(poDocument)) {
//                    return poDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus();
//                } else {
//                    return appDocStatus;
//                }
//            }
//        }
//        
//        return appDocStatus;
//    }
//    
//    /**
//     * This method finds the document for the given document header id
//     * @param documentHeaderId
//     * @return document The document in the workflow that matches the document header id.
//     */
//    protected Document findDocument(String documentHeaderId) {
//        Document document = null;
//        
//        try {
//            document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentHeaderId);
//        }
//        catch (WorkflowException ex) {
//            LOG.error("Exception encountered on finding the document: " + documentHeaderId, ex );
//        } catch ( UnknownDocumentTypeException ex ) {
//            // don't blow up just because a document type is not installed (but don't return it either)
//            LOG.error("Exception encountered on finding the document: " + documentHeaderId, ex );
//        }
//        
//        return document;
//    }
}
