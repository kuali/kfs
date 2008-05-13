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

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import junit.framework.AssertionFailedError;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.AttributeDefinition;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.test.ConfigureContext;
import org.kuali.test.suite.RelatesTo;
import org.kuali.test.suite.RelatesTo.JiraIssue;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import edu.iu.uis.eden.xml.XmlConstants;


/**
 * This class test that the labels used by workflow in the UI are coming from the KFS data dictionary. It also indirectly test if
 * routing is working, since the xpath is parsed to find the label in the data dictionary. If the label isn't found, it is likely
 * that the xpath will also fail to find the element in the document xml. The fact that the name and title are unique for each field
 * within the attribute is also verfied.
 */
@ConfigureContext
public class KualiXmlAttributeImplTest extends KualiTestBase {

    private static Log LOG = LogFactory.getLog(KualiXmlRuleAttributeImpl.class);

    private static final String RULE_ATTRIBUTE_CONFIG_NODE_NAME = XmlConstants.ROUTING_CONFIG;
    private static final String SEARCH_ATTRIBUTE_CONFIG_NODE_NAME = XmlConstants.SEARCHING_CONFIG;

    XPath myXPath = XPathHelper.newXPath();
    String ruleAttributeXml = "";
    String searchAttributeXml = "";
    private boolean testFailed = false;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        if ((StringUtils.isNotBlank(ruleAttributeXml)) && (StringUtils.isNotBlank(searchAttributeXml))) {
            return;
        }

