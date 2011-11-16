/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.web.struts;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This class handles Actions for the balance inquiry report menu
 */
public class KualiBalanceInquiryReportMenuAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiBalanceInquiryReportMenuAction.class);

    /**
     * Entry point to balance inquiry menu, forwards to jsp for rendering.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Returns back to calling document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBalanceInquiryReportMenuForm balanceInquiryReportMenuForm = (KualiBalanceInquiryReportMenuForm) form;

        String backUrl = balanceInquiryReportMenuForm.getBackLocation() + "?methodToCall=refresh&docFormKey=" + balanceInquiryReportMenuForm.getDocFormKey();
        return new ActionForward(backUrl, true);
    }

    /**
     * Needs to overrided to inject the real value into the docFormKey b/c otherwise the lookup's refresh back to this menu
     * overwrites the original value that we actually need. It too leverages the docFormKey.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBalanceInquiryReportMenuForm balanceInquiryReportMenuForm = (KualiBalanceInquiryReportMenuForm) form;

        // need to inject the real value into the docFormKey b/c otherwise the lookup's refresh back to this menu overwrites
        // the original value that we actually need.
        balanceInquiryReportMenuForm.setDocFormKey(balanceInquiryReportMenuForm.getBalanceInquiryReportMenuCallerDocFormKey());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Takes care of storing the action form in the user session and forwarding to the balance inquiry lookup action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performBalanceInquiryLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);

        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform lookup, no business object class name specified.");
        }

        // build the parameters for the lookup url
        Properties parameters = new Properties();
        String conversionFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        if (StringUtils.isNotBlank(conversionFields)) {
            parameters.put(KFSConstants.CONVERSION_FIELDS_PARAMETER, conversionFields);
        }

        // pass values from form that should be pre-populated on lookup search
        String parameterFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if (StringUtils.isNotBlank(parameterFields)) {
            String[] lookupParams = parameterFields.split(KFSConstants.FIELD_CONVERSIONS_SEPERATOR);

            for (int i = 0; i < lookupParams.length; i++) {
                String[] keyValue = lookupParams[i].split(KFSConstants.FIELD_CONVERSION_PAIR_SEPERATOR);

                // hard-coded passed value
                if (StringUtils.contains(keyValue[0], "'")) {
                    parameters.put(keyValue[1], StringUtils.replace(keyValue[0], "'", ""));
                }
                // passed value should come from property
                else if (StringUtils.isNotBlank(request.getParameter(keyValue[0]))) {
                    parameters.put(keyValue[1], request.getParameter(keyValue[0]));
                }
            }
        }

        // grab whether or not the "return value" link should be hidden or not
        String hideReturnLink = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM3_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL);
        if (StringUtils.isNotBlank(hideReturnLink)) {
            parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, hideReturnLink);
        }

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(KFSConstants.LOOKUP_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // determine what the action path is
        String actionPath = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM4_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM4_RIGHT_DEL);
        if (StringUtils.isBlank(actionPath)) {
            throw new IllegalStateException("The \"actionPath\" attribute is an expected parameter for the <gl:balanceInquiryLookup> tag - it " + "should never be blank.");
        }

        // now add required parameters
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, "start");
        parameters.put(KFSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form));
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);
        parameters.put(KFSConstants.RETURN_LOCATION_PARAMETER, basePath + mapping.getPath() + ".do");

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + actionPath, parameters);

        return new ActionForward(lookupUrl, true);
    }
}
