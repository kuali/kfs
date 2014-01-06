/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.web.struts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionOnDemandLookupResult;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.businessobject.lookup.DunningLetterDistributionOnDemandLookupUtil;
import org.kuali.kfs.module.ar.document.service.DunningLetterDistributionOnDemandService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.GlobalVariables; import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

/**
 * Action class for Dunning Letter Distribution On Demand Summary.
 */
public class DunningLetterDistributionOnDemandSummaryAction extends KualiAction {


    /**
     * 1. This method passes the control from Dunning Letter Distribution On Demand lookup to the Dunning Letter Distribution On
     * Demand Summary page. 2. Retrieves the list of selected awards by agency for sending Dunning Letters
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        DunningLetterDistributionOnDemandSummaryForm dunningLetterDistributionOnDemandSummaryForm = (DunningLetterDistributionOnDemandSummaryForm) form;
        String lookupResultsSequenceNumber = dunningLetterDistributionOnDemandSummaryForm.getLookupResultsSequenceNumber();
        if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
            String personId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
            Collection<DunningLetterDistributionOnDemandLookupResult> dunningLetterDistributionOnDemandLookupResults = DunningLetterDistributionOnDemandLookupUtil.getDunningLetterDistributionOnDemandLookupResultsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId);

            dunningLetterDistributionOnDemandSummaryForm.setDunningLetterDistributionOnDemandLookupResults(dunningLetterDistributionOnDemandLookupResults);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method would create invoices for the list of awards. It calls the batch process to reuse the functionality to send
     * dunning letters
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward sendDunningLetters(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        DunningLetterDistributionOnDemandSummaryForm dunningLetterDistributionOnDemandSummaryForm = (DunningLetterDistributionOnDemandSummaryForm) form;
        SimpleDateFormat FILE_NAME_TIMESTAMP = new SimpleDateFormat("MM-dd-yyyy-hh-mm-ss");
        byte[] report = null;

        Person person = GlobalVariables.getUserSession().getPerson();
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        String lookupResultsSequenceNumber = "";
        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            lookupResultsSequenceNumber = StringUtils.substringBetween(parameterName, ".number", ".");
        }

        Collection<DunningLetterDistributionOnDemandLookupResult> lookupResults = DunningLetterDistributionOnDemandLookupUtil.getDunningLetterDistributionOnDemandLookupResultsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        ByteArrayOutputStream zos = new ByteArrayOutputStream();
        PdfCopyFields reportCopy = new PdfCopyFields(zos);
        reportCopy.open();
        List<DunningLetterTemplate> dunningLetterTemplates = (List<DunningLetterTemplate>) SpringContext.getBean(BusinessObjectService.class).findAll(DunningLetterTemplate.class);
        Iterator<DunningLetterTemplate> iterator = dunningLetterTemplates.iterator();
        while (iterator.hasNext()) {
            DunningLetterTemplate dunningLetterTemplate = iterator.next();
            for (DunningLetterDistributionOnDemandLookupResult dunningLetterDistributionOnDemandLookupResult : lookupResults) {

                report = SpringContext.getBean(DunningLetterDistributionOnDemandService.class).createDunningLetters(dunningLetterTemplate, dunningLetterDistributionOnDemandLookupResult);
                if (ObjectUtils.isNotNull(report)) {
                    reportCopy.addDocument(new PdfReader(report));
                }
            }
        }
        reportCopy.close();

        byte[] finalReport = zos.toByteArray();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (finalReport.length > 0 && SpringContext.getBean(DunningLetterDistributionOnDemandService.class).createZipOfPDFs(finalReport, baos)) {
            response.setContentType("application/zip");
            response.setHeader("Content-disposition", "attachment; filename=Dunning_Letters_" + FILE_NAME_TIMESTAMP.format(new Date()) + ".zip");
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentLength(baos.size());
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            IOUtils.copy(bais, response.getOutputStream());
            response.getOutputStream().flush();
            return null;
        }
        else {
            KNSGlobalVariables.getMessageList().add(ArKeyConstants.DunningCampaignConstantsAndErrors.MESSAGE_DUNNING_CAMPAIGN_BATCH_NOT_SENT);
            dunningLetterDistributionOnDemandSummaryForm.setDunningLetterNotSent(true);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }


    /**
     * To cancel the document, invoices are not created when the cancel method is called.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CANCEL);
    }


}
