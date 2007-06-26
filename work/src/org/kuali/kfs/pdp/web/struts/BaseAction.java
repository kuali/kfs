/*
 * Created on Feb 20, 2004
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
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.service.PdpSecurityService;
import org.kuali.module.pdp.service.SecurityRecord;

import edu.iu.uis.eden.web.WebAuthenticationService;

/**
 * @author jsissom
 *
 * This Action will do most request processing for this appliation.
 * Your action should override the proper methods to do it's work.
 * 
 * This idea and most of the concepts were stolen from the Scafold base action
 * mentioned in Struts in Action.  The bugs are probably entirely mine.
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
        setUniversalUserService( SpringServiceLocator.getUniversalUserService() );
        setKualiConfigurationService( SpringServiceLocator.getKualiConfigurationService() );
        setWebAuthenticationService( SpringServiceLocator.getWebAuthenticationService() );
        setSecurityService( (PdpSecurityService)SpringServiceLocator.getService("pdpPdpSecurityService") );
    }

    /**
     * Struts execute method.  Don't override this unless you want to completely
     * replace the functionality of base action.
     */
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
        LOG.debug("execute() started");

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
        if ( (securityRecord == null) || (request.getParameter("backdoorId") != null) ) {
            LOG.debug("execute() Security Check");
            securityRecord = securityService.getSecurityRecord(getUser(request));
            session.setAttribute("SecurityRecord",securityRecord);
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

        String daysStr = kualiConfigurationService.getApplicationParameterValue(PdpConstants.PDP_APPLICATION, PdpConstants.ApplicationParameterKeys.DISBURSEMENT_ACTION_EXPIRATION_DAYS);
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

        String actualUserId = webAuthenticationService.getNetworkId(request);
        PdpUser user = (PdpUser)session.getAttribute("user");

        // Is back door needed and enabled?
        String backdoorId = request.getParameter("backdoorId");
    
        if ( (backdoorId != null) && isBackDoorAllowed() ) {
            if ( ! backdoorId.equals(actualUserId)) {
                LOG.debug("doAuthentication() Backdoor requested");
                try {
                    UniversalUser u = userService.getUniversalUserByAuthenticationUserId(backdoorId);
                    if ( u != null ) {
                        user = new PdpUser(u);
                    }
                } catch (UserNotFoundException e) {
                    LOG.error("doAuthentication() Sponsored user with no empl ID " + backdoorId);
                    return mapping.findForward("pdp_authentication_error");
                }
                if ( user != null ) {
                    LOG.debug("doAuthentication() backdoor active");
                    session.setAttribute("backdoor","Y");
                    actualUserId = backdoorId;
                    session.setAttribute("user",user);
                } else {
                    LOG.error("doAuthentication() Unable to find backdoor user " + backdoorId);
                    return mapping.findForward("pdp_authentication_error");
                }
            } else {
                session.removeAttribute("backdoor");
                user = null;
            }
        }

        if ( user == null ) {
            LOG.debug("doAuthentication() Regular login");

            try {
                UniversalUser u = userService.getUniversalUserByAuthenticationUserId(actualUserId);
                if ( u != null ) {
                    user = new PdpUser(u);
                }
            } catch (UserNotFoundException e) {
                LOG.error("doAuthentication() GDS user with no empl ID " + actualUserId, e);
                return mapping.findForward("pdp_authentication_error");
            }
            if ( user == null ) {
                LOG.error("doAuthentication() Unable to find user " + actualUserId);
                return mapping.findForward("pdp_authentication_error");
            } else {
                session.setAttribute("user",user);      
            }
        }

        MDC.put("user",user.getUniversalUser().getPersonUserIdentifier());
        return null;
    }

    /**
     * Check to see if this request is operating under a backdoor account.
     * @param request
     * @return
     */
    protected boolean isBackDoorEnabled(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object bd = session.getAttribute("backdoor");
        return ( bd != null );
    }

    /**
     * This checks the database to see if it is ok to have a back door.
     * @return
     */
    private boolean isBackDoorAllowed() {
        Boolean b = kualiConfigurationService.getPropertyAsBoolean("BACKDOOR");
        if ( b == null ) {
            return false;
        } else {
            return b.booleanValue();
        }
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