        DataSource mySource = SpringContext.getBean(DataSource.class);
        ruleAttributeXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<data >\n<ruleAttributes>\n";
        searchAttributeXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<data >\n<ruleAttributes>\n";
        Connection dbCon = null;
        try {

            dbCon = mySource.getConnection();
            Statement dbAsk = dbCon.createStatement();
            //ResultSet dbAnswer = dbAsk.executeQuery("select * from EN_RULE_ATTRIB_T");
             ResultSet dbAnswer = dbAsk.executeQuery("select * from EN_RULE_ATTRIB_T where RULE_ATTRIB_NM = 'KualiPurchaseOrderTransmissionMethodAttribute'");

            while (dbAnswer.next()) {
                String className = dbAnswer.getString("RULE_ATTRIB_CLS_NM");
                if (StringUtils.isNotBlank(className)) {
                    try {
                        if (KualiXmlAttribute.class.isAssignableFrom(Class.forName(className))) {
                            LOG.debug("Adding attribute to test with class name " + className);
                            String attributeType = dbAnswer.getString("RULE_ATTRIB_TYP");
                            if (EdenConstants.RULE_XML_ATTRIBUTE_TYPE.equals(attributeType)) {
                                ruleAttributeXml = ruleAttributeXml + "<ruleAttribute>\n\t<name>";
                                ruleAttributeXml = ruleAttributeXml + dbAnswer.getString("RULE_ATTRIB_NM");
                                ruleAttributeXml = ruleAttributeXml + "</name>\n\t<className>";
                                ruleAttributeXml = ruleAttributeXml + className;
                                ruleAttributeXml = ruleAttributeXml + "</className>\n\t<label>";
                                ruleAttributeXml = ruleAttributeXml + dbAnswer.getString("RULE_ATTRIB_LBL_TXT");
                                ruleAttributeXml = ruleAttributeXml + "</label>\n\t<description>";
                                ruleAttributeXml = ruleAttributeXml + dbAnswer.getString("RULE_ATTRIB_DESC");
                                ruleAttributeXml = ruleAttributeXml + "</description>\n\t<type>";
                                ruleAttributeXml = ruleAttributeXml + attributeType;
                                ruleAttributeXml = ruleAttributeXml + "</type>\n\t" + dbAnswer.getString("RULE_ATTRIB_XML_RTE_TXT") + "\n</ruleAttribute>\n";

                            }
                            else if (EdenConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(attributeType)) {
                                searchAttributeXml = searchAttributeXml + "<ruleAttribute>\n\t<name>";
                                searchAttributeXml = searchAttributeXml + dbAnswer.getString("RULE_ATTRIB_NM");
                                searchAttributeXml = searchAttributeXml + "</name>\n\t<className>";
                                searchAttributeXml = searchAttributeXml + className;
                                searchAttributeXml = searchAttributeXml + "</className>\n\t<label>";
                                searchAttributeXml = searchAttributeXml + dbAnswer.getString("RULE_ATTRIB_LBL_TXT");
                                searchAttributeXml = searchAttributeXml + "</label>\n\t<description>";
                                searchAttributeXml = searchAttributeXml + dbAnswer.getString("RULE_ATTRIB_DESC");
                                searchAttributeXml = searchAttributeXml + "</description>\n\t<type>";
                                searchAttributeXml = searchAttributeXml + attributeType;
                                searchAttributeXml = searchAttributeXml + "</type>\n\t" + dbAnswer.getString("RULE_ATTRIB_XML_RTE_TXT") + "\n</ruleAttribute>\n";

                            }
                        }
                        else {
                            LOG.debug("Skipping attribute with class name " + className);
                        }
                    }
                    catch (ClassNotFoundException cnfe) {
                        LOG.debug("Could not find class for name '" + className + "'");
                    }
                }
            }
            ruleAttributeXml = ruleAttributeXml + "</ruleAttributes>\n</data>\n";
            searchAttributeXml = searchAttributeXml + "</ruleAttributes>\n</data>\n";

            ruleAttributeXml = ruleAttributeXml.replaceAll(" & ", " &amp; ");
            searchAttributeXml = searchAttributeXml.replaceAll(" & ", " &amp; ");

            loadDataDictionaryEntries();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                dbCon.close();
            }
            catch (SQLException sqle2) {
                sqle2.printStackTrace();
            }
        }
    }

    /**
     * This method goes through all of the ruleAttributes in the inputSource and tries to get a label out of the data dictionary.
     */
    @RelatesTo(JiraIssue.KFSMI765)
    public void testConfirmLabels() {
        testFailed = false;

        // test rule xml attributes
        confirmLabels(KualiXmlAttributeHelper.notFound, ruleAttributeXml, RULE_ATTRIBUTE_CONFIG_NODE_NAME);

        // test search xml attributes
        confirmLabels(KualiXmlAttributeHelper.notFound, searchAttributeXml, SEARCH_ATTRIBUTE_CONFIG_NODE_NAME);

        assertFalse("testConfirmLabels failed", testFailed);
    }

    /**
     * This method accepts a Node, and if all goes well, returns the exact same Node, with the name and title attributes added to
     * the fieldDef element. This exercises the getConfigXML method on the class under test.
     * 
     * @param xmlNode
     * @return
     * @throws TransformerException
     */
    private Node configureRuleAttribute(Node xmlNode, KualiXmlAttribute myAttribute) throws TransformerException {
        RuleAttribute ruleAttribute = new RuleAttribute();

        StringWriter xmlBuffer = new StringWriter();
        Source source = new DOMSource(xmlNode);
        Result result = new StreamResult(xmlBuffer);
        TransformerFactory.newInstance().newTransformer().transform(source, result);

        ruleAttribute.setXmlConfigData(new String(xmlBuffer.getBuffer()));
        myAttribute.setRuleAttribute(ruleAttribute);

        if (LOG.isDebugEnabled()) {
            LOG.debug("This is the XML that was added to the attribute");
            LOG.debug(new String(xmlBuffer.getBuffer()));
            StringWriter xmlBuffer2 = new StringWriter();
            Source source2 = new DOMSource(xmlNode);
            Result result2 = new StreamResult(xmlBuffer2);
            TransformerFactory.newInstance().newTransformer().transform(source2, result2);
            LOG.debug("This is the XML that was returned from the ruleAttribute");
            LOG.debug(new String(xmlBuffer2.getBuffer()));
        }
        return myAttribute.getConfigXML();
    }

    /**
     * This method compares the label from the test to the expected, or not expected, value for all of the rule attributes in the
     * file. The inputSource file should be as close to the production version as possible, as described by the class comments. It
     * accepts the string to test against as a parameter.
     * 
     * @param testString
     */
    private void confirmLabels(String testString, String attributeXml, String configNodeName) {
        String theTitle = "";
        String theName = "";
        String attributeName = "";
        try {
            NodeList tempList = (NodeList) myXPath.evaluate("//ruleAttribute", new InputSource(new StringReader(attributeXml)), XPathConstants.NODESET);
            for (int i = 0; i < tempList.getLength(); i++) { // loop over ruleattributes
                Node originalNode = tempList.item(i);
                Set ruleAttributeFieldDefNames = new HashSet();
                Set ruleAttributeFieldDefTitles = new HashSet();
                attributeName = (String) myXPath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + "name", originalNode, XPathConstants.STRING);
                Node classNameNode = (Node) myXPath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + "className", originalNode, XPathConstants.NODE);
                if ((classNameNode != null) && (classNameNode.getFirstChild() != null)) {
                    KualiXmlAttribute myAttribute = (KualiXmlAttribute) GlobalResourceLoader.getObject(new ObjectDefinition(classNameNode.getFirstChild().getNodeValue()));
                    Node xmlNode = configureRuleAttribute(originalNode, myAttribute);
                    NamedNodeMap fieldDefAttributes = null;
                    String potentialFailMessage = "";

                    try {
                        NodeList xmlNodeList = (NodeList) myXPath.evaluate("//fieldDef", xmlNode, XPathConstants.NODESET);

                        for (int j = 0; j < xmlNodeList.getLength(); j++) {
                            Node fieldDefXmlNode = xmlNodeList.item(j);
                            fieldDefAttributes = fieldDefXmlNode.getAttributes();

                            theTitle = fieldDefAttributes.getNamedItem("title").getNodeValue();// Making sure they are clean
                            theName = fieldDefAttributes.getNamedItem("name").getNodeValue();
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(attributeName);
                                LOG.debug("name=" + theName + "   title=" + theTitle);
                            }
                            if (ruleAttributeFieldDefNames.contains(theName)) {
                                // names of fieldDefs inside a single attribute must be unique
                                potentialFailMessage = "Each fieldDef name on a single attribute must be unique and the fieldDef name '" + theName + "' already exists on the attribute '" + attributeName + "'";
                                fail(potentialFailMessage);
                            }
                            else {
                                ruleAttributeFieldDefNames.add(theName);
                            }
                            if (testString.equals(KualiXmlAttributeHelper.notFound)) {
                                potentialFailMessage = "Each fieldDef title should be a valid value and currently the title for attribute '" + attributeName + "' is '" + theTitle + "'";
                                assertFalse(potentialFailMessage, theTitle.equals(testString));
                                if (ruleAttributeFieldDefTitles.contains(theTitle)) {
                                    /*
                                     * Titles of fieldDefs inside a single attribute should be unique in the normal case. Having two
                                     * fields with the same label would certainly confuse the user. However, due to the way the
                                     * confirmSource test works, all the titles/labels must be the same. So only run this check when
                                     * not in the confirmSource test.
                                     */
                                    potentialFailMessage = "Each fieldDef title on a single attribute must be unique and the fieldDef title '" + theTitle + "' already exists on the attribute '" + attributeName + "'";
                                    fail(potentialFailMessage);
                                }
                                else {
                                    ruleAttributeFieldDefTitles.add(theTitle);
                                }
                            }
                            else {
                                potentialFailMessage = "For attribute '" + attributeName + "' the title should have been '" + testString + "' but was actually '" + theTitle + "'";
                                assertEquals(potentialFailMessage, testString, theTitle);
                            }
                        }
                    }
                    catch (AssertionFailedError afe) {
                        LOG.warn("Assertion Failed for attribute '" + attributeName + "' with error " + potentialFailMessage, afe);
                        testFailed = true;
                    }
                    finally {
                        attributeName = "";
                    }
                }
                else {
                    throw new RuntimeException("Could not find class for attribute named '" + attributeName + "'");
                }
            }
        }
        catch (Exception e) {
            LOG.error("General Exception thrown for attribute '" + attributeName + "'", e);
            testFailed = true;
        }
    }

    /**
     * This method confirms that the labels are coming from the data dictionary by modifing all the dictionary values
     * programatically to a nonsense value. It then rebuilds the Hash Table and runs confirmLabels() to make sure the labels have
     * changed.
     */
    @RelatesTo(JiraIssue.KFSMI765)
    public void testLabelSource() {
        DataDictionaryService myDDService = SpringContext.getBean(DataDictionaryService.class);
        XPath xpath = XPathHelper.newXPath();
        String nonsenseString = "BananaRama";
        for (Object tempEntity : myDDService.getDataDictionary().getBusinessObjectEntries().values()) {

            for ( AttributeDefinition attribute : ((BusinessObjectEntry) tempEntity).getAttributes() ) {
                attribute.setLabel(nonsenseString);
                attribute.setShortLabel(nonsenseString);
            }

        }
        for (Object tempEntity : myDDService.getDataDictionary().getDocumentEntries().values()) {

            for ( AttributeDefinition attribute : ((DocumentEntry) tempEntity).getAttributes() ) {
                attribute.setLabel(nonsenseString);
                attribute.setShortLabel(nonsenseString);
            }

        }
        // KualiXmlAttributeHelper.buildDictionaryHash();

        // test rule xml attributes
        KualiXmlAttribute ruleAttribute = new KualiXmlRuleAttributeImpl();
        confirmLabels(nonsenseString, ruleAttributeXml, RULE_ATTRIBUTE_CONFIG_NODE_NAME);

        // test search xml attributes
        KualiXmlAttribute searchAttribute = new KualiXmlSearchableAttributeImpl();
        confirmLabels(nonsenseString, searchAttributeXml, SEARCH_ATTRIBUTE_CONFIG_NODE_NAME);

        if (testFailed)
            throw new AssertionFailedError("testLabelSource failed");
    }

    private void loadDataDictionaryEntries() throws Exception {
        KualiXmlRuleAttributeImpl myAttribute = new KualiXmlRuleAttributeImpl();
        NamedNodeMap fieldDefAttributes = null;
        NodeList tempList = (NodeList) myXPath.evaluate("//ruleAttribute", new InputSource(new StringReader(ruleAttributeXml)), XPathConstants.NODESET);
        for (int i = 0; i < tempList.getLength(); i++) {
            Node xmlNode = configureRuleAttribute(tempList.item(i), myAttribute);
        }
        KualiXmlSearchableAttributeImpl mySearchAttribute = new KualiXmlSearchableAttributeImpl();
        fieldDefAttributes = null;
        tempList = (NodeList) myXPath.evaluate("//ruleAttribute", new InputSource(new StringReader(searchAttributeXml)), XPathConstants.NODESET);
        for (int i = 0; i < tempList.getLength(); i++) {
            Node xmlNode = configureRuleAttribute(tempList.item(i), mySearchAttribute);
        }
    }
}
