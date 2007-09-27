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
package org.kuali.module.pdp.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.MDC;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RiceConstants;
import org.kuali.core.UserSession;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.service.PdpSecurityService;
import org.kuali.module.pdp.service.SecurityRecord;

import edu.iu.uis.eden.web.WebAuthenticationService;

/**
 * This Action will do most request processing for the PDP part of appliation.
 * Your action should override the proper methods to do it's work.
 * 
 * This idea and most of the concepts were stolen from the Scafold base action
 * mentioned in Struts in Action.  The bugs are probably entirely mine.
 * 
 * This should be refactored out of the application to make full use of the Kuali Request Processor.
 */
public abstract class BaseAction extends Action {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BaseAction.class);

    /**
     * User Service (to lookup users)
     */
    protected UniversalUserService userService = null;

    /**
     * Application Settings Service (to check global app settings)
     */
    protected KualiConfigurationService kualiConfigurationService = null;

    /**
     * Web Authentication Service (to do authentication)
     */
    protected WebAuthenticationService webAuthenticationService = null;

    /**
     * Security Service (to do authorization)
     */
    protected PdpSecurityService securityService = null;

    public BaseAction() {
    }

    /**
     * Struts execute method.  Don't override this unless you want to completely
     * replace the functionality of base action.
     */
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
        LOG.debug("execute() started");

        // For some reason, these don't always get set when they are in the constructor.  We'll get them here
        // the first time they are needed.
        if ( userService == null ) {
            setUniversalUserService( SpringContext.getBean(UniversalUserService.class) );
            setKualiConfigurationService( SpringContext.getBean(KualiConfigurationService.class) );
            setWebAuthenticationService( SpringContext.getBean(WebAuthenticationService.class) );
            setSecurityService( SpringContext.getBean(PdpSecurityService.class) );
        }

        ActionForward forward = null;

        HttpSession session = request.getSession();

        // Log4j settings
        ServletContext sc = request.getSession().getServletContext();
        MDC.put("user","-");

        // Check for precondition errors; fail if found
        LOG.debug("execute() calling preProcess()");
        forward = preProcess(mapping,form,request,response);
        if ( forward != null ) {
            LOG.debug("execute() preProcess returned forward");
            return forward;
        }

        // Do authentication
        forward = doAuthentication(mapping,form,request,response);
        if ( forward != null ) {
            LOG.debug("execute() doAuthentication returned forward");
            return forward;
        }

        // Do authorization (check again if backdoorId is set)
        SecurityRecord securityRecord = (SecurityRecord)session.getAttribute("SecurityRecord");
        String srUser = (String)session.getAttribute("SecurityRecordUser");
        if ( (securityRecord == null) || (srUser == null) || ! srUser.equals(getUser(request).getNetworkId()) ) {
            LOG.debug("execute() Security Check");
            securityRecord = securityService.getSecurityRecord(getUser(request));
            session.setAttribute("SecurityRecord",securityRecord);
            session.setAttribute("SecurityRecordUser", getUser(request).getNetworkId());
        }

        if ( ! isAuthorized(mapping,form,request,response) ) {
            LOG.debug("execute() Unauthorized");
            return mapping.findForward("pdp_unauthorized");
        }
    
        Timestamp disbExpireDate = getLastGoodDisbursementActionDate();
        if (disbExpireDate == null) {
            return forward;
        }
        session.setAttribute("DisbursementExpireDate", disbExpireDate);

        try {
            LOG.debug("execute() Calling executeLogic()");
            forward = executeLogic(mapping,form,request,response);
            LOG.debug("execute() executeLogic() returned forward '" + forward + "'");
        } catch (Exception e) {
            LOG.error("execute() Exception received.  Calling catchException", e);
            forward = catchException(mapping,form,request,response,e);
        } finally {
            LOG.debug("execute() Calling postProcess()");
            postProcess(mapping,form,request,response);
        }

        LOG.debug("execute() Returning");
        return forward;
    }

    protected Timestamp getLastGoodDisbursementActionDate() {
        LOG.debug("getLastGoodDisbursementActionDate() started");

        String daysStr = kualiConfigurationService.getParameterValue(KFSConstants.PDP_NAMESPACE, KFSConstants.Components.ALL, PdpConstants.ApplicationParameterKeys.DISBURSEMENT_ACTION_EXPIRATION_DAYS);
        int days = Integer.valueOf(daysStr);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, (days * -1));
        c.set(Calendar.HOUR, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.AM_PM, Calendar.AM);
        LOG.debug("getLastGoodDisbursementActionDate() Date being used is " + c.get(Calendar.MONTH) + "/" + c.get(Calendar.DATE) + "/" + c.get(Calendar.YEAR));
        return new Timestamp(c.getTimeInMillis());
    }

    public String whichButtonWasPressed(HttpServletRequest request) {
        String paramName = new String();

        Enumeration enumer = request.getParameterNames();
        while (enumer.hasMoreElements()) {
            paramName = (String)enumer.nextElement();
            if (paramName.startsWith("btn")) { // All the button names start with btn
                if ( paramName.indexOf(".") > -1 ) {
                    return paramName.substring(0,paramName.indexOf("."));
                } else {
                    return paramName;
                }
            }
        }
        return "";
    }

    /**
     * This method does authentication.  Override if you want to do
     * a different authentication or none.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    protected ActionForward doAuthentication(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
        LOG.debug("doAuthentication() started");

        HttpSession session = request.getSession();

        UserSession userSession = (UserSession)request.getSession().getAttribute(RiceConstants.USER_SESSION_KEY);

        // This is needed for PDP. At some point, PDP should be refactored to use UserSession
        session.setAttribute("user",new PdpUser(userSession.getUniversalUser()));

        MDC.put("user",userSession.getNetworkId());

        return null;
    }

    /**
     * This group authorization.  Override to change the authorization
     * type.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws SharedException
     */
    protected boolean isAuthorized(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
        LOG.debug("isAuthorized() Default method = returning true");
        return true;
    }

    /**
     * Get the user object for the user that is logged in.
     * This only works after doAuthentication() has run 
     * for the first request from a user.
     * 
     * @param request
     * @return The user object
     */
    protected PdpUser getUser(HttpServletRequest request) {
        return (PdpUser)request.getSession().getAttribute("user");
    }

    /**
     * Get the security record for the user that is logged in.
     * This only works after doAuthentication() is run.
     * 
     * @param request
     * @return The security record object
     */
    protected SecurityRecord getSecurityRecord(HttpServletRequest request) {
        return (SecurityRecord)request.getSession().getAttribute("SecurityRecord");
    }
  
    /**
     * Get the disbursement action expiration date where a disbursement
     * date before this date is not allowed to be cancelled or cancelled 
     * and reissued
     * 
     * @param request
     * @return The last disbursement date that is allowed for cancel
     */
    protected Timestamp getDisbursementActionExpirationDate(HttpServletRequest request) {
        return (Timestamp)request.getSession().getAttribute("DisbursementExpireDate");
    }

    /**
     * Execute the business logic for this Action.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request
     * @param request The HTTP request we are processing
     * @param response The resonse we are creating
     */
    protected abstract ActionForward executeLogic(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception;

    /**
     * Optional extension point for pre-processing.
     * Default method does nothing.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request
     * @param request The HTTP request we are processing
     * @param response The resonse we are creating
     */
    protected ActionForward preProcess(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
        // override to provide functionality
        LOG.info("preProcess() default preProcess()");
        return null;
    }

    /**
     * Optional extension point for post-processing.
     * Default method does nothing.
     * This is called from a finally{} clause,
     * and so is guaranteed to be called after executeLogic() or
     * catchException().
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request
     * @param request The HTTP request we are processing
     * @param response The resonse we are creating
     */
    protected void postProcess(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
        // override to provide functionality
        LOG.info("postProcess() default postProcess()");
    }

    /**
     * Process the exception handling for this Action.
     * Default method just returns a standard system error page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request
     * @param request The HTTP request we are processing
     * @param response The response we are creating
     */
    protected ActionForward catchException(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,Exception exception) {
        // override to provide functionality
        LOG.info("catchException() default catchException()");
        return mapping.findForward("pdp_system_error");
    }

    /**
     * This method returns the network id of the user accessing
     * this action.
     * 
     * @param request
     * @return
     */
    protected String getNetworkId(HttpServletRequest request) {
        PdpUser user = getUser(request);

        if ( user == null ) {
            LOG.error("getNetworkId() not authenticated");
            return null;
        } else {
            return user.getUniversalUser().getPersonUserIdentifier();
        }
    }

    /**
     * Have the user service set for us
     * @param us
     */
    public void setUniversalUserService(UniversalUserService us) {
        this.userService = us;
    }

    /**
     * Have the application settings service set for us
     * @param ass
     */
    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    /**
     * Have the web authentication service set for us
     * @param was
     */
    public void setWebAuthenticationService(WebAuthenticationService was) {
        this.webAuthenticationService = was;
    }

    /**
     * Have the security service set for us
     * @param ss
     */
    public void setSecurityService(PdpSecurityService ss) {
        this.securityService = ss;
    }
}
