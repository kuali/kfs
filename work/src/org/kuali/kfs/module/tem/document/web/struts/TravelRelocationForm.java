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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.document.web.bean.TravelRelocationMvcWrapperBean;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Travel Relocation Form
 */
public class TravelRelocationForm extends TravelFormBase implements TravelRelocationMvcWrapperBean {

    public static Logger LOG = Logger.getLogger(TravelRelocationForm.class);

    private List<Serializable> history;
    private List<AccountingDistribution> distribution;

    private Date startDate;
    private Date endDate;

    private boolean canReturn;
    private boolean canCertify;
    private boolean canUnmask = false;

    private String travelDocumentIdentifier;

    /**
     * Constructor
     */
    public TravelRelocationForm() {
        super();
    }

    /**
    *
    */
    @Override
    public void populate(final HttpServletRequest request) {
        // get original dates
        final Date startDateIn = getTravelRelocationDocument().getTripBegin();
        final Date endDateIn = getTravelRelocationDocument().getTripEnd();

        // populate new stuff
        super.populate(request);

        final Date currentStart = getTravelRelocationDocument().getTripBegin();
        final Date currentEnd = getTravelRelocationDocument().getTripEnd();
        if (currentStart != null) {
            setStartDate(currentStart);
        }
        if (currentEnd != null) {
            setEndDate(currentEnd);
        }
    }

    /**
     * Creates a MAP for all the buttons to appear on the Travel Relocation Form, and sets the attributes of these buttons.
     *
     * @return the button map created.
     */
    protected Map<String, ExtraButton> createButtonsMap() {
        final HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();

        // New Relocation button
        ExtraButton newRelocationButton = new ExtraButton();
        newRelocationButton.setExtraButtonProperty("methodToCall.newRelocation");
        newRelocationButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_newrelocation.png");
        newRelocationButton.setExtraButtonAltText("New Relocation");

        result.put(newRelocationButton.getExtraButtonProperty(), newRelocationButton);

        result.putAll(createPaymentExtraButtonMap());

        return result;
    }

    @Override
    public List<ExtraButton> getExtraButtons() {
        super.getExtraButtons();
        final Map<String, ExtraButton> buttonsMap = createButtonsMap();
        LOG.debug("Creating button map");

        if (!SpringContext.getBean(TravelDocumentService.class).isUnsuccessful(this.getTravelDocument())) {
            if (getTravelRelocationDocument().canPayDVToVendor()) {
                extraButtons.add(buttonsMap.get("methodToCall.payDVToVendor"));
            }
        }

        if (getDocumentActions().keySet().contains(TemConstants.TravelAuthorizationActions.CAN_NEW_RELOCATION)) {
            extraButtons.add(buttonsMap.get("methodToCall.newRelocation"));
        }

        return extraButtons;
    }


    @Override
    public boolean isDefaultOpenPaymentInfoTab() {
      String appDocStatus = getDocument().getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus();
      if(TemConstants.TravelRelocationStatusCodeKeys.AWAIT_RELO_MANAGER.equals(appDocStatus)) {
          return true;
      }

      return super.isDefaultOpenPaymentInfoTab();

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
     * Get Travel Relocation Document
     *
     * @return TravelRelocationForm
     */
    @Override
    public TravelRelocationDocument getTravelRelocationDocument() {
        return (TravelRelocationDocument) getDocument();
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
        return "RELO";
    }

    @Override
    public List<Serializable> getHistory() {
        return this.history;
    }

    @Override
    public void setHistory(final List<Serializable> history) {
        this.history = history;
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
        this.endDate = endDate;
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

    public boolean isCanUnmask() {
        return canUnmask;
    }

    public void setCanUnmask(boolean canUnmask) {
        this.canUnmask = canUnmask;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelFormBase#getTravelPaymentFormAction()
     */
    @Override
    public String getTravelPaymentFormAction() {
        return TemConstants.TRAVEL_RELOCATION_ACTION_NAME;
    }

    /**
     * @return the travel document identifier if it has been set
     */
    public String getTravelDocumentIdentifier() {
        return travelDocumentIdentifier;
    }

    /**
     * Sets the travel document identifier to populate from
     * @param travelDocumentIdentifier the travel document identifier to populate from
     */
    public void setTravelDocumentIdentifier(String travelDocumentIdentifier) {
        this.travelDocumentIdentifier = travelDocumentIdentifier;
    }

}
