/*
 * Copyright 2006-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.gl.service.TrialBalanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiLookupAction;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.util.GlobalVariables;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;

/**
 * Actions for Trial Balance Report
 */

public class TrialBalanceReportAction extends KualiLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrialBalanceReportAction.class);

    private static final String TOTALS_TABLE_KEY = "totalsTable";


    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#search(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TrialBalanceReportForm lookupForm = (TrialBalanceReportForm) form;
        Lookupable lookupable = lookupForm.getLookupable();

        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Collection displayList = new ArrayList();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        lookupable.validateSearchParameters(lookupForm.getFields());

        // get the lookup results and build the inquiry link
        displayList = lookupable.performLookup(lookupForm, resultTable, true);

        int totalSize = displayList.size();

        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, totalSize);

        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, resultTable);

        request.setAttribute(TOTALS_TABLE_KEY, resultTable);
        GlobalVariables.getUserSession().addObject(TOTALS_TABLE_KEY, resultTable);

        if (request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY) != null) {
            GlobalVariables.getUserSession().removeObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
        }

        request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(resultTable));

        return mapping.findForward(RiceConstants.MAPPING_BASIC);

    }

    /**
     * View results from trial balance
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, GlobalVariables.getUserSession().retrieveObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY)));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, request.getParameter(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE));

        Object totalsTable = GlobalVariables.getUserSession().retrieveObject(TOTALS_TABLE_KEY);
        request.setAttribute(TOTALS_TABLE_KEY, totalsTable);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method handles print pdf file action
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TrialBalanceReportForm lookupForm = (TrialBalanceReportForm) form;

        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Collection displayList = new ArrayList();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(lookupForm.getFields());

        boolean bounded = true;

        displayList = kualiLookupable.performLookup(lookupForm, resultTable, bounded);

        if (!displayList.isEmpty()) {
            String reportYear = lookupForm.getFields().get(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME);

            String reportFileFullName = SpringContext.getBean(TrialBalanceService.class).generateReportForExtractProcess(displayList, reportYear);

            // using itext for pdf
            ByteArrayOutputStream baos = generatePdfOutStream(reportFileFullName);

            StringBuffer fileName = new StringBuffer(reportYear);
            fileName.append("-TrialBalance.pdf");

            WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, fileName.toString());

            if (baos != null) {
                baos.close();
                baos = null;
            }

            return null;
        }
        else {
            lookupForm.setMessage("No data found.");
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }


    /**
     * Generate pdf for sending response using itext
     *
     * @param reportFileFullName
     * @return
     * @throws IOException
     * @throws DocumentException
     * @throws BadPdfFormatException
     */
    protected ByteArrayOutputStream generatePdfOutStream(String reportFileFullName) throws IOException, DocumentException, BadPdfFormatException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // we create a reader for a certain document
        PdfReader reader = new PdfReader(reportFileFullName);
        reader.consolidateNamedDestinations();

        // step 1: creation of a document-object
        Document document = new Document(reader.getPageSizeWithRotation(1));
        // step 2: we create a writer that listens to the document
        PdfCopy writer = new PdfCopy(document, baos);
        // step 3: we open the document
        document.open();

        // we retrieve the total number of pages
        int n = reader.getNumberOfPages();

        // step 4: we add content
        PdfImportedPage page;
        for (int i = 0; i < n;) {
            ++i;
            page = writer.getImportedPage(reader, i);
            writer.addPage(page);
        }
        writer.freeReader(reader);

        // step 5: we close the document
        document.close();
        return baos;
    }

}
