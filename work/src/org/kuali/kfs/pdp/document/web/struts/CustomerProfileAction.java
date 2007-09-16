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
package org.kuali.module.pdp.action.customerprofile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.bo.CustomerBank;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.DisbursementType;
import org.kuali.module.pdp.form.customerprofile.CustomerBankForm;
import org.kuali.module.pdp.form.customerprofile.CustomerProfileForm;
import org.kuali.module.pdp.service.CustomerProfileService;
import org.kuali.module.pdp.service.ReferenceService;
import org.kuali.module.pdp.service.SecurityRecord;

public class CustomerProfileAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileAction.class);

    private CustomerProfileService customerProfileService;
    private ReferenceService referenceService;

    public CustomerProfileAction() {
        super();
        setCustomerProfileService( SpringContext.getBean(CustomerProfileService.class) );
        setReferenceService( SpringContext.getBean(ReferenceService.class) );
    }

    public void setCustomerProfileService(CustomerProfileService c) {
        customerProfileService = c;
    }

    public void setReferenceService(ReferenceService r) {
        referenceService = r;
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isSysAdminRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("executeLogic() starting");
        String forward = "list";
        String profileId = request.getParameter("profile");

        if (profileId != null) {
            int cpId = -1;
            try {
                cpId = Integer.parseInt(profileId);
            } catch (NumberFormatException e) {
                // Bad number - we don't need to do anything here
            }
            LOG.debug("executeLogic() cpId = " + cpId);
            if (cpId == 0) {
                // Add a new Profile
                CustomerProfileForm newProfileForm = new CustomerProfileForm();
  
                List dtl = referenceService.getAll("DisbursementType");
                int i = 0;
                newProfileForm.setCustomerBankFormArraySize(dtl.size());
                for (Iterator iter = dtl.iterator(); iter.hasNext();) {
                    DisbursementType dt = (DisbursementType)iter.next();
                    CustomerBankForm cbf = new CustomerBankForm();
                    cbf.setDisbursementTypeCode(dt.getCode());
                    cbf.setDisbursementDescription(dt.getDescription());
                    newProfileForm.setCustomerBankForms(i,cbf);
                    i++;
                }
        
                request.setAttribute("PdpCustomerProfileForm",newProfileForm);
                forward = "edit";
            } else if ( cpId == -1 ) {
                // No Id or invalid customer profile ID, go back to the list
                List l = customerProfileService.getAll();
                request.setAttribute("customers",l);
                forward = "list";
            } else {
                // Load the customer profile to edit it
                CustomerProfile custProfile = customerProfileService.get(new Integer(profileId));
                CustomerProfileForm cpf = new CustomerProfileForm();
                List formList = new ArrayList();

                cpf.setForm(custProfile);
                LOG.debug("executeLogic() profile list of customerbanks is " + custProfile.getCustomerBanks());
        
                LOG.debug("executeLogic() CustomerProfileForm is " + cpf);

                List dtl = referenceService.getAll("DisbursementType");
                int i = 0;
                cpf.setCustomerBankFormArraySize(dtl.size());
                for (Iterator iter = dtl.iterator(); iter.hasNext();) {
                    DisbursementType dt = (DisbursementType)iter.next();
                    CustomerBankForm cbf = new CustomerBankForm();
                    cbf.setDisbursementTypeCode(dt.getCode());
                    cbf.setDisbursementDescription(dt.getDescription());
                    CustomerBank cb = custProfile.getCustomerBankByDisbursementType(dt.getCode());
                    if ( cb != null ) {
                        cbf.setBankId(cb.getBank().getId());
                    }
                    cpf.setCustomerBankForms(i, cbf);
                    i++;
                }

                request.setAttribute("PdpCustomerProfileForm",cpf);
                forward = "edit";
            }
        } else {
            List l = customerProfileService.getAll();
            request.setAttribute("customers",l);
        }

        return mapping.findForward(forward);
    }
}
