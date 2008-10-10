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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.service.PaymentDetailService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class PaymentDetailAction extends KualiAction {
    
    private PaymentDetailService paymentDetailService;
    private PaymentGroupService paymentGroupService;
    
    public PaymentDetailAction() {
        setPaymentDetailService(SpringContext.getBean(PaymentDetailService.class));
        setPaymentGroupService(SpringContext.getBean(PaymentGroupService.class));
    }
    
    public ActionForward showPaymentDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentDetailForm paymentDetailForm = (PaymentDetailForm) form;
        paymentDetailForm.setPayees(getPayeesMap());
        Integer detailId;
        PaymentDetail pd;
        if (request.getParameter("DetailId") != null) {
            // Payment Detail ID was passed - find payment Detail with the ID
            detailId = new Integer(request.getParameter("DetailId"));
            pd = paymentDetailService.get(detailId);
            paymentDetailForm.setPaymentDetail(pd);
            paymentDetailForm.setLookupLink(getLookupLink(detailId));

            // pd.setLastDisbursementActionDate(this.getDisbursementActionExpirationDate(request));
            setPayeeDescriptor(paymentDetailForm);
            setDisbursementPaymentList(paymentDetailForm);
            paymentDetailForm.setSize(new Integer(pd.getPaymentGroup().getPaymentDetails().size()));
        }
        String tab = request.getParameter("tab");
        if (StringUtils.isNotEmpty(tab)) {
            paymentDetailForm.setBtnPressed(tab);
        }
        else {
            paymentDetailForm.setBtnPressed("btnSummaryTab");
        }
        String listType = request.getParameter("listType");
        if(StringUtils.isNotEmpty(listType))
        {
            paymentDetailForm.setListType(listType);
            return mapping.findForward("displayList");
        }

        return mapping.findForward("display");
    }
    
    private String getLookupLink(Integer detailId)
    {
        Properties params = new Properties();
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PaymentDetail.class.getName());
        params.put(KNSConstants.DOC_FORM_KEY, "88888888");
        params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        params.put(KFSConstants.RETURN_LOCATION_PARAMETER, KFSConstants.MAPPING_PORTAL + ".do");
        params.put(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_ID, UrlFactory.encode(String.valueOf(detailId)));
        String url = "/kr/" + UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
       return url;
    }
    
    private void setPayeeDescriptor(PaymentDetailForm paymentDetailForm) {
        // Get descriptor of Payee ID Type based on Code in DB
        Map payees = paymentDetailForm.getPayees();
        PaymentDetail paymentDetail = paymentDetailForm.getPaymentDetail();
        Iterator i = payees.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            if (paymentDetail != null) {
                if (key.equals(paymentDetail.getPaymentGroup().getPayeeIdTypeCd())) {
                    paymentDetailForm.setPayeeIdTypeDesc((String)payees.get(key));
                }
                if (key.equals(paymentDetail.getPaymentGroup().getAlternatePayeeIdTypeCd())) {
                    paymentDetailForm.setAlternatePayeeIdTypeDesc((String)payees.get(key));
                }
            }
        }
    }
    
    private void setDisbursementPaymentList(PaymentDetailForm paymentDetailForm) {
        List paymentDetailList = new ArrayList();
        PaymentDetail pd = paymentDetailForm.getPaymentDetail();
        Integer disbNbr = pd.getPaymentGroup().getDisbursementNbr().intValue();

        if ((disbNbr != null) && (disbNbr != new Integer(0))) {
            List paymentGroupList = paymentGroupService.getByDisbursementNumber(disbNbr);
            for (Iterator iter = paymentGroupList.iterator(); iter.hasNext();) {
                PaymentGroup elem = (PaymentGroup) iter.next();
                paymentDetailList.addAll(elem.getPaymentDetails());
            }
            paymentDetailForm.setDisbNbrTotalPayments( paymentDetailList.size());
            paymentDetailForm.setDisbursementDetailsList(paymentDetailList);
        }
    }
    
    private Map getPayeesMap()
    {
        Map<String, String> payees = new HashMap();
        payees.put(PdpConstants.PayeeIdTypeCodes.PAYEE_ID, "Payee ID");
        payees.put(PdpConstants.PayeeIdTypeCodes.SSN, "SSN");
        payees.put(PdpConstants.PayeeIdTypeCodes.EMPLOYEE_ID, "Employee ID");
        payees.put(PdpConstants.PayeeIdTypeCodes.FEIN, "FEIN");
        payees.put(PdpConstants.PayeeIdTypeCodes.VENDOR_ID, "Vendor ID");
        payees.put(PdpConstants.PayeeIdTypeCodes.OTHER, "Other");
        return payees;
    }

    public PaymentDetailService getPaymentDetailService() {
        return paymentDetailService;
    }

    public void setPaymentDetailService(PaymentDetailService paymentDetailService) {
        this.paymentDetailService = paymentDetailService;
    }

    public PaymentGroupService getPaymentGroupService() {
        return paymentGroupService;
    }

    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

}
