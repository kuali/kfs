/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.budget.web.struts.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.apache.commons.lang.StringUtils;
import org.apache.fop.apps.Driver;
import org.apache.fop.messaging.MessageHandler;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.WebUtils;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;
import org.kuali.module.kra.budget.xml.BudgetXml;
import org.w3c.dom.Document;

/**
 * This class handles Output Actions for Research Administration.
 * 
 * @author KRA (era_team@indiana.edu)
 */
public class BudgetOutputAction extends BudgetAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetOutputAction.class);
    private static Logger fopLogger = null; // Needed for fop logging

    // These are not at global level because only the output page should care about this. Not application constants
    // because tag can't be changed without release.
    private static final String GENERIC_BY_TASK = "genericByTask";
    private static final String GENERIC_BY_PERIOD = "genericByPeriod";
    private static final String AGENCY = "agency";
    private static final String NIH_2590 = "NIH-2590";
    private static final String NIH_398 = "NIH-398";
    private static final String NIH_MOD = "NIH-mod";
    private static final String NIH_SUMMARY = "NSF-summary";
    private static final String SF_424 = "SF424";

    // Style sheet locations.
    /** TODO Application Constants */
    private static final String STYLESHEET_URL_OR_PATH = "/xml/budget/";
    private static final String GENERIC_BY_TASK_XSL_PATH = "generic/genericByTask.xsl";
    private static final String GENERIC_BY_PERIOD_XSL_PATH = "generic/genericByPeriod.xsl";
    private static final String NIH_2590_XSL_PATH = "nih/nih-2590.xsl";
    private static final String NIH_398_XSL_PATH = "nih/nih-398.xsl";
    private static final String NIH_MOD_XSL_PATH = "nih/nih-modular.xsl";
    private static final String NIH_SUMMARY_XSL_PATH = "nsf/NSFSummaryProposalBudget.xsl";
    private static final String SF_424_XSL_PATH = "sf424.xsl";

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

        BudgetForm budgetForm = (BudgetForm) form;
        BudgetDocument budgetDoc = budgetForm.getBudgetDocument();

        if (!isOutputPageSelectionValid(budgetForm)) {
            return mapping.findForward(Constants.MAPPING_BASIC);
        }

        Document xmlDocument = makeXml(request, budgetForm, budgetDoc);

        // Prepare a transformer based on our stylesheet (returned by pickStylesheet).
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        StreamSource streamSource = pickStylesheet(budgetForm.getCurrentOutputReportType(), budgetForm.getCurrentOutputAgencyType(), request);
        Transformer processor = transformerFactory.newTemplates(streamSource).newTransformer();

        // XML to XML-FO transformation based on stylesheet just loaded.
        DOMResult result = new DOMResult();
        processor.transform(new DOMSource(xmlDocument), result);
        Document foDocument = (Document) result.getNode();

        // Set logger for FOP up. org.apache.avalon.framework.logger.Logger is replaced with Jakarta Commons-Logging in FOP 0.92.
        // See
        // http://xmlgraphics.apache.org/fop/0.20.5/embedding.html#basic-logging for details. */
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
        KualiConfigurationService kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
        String env = kualiConfigurationService.getPropertyString("environment");

        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, "kraBudget-" + env + budgetDoc.getBudget().getDocumentHeaderId() + ".pdf");

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
        this.load(mapping, form, request, response); // Make sure BudgetForm is fully populated

        BudgetForm budgetForm = (BudgetForm) form;
        BudgetDocument budgetDoc = budgetForm.getBudgetDocument();

        // Validation won't be done because it isn't critical that the xml document has the proper parameters.

        Document xmlDocument = makeXml(request, budgetForm, budgetDoc);

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
        KualiConfigurationService kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
        String env = kualiConfigurationService.getPropertyString("environment");

        WebUtils.saveMimeOutputStreamAsFile(response, "text/xml", baos, "kraBudget-" + env + budgetDoc.getBudget().getDocumentHeaderId() + ".xml");

        return null; // because saveMimeOutputStreamAsFile commits the response
    }


    /**
     * Validation check for the output page.
     * 
     * @param budgetForm
     * @return whether output page selections are valid
     */
    private boolean isOutputPageSelectionValid(BudgetForm budgetForm) {
        boolean valid = true;
        if (budgetForm.getCurrentOutputReportType() == null) {
            GlobalVariables.getErrorMap().putError("currentOutputReportType", KeyConstants.ERROR_REQUIRED, "Report Type");
            valid = false;
        }
        else {
            if ((GENERIC_BY_TASK.equals(budgetForm.getCurrentOutputReportType()) || GENERIC_BY_PERIOD.equals(budgetForm.getCurrentOutputReportType())) && StringUtils.isBlank(budgetForm.getCurrentOutputDetailLevel())) {
                GlobalVariables.getErrorMap().putError("currentOutputDetailLevel", KeyConstants.ERROR_REQUIRED, "Detail Level");
                valid = false;
            }
            if (AGENCY.equals(budgetForm.getCurrentOutputReportType()) && StringUtils.isBlank(budgetForm.getCurrentOutputAgencyType())) {
                GlobalVariables.getErrorMap().putError("currentOutputAgencyType", KeyConstants.ERROR_REQUIRED, "Agency Type");
                valid = false;
            }
            else if (AGENCY.equals(budgetForm.getCurrentOutputReportType()) && NIH_2590.equals(budgetForm.getCurrentOutputAgencyType()) && StringUtils.isBlank(budgetForm.getCurrentOutputAgencyPeriod())) {
                GlobalVariables.getErrorMap().putError("currentOutputAgencyPeriod", KeyConstants.ERROR_REQUIRED, "Agency Period");
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Helper method for pdfOutput and xmlOutput.
     * 
     * @param request
     * @param budgetForm
     * @param budgetDocument
     * @return newly created Xml document.
     * @throws ParserConfigurationException
     * @throws Exception
     */
    private Document makeXml(HttpServletRequest request, BudgetForm budgetForm, BudgetDocument budgetDocument) throws ParserConfigurationException, Exception {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath(); // like
                                                                                                                                            // returnUrl
                                                                                                                                            // in
                                                                                                                                            // Kuali
                                                                                                                                            // Core

        String param = "";
        if (GENERIC_BY_TASK.equals(budgetForm.getCurrentOutputReportType()) || GENERIC_BY_PERIOD.equals(budgetForm.getCurrentOutputReportType())) {
            param = budgetForm.getCurrentOutputDetailLevel();
        }
        else if (AGENCY.equals(budgetForm.getCurrentOutputReportType()) && NIH_2590.equals(budgetForm.getCurrentOutputAgencyType())) {
            param = budgetForm.getCurrentOutputAgencyPeriod();
        }

        // Set DOM objects for XML generation up
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
        Document xmlDocument = domBuilder.newDocument();
        BudgetXml.makeXml(budgetDocument, xmlDocument, baseUrl, param);
        return xmlDocument;
    }

    /**
     * Returns a file handle to the parameter passed in for a particular style sheet. If STYLESHEET_URL_OR_PATH contains a complete
     * url (method checks for "://") then STYLESHEET_URL_OR_PATH is used, otherwise baseUrl + STYLESHEET_URL_OR_PATH is used. This
     * is to allow both internal and external URLs. The appropriate *_XSL_PATH is tagged to the end of that.
     * 
     * @param currentOutputReportType stylesheet identifier
     * @param currentOutputAgencyType stylesheet identifier if currentOutputReportType=AGENCY
     * @param request
     * @return StreamSource to appropriate stylesheet
     * @throws IOException
     */
    private StreamSource pickStylesheet(String currentOutputReportType, String currentOutputAgencyType, HttpServletRequest request) throws IOException {
        String urlString = "";

        if (STYLESHEET_URL_OR_PATH.contains("://")) {
            urlString = STYLESHEET_URL_OR_PATH;
        }
        else {
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath(); // like
                                                                                                                                                // returnUrl
                                                                                                                                                // in
                                                                                                                                                // Kuali
                                                                                                                                                // Core
            urlString = baseUrl + STYLESHEET_URL_OR_PATH;
        }

        if (GENERIC_BY_TASK.equals(currentOutputReportType)) {
            urlString += GENERIC_BY_TASK_XSL_PATH;
        }
        else if (GENERIC_BY_PERIOD.equals(currentOutputReportType)) {
            urlString += GENERIC_BY_PERIOD_XSL_PATH;
        }
        else if (AGENCY.equals(currentOutputReportType)) {
            if (NIH_2590.equals(currentOutputAgencyType)) {
                urlString += NIH_2590_XSL_PATH;
            }
            else if (NIH_398.equals(currentOutputAgencyType)) {
                urlString += NIH_398_XSL_PATH;
            }
            else if (NIH_MOD.equals(currentOutputAgencyType)) {
                urlString += NIH_MOD_XSL_PATH;
            }
            else if (NIH_SUMMARY.equals(currentOutputAgencyType)) {
                urlString += NIH_SUMMARY_XSL_PATH;
            }
            else {
                LOG.error("Report type agency stylesheet not found, using default.");
            }
        }
        else if (SF_424.equals(currentOutputReportType)) {
            urlString += SF_424_XSL_PATH;
        }
        else {
            LOG.error("Report type stylesheet not found, using default.");
        }

        return new StreamSource(new URL(urlString).openConnection().getInputStream());
    }
}
