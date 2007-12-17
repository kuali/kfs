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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.kuali.core.service.DocumentTypeService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;

/**
 * This class determines the classes of the accounting lines of a workflow created XML document.
 */
public class AccountingLineClassDeterminer {
    private AccountingDocument accountingDocument;
    private String sourceAccountingLineClassName;
    private boolean sourceNameIsNull = false;
    private String targetAccountingLineClassName;
    private boolean targetNameIsNull = false;
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingLineClassDeterminer.class);
    
    /**
     * Constructs a AccountingLineClassDeterminer
     * @param doc the XML document that represents an accounting document
     */
    public AccountingLineClassDeterminer(Document doc) {
        try {
            XPath xpath = XPathHelper.newXPath(doc);
            
            String documentXPath = KualiWorkflowUtils.xstreamSafeXPath(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + KFSPropertyConstants.DOCUMENT + "[@class]");
            NodeList documentNodes = (NodeList)xpath.evaluate(documentXPath, doc, XPathConstants.NODESET);
            if (documentNodes.getLength() > 0) {
                String documentClassName = documentNodes.item(0).getAttributes().getNamedItem("class").getTextContent();
                Class documentClass = Class.forName(documentClassName);
                accountingDocument = getAccountingDocumentFromClass(documentClass);
            }
        }
        catch (XPathExpressionException e) {
            LOG.error("Could not parse XPath expression: "+e);
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e) {
            LOG.error("Could not find class "+e);
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            LOG.error("Could not instantiate a class of the given document class: "+e);
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            LOG.error("Illegal Access Exception while instantiating instance of document class: "+e);
            throw new RuntimeException(e);
        }    
    }
    
    /**
     * Constructs a AccountingLineClassDeterminer, using the document type name to determine the class name
     * @param documentTypeName the name of a workflow document type
     */
    public AccountingLineClassDeterminer(String documentTypeName) {
        try {
            Class c = SpringContext.getBean(DocumentTypeService.class).getClassByName(documentTypeName);
            accountingDocument = getAccountingDocumentFromClass(c);
        }
        catch (InstantiationException e) {
            LOG.error("Could not instantiate a class of the given document class: "+e);
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            LOG.error("Illegal Access Exception while instantiating instance of document class: "+e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * This method returns an instantation of a blank instance of that document type, based on the class sent in
     * @param documentClass the child class of AccountingDocument to instantiate
     * @return a blank instance of the accounting document type
     * @throws InstantiationException if exceptions are caused by the instantiation of an instance of the document
     * @throws IllegalAccessException if the class of the document cannot be accessed
     */
    private AccountingDocument getAccountingDocumentFromClass(Class documentClass) throws InstantiationException, IllegalAccessException {
        
        if (!AccountingDocument.class.isAssignableFrom(documentClass)) {
            throw new IllegalArgumentException("getSourceAccountingLineClassName method of KualiWorkflowUtils requires a documentTypeName String that corresponds to a class that implments AccountingDocument");
        }
        
        String docTypeName = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(documentClass);
        return (AccountingDocument) documentClass.newInstance();
    }
    
    /**
     * Returns the full name of the class of the source accounting lines in the document, or null if that class cannot be determined
     * @return the name of the source accounting line class for the document, or null
     */
    public String getSourceAccountingLineClassName() {
        if (sourceAccountingLineClassName == null && !sourceNameIsNull){
            if (accountingDocument != null) {
                if (accountingDocument.getSourceAccountingLineClass() != null) {
                    sourceAccountingLineClassName = accountingDocument.getSourceAccountingLineClass().getName();
                } else {
                    sourceNameIsNull = true;
                }
            } else {
                sourceNameIsNull = true;
            }
        }
        return sourceAccountingLineClassName;
    }
    
    /**
     * Returns the full name of the class of the target accounting lines in the document, or null if that class cannot be determined
     * @return the name of the target accounting line class for the document, or null
     */
    public String getTargetAccountingLineClassName() {
        if (targetAccountingLineClassName == null && !targetNameIsNull) {
            if (accountingDocument != null) {
                if (accountingDocument.getTargetAccountingLineClass() != null) {
                    targetAccountingLineClassName = accountingDocument.getTargetAccountingLineClass().getName();
                } else {
                    targetNameIsNull = true;
                }
            } else {
                targetNameIsNull = true;
            }
        }
        return targetAccountingLineClassName;
    } 
}
