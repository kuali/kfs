/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.rice.kns.workflow.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.WorkflowAttributePropertyResolutionService;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.role.QualifierResolver;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.RoutingTypeDefinition;
import org.kuali.rice.kns.datadictionary.WorkflowAttributes;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;

/**
 * QualifierResolver which uses Data Dictionary defined workflow attributes to gather a collection
 * of qualifiers to use to determine the responsibility for a document at a given workflow route node.
 * 
 * WorkflowAttributes can be defined in the data dictionary like so:
 * 
 * <dd:workflowAttributes id="InternalBillingDocument-workflowAttributes-parentBean" abstract="true">
 *   <dd:searchingType name="Chart" >
 *     <dd:searchingAttribute businessObjectClassName="org.kuali.kfs.coa.businessobject.Chart" attributeName="chartOfAccountsCode" />
 *     <dd:documentValue path="sourceAccountingLines.chartOfAccountsCode" />
 *     <dd:documentValue path="targetAccountingLines.chartOfAccountsCode" />
 *   </dd:searchingType>
 *   <dd:searchingType name="Account" >
 *     <dd:searchingAttribute businessObjectClassName="org.kuali.kfs.coa.businessobject.Account" attributeName="accountNumber" />
 *     <dd:documentValue path="sourceAccountingLines.accountNumber" />
 *     <dd:documentValue path="targetAccountingLines.accountNumber" />
 *  </dd:searchingType>
 *  <dd:routingType name="AccountingOrgReview">
 *     <dd:routingAttributes>
 *       <dd:routingAttribute businessObjectClassName="org.kuali.kfs.sys.businessobject.AccountingDocument" attributeName="totalAmount" />
 *       <dd:routingAttribute businessObjectClassName="org.kuali.kfs.coa.businessobject.Chart" attributeName="chartOfAccountsCode" />
 *       <dd:routingAttribute businessObjectClassName="org.kuali.kfs.coa.businessobject.Org" attributeName="organizationCode" />
 *       <dd:routingAttribute businessObjectClassName="org.kuali.kfs.sys.businessobject.AccountingLine" attributeName="overrideCode" />
 *     </dd:routingAttributes>
 *     <dd:documentValuePathGroup>
 *       <dd:documentValue path="totalAmount"/>
 *       <dd:documentCollectionPath path="items">
 *         <dd:documentValue path="amount" />
 *         <dd:documentCollectionPath path="sourceAccountingLines">
 *           <dd:documentValue path="chartsOfAccountCode"/>
 *           <dd:documentValue path="account.organizationCode"/>
 *           <dd:documentValue path="overrideCode"/>
 *         </dd:documentCollectionPath>
 *       </dd:documentCollectionPath>
 *     </dd:documentValuePathGroup>
 *     <dd:documentValuePathGroup>
 *       <dd:documentValue path="totalAmount"/>
 *       <dd:documentCollectionPath path="items">
 *         <dd:documentValue path="amount" />
 *         <dd:documentCollectionPath path="targetAccountingLines">
 *           <dd:documentValue path="chartsOfAccountCode"/>
 *             <dd:documentValue path="account.organizationCode"/>
 *             <dd:documentValue path="overrideCode"/>
 *           </dd:documentCollectionPath>
 *         </dd:documentCollectionPath>
 *      </dd:documentValuePathGroup>
 *   </dd:routingType>
 * </dd:workflowAttributes> 
 * 
 * This means that at the "AccountReview" node, a collection of chartOfAccountsCode and accountNumber combinations will be returned
 * as qualifiers; at the "OrganizationReview" node, there will be a collection of chartOfAccountsCode and organizationCode combinations.
 * These qualifiers will be used by workflow to route the document to only the qualified members of the
 * role associated with the responsibility.
 */
