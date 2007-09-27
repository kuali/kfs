/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.web.struts.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.logger.Log4JLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fop.apps.Driver;
import org.apache.fop.messaging.MessageHandler;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.WebUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.budget.web.struts.action.BudgetOutputAction;
import org.kuali.module.kra.document.ResearchDocument;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.xml.RoutingFormXml;
import org.kuali.module.kra.web.struts.form.ResearchDocumentFormBase;
import org.w3c.dom.Document;

public class RoutingFormOutputAction extends RoutingFormAction {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetOutputAction.class);
    private static Logger fopLogger = null; // Needed for fop logging

    private static final String XSL_PATH_PARM_NM = "OUTPUT_XSL_PATH";
    private static final String STYLESHEET_URL_OR_PATH_PARM_NM = "OUTPUT_STYLESHEET_URL_OR_PATH";
    
    /**
     * Use for generation of PDF that is to be pushed to the browser.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward pdfOutput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.load(mapping, form, request, response); // Make sure BudgetForm is fully populated

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();

        // No validation on Routing Form

        Document xmlDocument = makeXml(request, researchDocument);

        // Prepare a transformer based on our stylesheet (returned by pickStylesheet).
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        StreamSource streamSource = pickStylesheet();
        Transformer processor = transformerFactory.newTemplates(streamSource).newTransformer();

        // XML to XML-FO transformation based on stylesheet just loaded.
        DOMResult result = new DOMResult();
        processor.transform(new DOMSource(xmlDocument), result);
        Document foDocument = (Document) result.getNode();

        // Set logger for FOP up. org.apache.avalon.framework.logger.Logger is replaced with Jakarta Commons-Logging in FOP 0.92.
        // See http://xmlgraphics.apache.org/fop/0.20.5/embedding.html#basic-logging for details.
        if (fopLogger == null) {
            fopLogger = new Log4JLogger(LOG);
            MessageHandler.setScreenLogger(fopLogger);
        }

        // Create and set up a new fop Driver. Then render the PDF based on the DOM object.
        Driver driver = new Driver();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        driver.setLogger(fopLogger);
        driver.setRenderer(Driver.RENDER_PDF);
        driver.setOutputStream(baos);
        driver.render(foDocument);

        // Retrieve the environment we're in.
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        String env = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);

        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, "kraRoutingForm-" + env + researchDocument.getDocumentNumber() + ".pdf");

        return null; // because saveMimeOutputStreamAsFile commits the response
    }
    
    /**
     * Used for generation of XML data that is to be pushed to the browser.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward xmlOutput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.load(mapping, form, request, response);

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();

        // Validation won't be done because it isn't critical that the xml document has the proper parameters.

        Document xmlDocument = makeXml(request, researchDocument);

        // Create transformer with indentation
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");

        // src = our XML source object, dest = byte stream
        Source src = new DOMSource(xmlDocument);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Result dest = new StreamResult(baos);
        transformer.transform(src, dest);

        // Retrieve the environment we're in.
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        String env = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        
        WebUtils.saveMimeOutputStreamAsFile(response, "text/xml", baos, "kraRoutingForm-" + env + researchDocument.getDocumentNumber() + ".xml");

        return null; // because saveMimeOutputStreamAsFile commits the response
    }

    /**
     * Helper method for pdfOutput and xmlOutput.
     * 
     * @param request
     * @param ResearchDocument researchDocument
     * @return newly created Xml document.
     * @throws Exception
     */
    private Document makeXml(HttpServletRequest request, ResearchDocument researchDocument) throws Exception {
        // following is like returnUrl in KualiCore
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        // Set DOM objects for XML generation up
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
        Document xmlDocument = domBuilder.newDocument();
        RoutingFormXml.makeXml((RoutingFormDocument) researchDocument, xmlDocument, baseUrl);
        return xmlDocument;
    }
    
    /**
     * Returns a file handle to the routing form style sheet. If STYLESHEET_URL_OR_PATH contains a complete url (method checks
     * for "://") then STYLESHEET_URL_OR_PATH is used, otherwise baseUrl + STYLESHEET_URL_OR_PATH is used. This is to allow
     * both internal and external URLs. The appropriate *_XSL_PATH is tagged to the end of that.
     * 
     * @return StreamSource to appropriate stylesheet
     * @throws IOException
     */
    private StreamSource pickStylesheet() throws IOException {
        String urlString = "";
        
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        String stylesheetUrlOrPath = kualiConfigurationService.getParameterValue(KFSConstants.KRA_NAMESPACE, KraConstants.Components.ROUTING_FORM, STYLESHEET_URL_OR_PATH_PARM_NM);
        
        // following checks if STYLESHEET_URL_OR_PATH is a URL already or path within the project
        if (stylesheetUrlOrPath.contains("://")) {
            urlString = stylesheetUrlOrPath;
        }
        else {
            String applicationBaseUrlKey = kualiConfigurationService.getPropertyString(KFSConstants.APPLICATION_URL_KEY);
            urlString = applicationBaseUrlKey + stylesheetUrlOrPath;
        }

        urlString += kualiConfigurationService.getParameterValue(KFSConstants.KRA_NAMESPACE, KraConstants.Components.ROUTING_FORM, XSL_PATH_PARM_NM);

        return new StreamSource(new URL(urlString).openConnection().getInputStream());
    }
    
    /**
     * Handle header navigation request.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward navigate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.load(mapping, form, request, response);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
