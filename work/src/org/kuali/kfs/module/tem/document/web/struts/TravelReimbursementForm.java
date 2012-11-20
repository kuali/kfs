/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.ENABLE_VENDOR_PAYMENT_BEFORE_FINAL_TR_APPROVAL_IND;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.document.web.bean.TravelReimbursementMvcWrapperBean;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Travel Reimbursement Form
 * 
 */
public class TravelReimbursementForm extends TravelFormBase implements TravelReimbursementMvcWrapperBean {   
    private List<Serializable> history;
    private List<TravelAdvance> invoices;
    private Date startDate;
    private Date endDate;

    private boolean canReturn;
    private boolean canCertify;
    private boolean canUnmask = false;
    private TravelAdvance newTravelAdvanceLine;
    
    /**
     * Constructor
     */
    public TravelReimbursementForm() {
        super();                
        setInvoices(new ArrayList<TravelAdvance>());       
    }

    /**
     *
     */
    @Override
    public void populate(final HttpServletRequest request) {
        
        //get original dates
        final Date startDateIn = getTravelReimbursementDocument().getTripBegin();
        final Date endDateIn = getTravelReimbursementDocument().getTripEnd();

        //populate new stuff
        super.populate(request);
        /*if (getTravelReimbursementDocument().getActualExpenses() == null ||
        getTravelReimbursementDocument().getActualExpenses().isEmpty()) {
        if (!this.getMethodToCall().equals(KFSConstants.SAVE_METHOD)
                && !this.getMethodToCall().equals(KFSConstants.ROUTE_METHOD)
                && !this.getMethodToCall().equals(KFSConstants.BLANKET_APPROVE_METHOD)){
            getDocument().refreshReferenceObject(TemPropertyConstants.OTHER_TRAVEL_EXPENSES);
        }
        }*/

        final Date currentStart = getTravelReimbursementDocument().getTripBegin();
        final Date currentEnd = getTravelReimbursementDocument().getTripEnd();
        if (currentStart != null) {
            setStartDate(currentStart);    
        }
        if (currentEnd != null) {
            setEndDate(currentEnd);
        }
        
   }

    /**
     * Creates a MAP for all the buttons to appear on the Travel Authorization Form, and sets the attributes of these buttons.
     * 
     * @return the button map created.
     */
    protected Map<String, ExtraButton> createButtonsMap() {
        final HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();
        
        result.putAll(createDVExtraButtonMap());
        return result;
    }
 
    @Override
    public List<ExtraButton> getExtraButtons() {
        super.getExtraButtons();
        final Map<String, ExtraButton> buttonsMap = createButtonsMap();

        boolean enablePayments = getParameterService().getIndicatorParameter(PARAM_NAMESPACE, TravelReimbursementParameters.PARAM_DTL_TYPE, ENABLE_VENDOR_PAYMENT_BEFORE_FINAL_TR_APPROVAL_IND);
        if (enablePayments && !SpringContext.getBean(TravelDocumentService.class).isUnsuccessful(this.getTravelDocument())){
            if (getTravelReimbursementDocument().canPayDVToVendor()) {                 
                extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.payDVToVendor"));
            }
        }
        
        return extraButtons;
    }
    
    @Override
    public boolean canCertify() {
        return canCertify;
    }
    
    @Override
    public boolean getCanCertify() {
        return canCertify;
    }

    @Override
    public void setCanCertify(final boolean canCertify) {
        this.canCertify = canCertify;
    }
   
    /**
     * Get Travel Reimbursement Document
     * 
     * @return TravelReimbursementForm
     */
    @Override
    public TravelReimbursementDocument getTravelReimbursementDocument() {
        return (TravelReimbursementDocument) getDocument();
    }

    /**
     * Retrieve the name of the document identifier field for datadictionary queries
     *
     * @return String with the field name of the document identifier
     */
    @Override
    protected String getDocumentIdentifierFieldName() {
        return "travelDocumentIdentifier";
    }
    
    @Override
    protected String getDefaultDocumentTypeName() {
        return "TR";
    }

    @Override
    public List<Serializable> getHistory() {
        return this.history;
    }

    @Override
    public void setHistory(final List<Serializable> history) {
        this.history = history;
    }

    @Override
    public void setInvoices(final List<TravelAdvance> invoices) {
        this.invoices = invoices;
    }

    @Override
    public List<TravelAdvance> getInvoices() {
        return this.invoices;
    }

    /**
     * Gets the startDate attribute.
     * 
     * @return Returns the startDate.
     */
    @Override
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the startDate attribute value.
     * 
     * @param startDate The startDate to set.
     */
    @Override
    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the endDate attribute.
     * 
     * @return Returns the endDate.
     */
    @Override
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the endDate attribute value.
     * 
     * @param endDate The endDate to set.
     */
    @Override
    public void setEndDate(final Date endDate) {
        try{
            this.endDate = endDate;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    protected TravelService getTravelService() {
        return SpringContext.getBean(TravelService.class);
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        return SpringContext.getBean(TravelReimbursementService.class);
    }

    public PersonService getPersonService() {
        return SpringContext.getBean(PersonService.class);
    }

    protected TravelAuthorizationService getTravelAuthorizationService() {
        return SpringContext.getBean(TravelAuthorizationService.class);
    }

    @Override
    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    public TravelAdvance getNewTravelAdvanceLine() {
        return newTravelAdvanceLine;
    }

    public void setNewAdvanceLine(TravelAdvance newTravelAdvanceLine) {
        this.newTravelAdvanceLine = newTravelAdvanceLine;
    }

    public boolean isCanUnmask() {
        return canUnmask;
    }

    public void setCanUnmask(boolean canUnmask) {
        this.canUnmask = canUnmask;
    }
}