public class DataDictionaryQualifierResolver implements QualifierResolver {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DataDictionaryQualifierResolver.class);
    
    private static final String KIM_ATTRIBUTE_DOCUMENT_TYPE_NAME = KimAttributes.DOCUMENT_TYPE_NAME;
    private static final String KIM_ATTRIBUTE_DOCUMENT_NUMBER = KimAttributes.DOCUMENT_NUMBER;
    private static final String KIM_ATTRIBUTE_ROUTE_LEVEL_NAME = KimAttributes.ROUTE_NODE_NAME;

    /**
     * Given the RouteContext, determines the document type of the document being routed and the current
     * route nodes; generates a List of qualifier AttributeSets based on the the contents of the document. 
     * @see org.kuali.rice.kew.role.QualifierResolver#resolve(org.kuali.rice.kew.engine.RouteContext)
     */
    public List<AttributeSet> resolve(RouteContext context) {
        final String routeLevel = context.getNodeInstance().getName();
        final DocumentEntry documentEntry = getDocumentEntry(context);
        final RoutingTypeDefinition routingTypeDefinition = getWorkflowAttributeDefintion(documentEntry, routeLevel);
        final Document document = getDocument(context, documentEntry.getDocumentClass());
        List<AttributeSet> qualifiers = null;
        
        if (document != null && routingTypeDefinition != null) {
            document.populateDocumentForRouting();
            qualifiers = SpringContext.getBean(WorkflowAttributePropertyResolutionService.class).resolveRoutingTypeQualifiers(document, routingTypeDefinition);
        } else {
            qualifiers = new ArrayList<AttributeSet>();
            AttributeSet basicQualifier = new AttributeSet();
            qualifiers.add(basicQualifier);
        }
        decorateWithCommonQualifiers(qualifiers, document, documentEntry, routeLevel);
        return qualifiers;
    }

    /**
     * Retrieves the data dictionary entry for the document being operated on by the given route context
     * @param context the current route context
     * @return the data dictionary document entry
     */
    protected DocumentEntry getDocumentEntry(RouteContext context) {
        return SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(context.getDocument().getDocumentType().getName());
    }
    
    /**
     * Retrieves the document that the current route context is operating on
     * @param context the current route context
     * @return the document
     */
    protected Document getDocument(RouteContext context, Class<? extends Document> documentClass) {
        String documentID = getDocumentId(context);
        
        Map<String, String> documentNumberCriteria = new HashMap<String, String>();
        documentNumberCriteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentID);
        if (documentID != null) {
            try {
                return SpringContext.getBean(DocumentService.class).getByDocumentHeaderIdSessionless(documentID);
            }
            catch (WorkflowException e) {
                LOG.error("Unable to retrieve document with system user.", e);
                return null;
            }
        }
        return null;
    }
    
    /**
     * Retrieves the id of the current document from the RouteContext
     * @param context the current route context
     * @return the id of the document
     */
    protected String getDocumentId(RouteContext context) {
        final Long documentID = context.getNodeInstance().getDocumentId();
        return documentID != null ? documentID.toString() : null;
    }

    /**
     * Retrieves the proper List of WorkflowAttributes for the given route level from the data dictionary
     * document entry
     * @param documentEntry the data dictionary document entry for the currently routed document
     * @param routeLevelName the name of the route level
     * @return a WorkflowAttributeDefinition if one could be found for the route level; otherwise, nothing
     */
    protected RoutingTypeDefinition getWorkflowAttributeDefintion(DocumentEntry documentEntry, String routeLevelName) {
       final WorkflowAttributes workflowAttributes = documentEntry.getWorkflowAttributes();
       if ( workflowAttributes == null ) {
           return null;
       }
       final Map<String, RoutingTypeDefinition> routingTypeMap = workflowAttributes.getRoutingTypeDefinitions();
       if (routingTypeMap.containsKey(routeLevelName)) return routingTypeMap.get(routeLevelName);
       return null;
    }
    
    /**
     * Add common qualifiers to every AttributeSet in the given List of AttributeSet
     * @param qualifiers a List of AttributeSets to add common qualifiers to
     * @param document the document currently being routed
     * @param documentEntry the data dictionary entry of the type of document currently being routed
     * @param routeLevel the document's current route level
     */
    protected void decorateWithCommonQualifiers(List<AttributeSet> qualifiers, Document document, DocumentEntry documentEntry, String routeLevel) {
        for (AttributeSet qualifier : qualifiers) {
            addCommonQualifiersToAttributeSet(qualifier, document, documentEntry, routeLevel);
        }
    }
    
    /**
     * Adds common qualifiers to a given AttributeSet
     * @param qualifier an AttributeSet to add common qualifiers to
     * @param document the document currently being routed
     * @param documentEntry the data dictionary entry of the type of document currently being routed
     * @param routeLevel the document's current route level
     */
    protected void addCommonQualifiersToAttributeSet(AttributeSet qualifier, Document document, DocumentEntry documentEntry, String routeLevel) {
        qualifier.put(DataDictionaryQualifierResolver.KIM_ATTRIBUTE_DOCUMENT_NUMBER, document.getDocumentNumber());
        qualifier.put(DataDictionaryQualifierResolver.KIM_ATTRIBUTE_DOCUMENT_TYPE_NAME, documentEntry.getDocumentTypeName());
        qualifier.put(DataDictionaryQualifierResolver.KIM_ATTRIBUTE_ROUTE_LEVEL_NAME, routeLevel);
    }
}
