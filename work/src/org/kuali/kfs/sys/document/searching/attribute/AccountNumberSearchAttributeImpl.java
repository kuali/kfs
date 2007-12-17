/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.workflow.attribute;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.docsearch.SearchableAttributeStringValue;
import edu.iu.uis.eden.docsearch.SearchableAttributeValue;
import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;

/**
 * This is a search attribute that lets us search on account numbers in any accounting document.
 */
public class AccountNumberSearchAttributeImpl extends KualiXmlSearchableAttributeImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountNumberSearchAttributeImpl.class);
    
    /**
     * Validates the inputs that the user gave to search on.  In this case, we're going to make sure
     * that the account number entered is numeric.
     * 
     * @see edu.iu.uis.eden.docsearch.SearchableAttribute#validateUserSearchInputs(java.util.Map)
     */
    @Override
    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map params) {
        String accountNumber = (String)params.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        List<WorkflowAttributeValidationError> errors = new ArrayList<WorkflowAttributeValidationError>();
        if (StringUtils.isNotBlank(accountNumber)) { // only validate if the account number isn't empty
            if (!accountNumber.matches("^[0-9 ]{1,10}$")) {
                errors.add(new WorkflowAttributeValidationError(KFSPropertyConstants.ACCOUNT_NUMBER, "Invalid Account Number Value.  Account numbers must be all numeric, with at least one digit"));
            }
        }
        return errors;
    }

    /**
     * Returns searchable attributes that should be searched on for the given document content.  Here,
     * we're going to determine the type of the document and, from that, derive the correct type names of the
     * Source and Target accounting line classes.
     * 
     * @see edu.iu.uis.eden.docsearch.xml.StandardGenericXMLSearchableAttribute#getSearchStorageValues(java.lang.String)
     */
    @Override
    public List<SearchableAttributeValue> getSearchStorageValues(String docContent) {
        List<SearchableAttributeValue> storageValues =  super.getSearchStorageValues(docContent);
        Document document;
        if (StringUtils.isBlank(docContent)) {
            LOG.warn("Empty Document Content found '" + docContent + "'");
            return storageValues;
        }
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(docContent))));
        } catch (Exception e){
            LOG.error("error parsing docContent: "+docContent, e);
            throw new RuntimeException("Error trying to parse docContent: "+docContent, e);
        }
        AccountingLineClassDeterminer accountingLineClassDeterminer = new AccountingLineClassDeterminer(document);
        XPath xpath = XPathHelper.newXPath(document);
                
        try {
            if (accountingLineClassDeterminer.getSourceAccountingLineClassName() != null) {
                String sourceLineXPath = KualiWorkflowUtils.xstreamSafeXPath(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + accountingLineClassDeterminer.getSourceAccountingLineClassName());
                NodeList sourceLineNodes = (NodeList) xpath.evaluate(sourceLineXPath, document, XPathConstants.NODESET);
                addSearchableValuesForNodes(sourceLineNodes, storageValues, xpath);
            }

            if (accountingLineClassDeterminer.getTargetAccountingLineClassName() != null) {
                String targetLineXPath = KualiWorkflowUtils.xstreamSafeXPath(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + accountingLineClassDeterminer.getTargetAccountingLineClassName());
                NodeList targetLineNodes = (NodeList) xpath.evaluate(targetLineXPath, document, XPathConstants.NODESET);
                addSearchableValuesForNodes(targetLineNodes, storageValues, xpath);
            }
        }
        catch (XPathExpressionException e) {
            LOG.error("Could not parse XPath expression: "+e);
            throw new RuntimeException(e);
        }
        
        return storageValues;
    }
    
    /**
     * Loops through a NodeList (what an awful, awful class) and adds the appropriate search attribute values to store for account number searches into the given List of SearchableAttributeValues
     * @param accountingLineNodes a list of nodes with accounting lines in them
     * @param searchAttributeStorageValues the list of SearchableAttributeValues to put the storage attributes into
     * @param xpath the XPath evaluator for the document
     */
    private void addSearchableValuesForNodes(NodeList accountingLineNodes, List<SearchableAttributeValue> searchAttributeStorageValues, XPath xpath) throws XPathExpressionException {
        for (int i = 0; i < accountingLineNodes.getLength(); i++) {
            Node node = accountingLineNodes.item(i);
            SearchableAttributeValue val = retrieveSearchableAttribute(node, xpath);
            if (val != null) {
                searchAttributeStorageValues.add(val);
            }
        }
    }

    /**
     * Creates a new searchable attribute value with the account number from the node as the value and "accountNumber" as the key
     * @param accountingNumberNode an XML document node with an account number in it
     * @param xpath the XPath evaluator for the document
     * @return the searchable attribute value derived from that node
     */
    private SearchableAttributeValue retrieveSearchableAttribute(Node accountingLineNode, XPath xpath) throws XPathExpressionException {
        String accountNumber = xpath.evaluate((KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.ACCOUNT_NUMBER), accountingLineNode);
        if (!StringUtils.isBlank(accountNumber)) {
            SearchableAttributeStringValue storageValue = new SearchableAttributeStringValue();
            storageValue.setSearchableAttributeKey(KFSPropertyConstants.ACCOUNT_NUMBER);
            storageValue.setSearchableAttributeValue(accountNumber);
            return storageValue;
        }
        else {
            return null;
        }
    }
}
