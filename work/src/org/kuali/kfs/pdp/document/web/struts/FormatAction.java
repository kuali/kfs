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
package org.kuali.kfs.pdp.document.web.struts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.kfs.module.bc.document.service.BudgetRequestImportService;
import org.kuali.kfs.pdp.DateHandler;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.FormatProcess;
import org.kuali.kfs.pdp.businessobject.FormatResult;
import org.kuali.kfs.pdp.businessobject.FormatSelection;
import org.kuali.kfs.pdp.service.FormatProcessService;
import org.kuali.kfs.pdp.service.FormatService;
import org.kuali.kfs.pdp.service.impl.exception.DisbursementRangeExhaustedException;
import org.kuali.kfs.pdp.service.impl.exception.MissingDisbursementRangeException;
import org.kuali.kfs.pdp.service.impl.exception.NoBankForCustomerException;
import org.kuali.kfs.pdp.web.struts.FormatProcessForm;
import org.kuali.kfs.pdp.web.struts.FormatSelectionForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.web.format.DateFormatter;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class FormatAction extends KualiAction {
    
    private FormatService formatService;
    private FormatProcessService formatProcessService;
    
    public FormatAction() {
        formatService = SpringContext.getBean(FormatService.class);
        formatProcessService = SpringContext.getBean(FormatProcessService.class);
    }
    
    /**
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        FormatForm formatForm = (FormatForm) form;
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();
        FormatSelection formatSelection = formatProcessService.getDataForFormat(kualiUser);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        formatForm.setCampus(kualiUser.getCampusCode());

        // no data for format because another format process is already running
        if (formatSelection.getStartDate() != null) {
            formatService.endFormatProcess(kualiUser.getCampusCode());
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.Format.ERROR_PDP_FORMAT_PROCESS_ALREADY_RUNNING, dateTimeService.toDateString(formatSelection.getStartDate()));
        }
        else {
            List<CustomerProfile> customers = formatSelection.getCustomerList();

            for (CustomerProfile element : customers) {

                if (formatSelection.getCampus().equals(element.getDefaultPhysicalCampusProcessingCode())) {
                    element.setSelectedForFormat(Boolean.TRUE);
                }
                else {
                    element.setSelectedForFormat(Boolean.FALSE);
                }
            }

            formatForm.setPaymentDate(dateTimeService.getCurrentTimestamp());
            formatForm.setPaymentTypes(PdpConstants.PaymentType.ALL.getPaymentType());
            formatForm.setCustomers(customers);
            formatForm.setRanges(formatSelection.getRangeList());
        }

        return mapping.findForward(PdpConstants.MAPPING_SELECTION);
    }
    
    /**
     * This method...
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FormatForm formatForm = (FormatForm)form;

        if (formatForm.getCampus() == null) {
            return mapping.findForward(PdpConstants.MAPPING_SELECTION);
        }

        // Figure out which ones they have selected
        List selectedCustomers = new ArrayList();

        for(CustomerProfile customer : formatForm.getCustomers()){
            if(customer.isSelectedForFormat())
            {
                selectedCustomers.add(customer);
            }
        }

        Date paymentDate = formatForm.getPaymentDate();
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();

        List<FormatResult> results = formatService.startFormatProcess(kualiUser, formatForm.getCampus(), selectedCustomers, paymentDate, formatForm.getPaymentTypes());
        if (results.size() == 0) {
            GlobalVariables.getMessageList().add(PdpKeyConstants.Format.ERROR_PDP_NO_MATCHING_PAYMENT_FOR_FORMAT);
            return mapping.findForward(PdpConstants.MAPPING_SELECTION);
        }

        // Get the first one to get the process ID out of it
        FormatResult fr = (FormatResult) results.get(0);
        formatForm.setProcId(fr.getProcId());
        
        int count = 0;
        KualiDecimal amount = KualiDecimal.ZERO;

        for (FormatResult element : results) {
            count += element.getPayments();
            amount = amount.add(element.getAmount());
        }

        formatForm.setTotalAmount(amount);
        formatForm.setTotalPaymentCount(count);
        formatForm.setResults(results);

        return mapping.findForward(PdpConstants.MAPPING_CONTINUE);
    }
    
    /**
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward continueFormat(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FormatForm formatForm = (FormatForm)form;
        try {
            List<FormatResult> results = formatService.performFormat(formatForm.getProcId());
            Collections.sort(results);
            FormatResult total = new FormatResult();
            for (FormatResult element : results) {
                total.setPayments(total.getPayments() + element.getPayments());
                total.setAmount(total.getAmount().add(element.getAmount()));
            }
            formatForm.setResults(results);
            formatForm.setTotalAmount(total.getAmount());
            formatForm.setTotalPaymentCount(total.getPayments());
            
            return mapping.findForward(PdpConstants.MAPPING_FINISHED);
        }
        catch (NoBankForCustomerException nbfce) {

            ActionErrors ae = new ActionErrors();
            ae.add("global", new ActionMessage("format.bank.missing", nbfce.getCustomerProfile()));
            saveErrors(request, ae);
            return mapping.findForward("pdp_message");
        }
        catch (DisbursementRangeExhaustedException e) {

            ActionErrors ae = new ActionErrors();
            ae.add("global", new ActionMessage("format.disb.exhausted"));
            saveErrors(request, ae);
            return mapping.findForward("pdp_message");
        }
        catch (MissingDisbursementRangeException e) {

            ActionMessages ae = new ActionMessages();
            ae.add("global", new ActionMessage("format.disb.missing"));
            saveErrors(request, ae);
            return mapping.findForward("pdp_message");
        }
    }
    

}
