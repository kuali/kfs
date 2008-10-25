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
package org.kuali.kfs.fp.document.web.struts;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherRuleConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.action.KualiAction;

/**
 * This class...
 */
public class DisbursementVoucherInitAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherInitAction.class);

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherInitForm dviForm = (DisbursementVoucherInitForm)form;

        return super.execute(mapping, form, request, response);
    }

    /**
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {   
        DisbursementVoucherInitForm dvForm = (DisbursementVoucherInitForm) form;
    
        /* refresh from dv vendor lookup */
        if ((KFSConstants.KUALI_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller()) || KFSConstants.KUALI_VENDOR_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller())) && request.getParameter(KFSPropertyConstants.PAYEE_ID_NUMBER) != null) {
            VendorDetail refreshVendorDetail = new VendorDetail();
            refreshVendorDetail.setVendorNumber(dvForm.getPayeeIdNumber());
            refreshVendorDetail = (VendorDetail) SpringContext.getBean(BusinessObjectService.class).retrieve(refreshVendorDetail);
            if(refreshVendorDetail!=null) {
                dvForm.setHasMultipleAddresses(1 < refreshVendorDetail.getVendorAddresses().size());
            }
            if(StringUtils.isBlank(dvForm.getPayeeTypeCode())) {
                dvForm.setPayeeTypeCode(DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR);
            }
        }
        /* refresh from employee lookup */
        else if ((KFSConstants.KUALI_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller()) || KFSConstants.KUALI_USER_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller())) && request.getParameter(KFSPropertyConstants.PAYEE_ID_NUMBER) != null) {
            dvForm.setHasMultipleAddresses(false);
            
            if(StringUtils.isBlank(dvForm.getPayeeTypeCode())) {
                dvForm.setPayeeTypeCode(DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE);
            }
        }
        /* refresh from vendor address lookup */
        else if((KFSConstants.KUALI_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller()) || KFSConstants.KUALI_VENDOR_ADDRESS_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller())) && request.getParameter(KFSPropertyConstants.VENDOR_ADDRESS_GENERATED_ID) != null ) {
            // Redirect user to the DV main entry page
            // Needs to be set up to handle immediate redirection to DV if there is only one vendor address
            Properties props = new Properties();
            props.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, "docHandler");
            props.put(KNSConstants.PARAMETER_COMMAND, "initiate");
            props.put(KNSConstants.DOCUMENT_TYPE_NAME, "DisbursementVoucherDocument");
            props.put(KFSPropertyConstants.PAYEE_ID_NUMBER, dvForm.getPayeeIdNumber());
            props.put(KFSPropertyConstants.PAYEE_TYPE_CODE, dvForm.getPayeeTypeCode());
            props.put(KFSPropertyConstants.PAYEE_ADDRESS_IDENTIFIER, dvForm.getVendorAddressGeneratedIdentifier());
            String url = UrlFactory.parameterizeUrl(getBasePath(request) + "/financialDisbursementVoucher.do", props);
            
            return new ActionForward(url, true);
        }
    
        return super.refresh(mapping, form, request, response);
    }

    /**
     * The action that sends the user to the correct lookup for them
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return super.refresh(mapping, form, request, response);
    }
 
    /**
     * Hook into performLookup to switch the payee lookup based on the payee type selected.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#performLookup(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // substitute bo class and mapping if the type is Employee, lookup already setup for Vendor
        DisbursementVoucherInitForm dvForm = (DisbursementVoucherInitForm) form;

        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);

        if (VendorDetail.class.getName().equals(boClassName) && dvForm.isEmployee()) {
            String conversionFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);

            fullParameter = StringUtils.replace(fullParameter, boClassName, Person.class.getName());
            fullParameter = StringUtils.replace(fullParameter, conversionFields, "principalId:payeeIdNumber,personName:payeePersonName");
            request.setAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE, fullParameter);
        }

        return super.performLookup(mapping, form, request, response);
    }

    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performContinueAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Insert code here to build out action forward to point to vendor address lookup.
        DisbursementVoucherInitForm dvForm = (DisbursementVoucherInitForm)form;
        
        if(StringUtils.isBlank(dvForm.getPayeeIdNumber())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DV_PAYEE_ID_FIELD_NAME, KFSKeyConstants.ERROR_DV_PAYEE_SELECTION_EMPTY);
            return super.refresh(mapping, dvForm, request, response);
        }
        
        if(dvForm.hasMultipleAddresses()) {
            return renderVendorAddressSelection(request, dvForm);
        }
        else {
            // Redirect to DV if there is only one vendor address
            return renderDisbursementVoucherMainPage(request, dvForm);
        }
    }

    /**
     * 
     * This method...
     * @param request
     * @param dvForm
     * @return
     */
    private ActionForward renderDisbursementVoucherMainPage(HttpServletRequest request, DisbursementVoucherInitForm dvForm) {
        Properties props = new Properties();
        props.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, "docHandler");
        props.put(KNSConstants.PARAMETER_COMMAND, "initiate");
        props.put(KNSConstants.DOCUMENT_TYPE_NAME, "DisbursementVoucherDocument");
        props.put(KFSPropertyConstants.PAYEE_ID_NUMBER, dvForm.getPayeeIdNumber());
        props.put(KFSPropertyConstants.PAYEE_TYPE_CODE, dvForm.getPayeeTypeCode());
        String url = UrlFactory.parameterizeUrl(getBasePath(request) + "/financialDisbursementVoucher.do", props);
        
        return new ActionForward(url, true);
    }

    /**
     * 
     * This method...
     * @param request
     * @param dvForm
     * @return
     */
    private ActionForward renderVendorAddressSelection(HttpServletRequest request, DisbursementVoucherInitForm dvForm) {
        Properties props = new Properties();
        props.put(KNSConstants.SUPPRESS_ACTIONS, Boolean.toString(true));
        props.put(KNSConstants.DOC_FORM_KEY, "88888888");
        props.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, VendorAddress.class.getName());
        props.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, "search");
        props.put(KNSConstants.LOOKUP_ANCHOR, KNSConstants.ANCHOR_TOP_OF_FORM);
        props.put(KNSConstants.LOOKED_UP_COLLECTION_NAME, "vendorAddresses");
        props.put(KNSConstants.CONVERSION_FIELDS_PARAMETER, "vendorAddressGeneratedIdentifier:vendorAddressGeneratedIdentifier,vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier");
        props.put(KFSPropertyConstants.ACTIVE, "Y");
        props.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, dvForm.getVendorHeaderGeneratedIdentifier());
        props.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, dvForm.getVendorDetailAssignedIdentifier());
        props.put(KNSConstants.RETURN_LOCATION_PARAMETER, getBasePath(request) + "/" + getReturnLocation());
        props.put(KNSConstants.BACK_LOCATION, getBasePath(request) + "/" + getReturnLocation());
        String url = UrlFactory.parameterizeUrl(getBasePath(request) + "/kr/" + KNSConstants.LOOKUP_ACTION, props);
        
        return new ActionForward(url, true);
    }
        
    /**
     * Clears the initial fields on the <code>PaymentRequestDocument</code> which should be accessible from the given form.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm, which must be a PaymentRequestForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward clearInitFields(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearInitFields() method");
        DisbursementVoucherInitForm dvForm = (DisbursementVoucherInitForm) form;
        dvForm.clearInitFields();

        return super.refresh(mapping, form, request, response);
    }

    /**
     * 
     * This method...
     * @return
     */
    private String getReturnLocation() {
        return "financialDisbursementVoucherInit.do";
    }
        
}

