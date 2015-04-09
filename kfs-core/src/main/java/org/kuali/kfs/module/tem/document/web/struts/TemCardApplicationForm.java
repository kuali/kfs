/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.tem.document.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.document.CardApplicationDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;


public class TemCardApplicationForm extends FinancialSystemTransactionalDocumentFormBase {
    ConfigurationService ConfigurationService;
    private boolean fiscalOfficer           = false;
    private boolean travelManager           = false;
    private boolean appliedToBank           = false;
    private boolean initiator               = false;
    private boolean emptyAccount            = false;
    private boolean emptyProfile            = false;
    private boolean multipleApplications    = false;

    @Override
    public List<ExtraButton> getExtraButtons() {
        List<ExtraButton> extraButtons = super.getExtraButtons();

        if (isTravelManager()){
            String documentStatus = getDocument().getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus();
            if (TemWorkflowConstants.RouteNodeNames.PENDING_BANK_APPLICATION.equals(documentStatus)
                    || TemWorkflowConstants.RouteNodeNames.TRAVEL_OFFICE_REVIEW.equals(documentStatus)){
                this.getDocumentActions().remove(KRADConstants.KUALI_ACTION_CAN_APPROVE);
            }
            String applyToBankButtonURL = getConfigurationService().getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
            String submitButtonURL = getConfigurationService().getPropertyValueAsString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);

            if (TemWorkflowConstants.RouteNodeNames.PENDING_BANK_APPLICATION.equals(documentStatus)){
                addExtraButton("methodToCall.applyToBank", applyToBankButtonURL + "buttonsmall_applytobank.gif", "Apply To Bank");
            }
            else if (TemWorkflowConstants.RouteNodeNames.TRAVEL_OFFICE_REVIEW.equals(documentStatus)) {
                addExtraButton("methodToCall.submit", submitButtonURL + "buttonsmall_submit.gif", "Submit");
            }
        }

        return extraButtons;
    }

    public ConfigurationService getConfigurationService() {
        if (ConfigurationService == null){
            ConfigurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return ConfigurationService;
    }

    /**
     * Adds a new button to the extra buttons collection.
     *
     * @param property - property for button
     * @param source - location of image
     * @param altText - alternate text for button if images don't appear
     */
    protected void addExtraButton(String property, String source, String altText) {

        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
    }


    /**
     * Gets the fiscalOfficer attribute.
     *
     * @return Returns the fiscalOfficer
     */

    public boolean isFiscalOfficer() {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        CardApplicationDocument document = (CardApplicationDocument) this.getDocument();

        TemProfile profile = document.getTemProfile();
        if (profile != null){
            Account account = null;
            Map<String,String> fieldValues = new HashMap<String,String>();
            fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, profile.getDefaultChartCode());
            fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, profile.getDefaultAccount());
            account = getBusinessObjectService().findByPrimaryKey(Account.class, fieldValues);
            return currentUser.getPrincipalId().equals(account.getAccountFiscalOfficerSystemIdentifier());
        }
        else{
            return false;
        }
    }


    /**
     * Gets the travelManager attribute.
     *
     * @return Returns the travelManager
     */

    public boolean isTravelManager() {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        return SpringContext.getBean(TravelDocumentService.class).isTravelManager(currentUser);
    }


    /**
     * Gets the appliedToBank attribute.
     *
     * @return Returns the appliedToBank
     */

    public boolean isAppliedToBank() {
        CardApplicationDocument document = (CardApplicationDocument) this.getDocument();
        if(TemWorkflowConstants.RouteNodeNames.APPLIED_TO_BANK.equals(document.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus())
                || TemWorkflowConstants.RouteNodeNames.APPROVED.equals(document.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus())
                || TemWorkflowConstants.RouteNodeNames.APPROVED_BY_BANK.equals(document.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus())
                || TemWorkflowConstants.RouteNodeNames.DECLINED.equals(document.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus())){
            setAppliedToBank(true);
        }
        return appliedToBank;
    }

    /**
     * Sets the appliedToBank attribute.
     *
     * @param appliedToBank The appliedToBank to set.
     */
    public void setAppliedToBank(boolean appliedToBank) {
        this.appliedToBank = appliedToBank;
    }

    /**
     * Gets the initiator attribute.
     *
     * @return Returns the initiator
     */

    public boolean isInitiator() {
        return initiator;
    }

    /**
     * Sets the initiator attribute.
     *
     * @param initiator The initiator to set.
     */
    public void setInitiator(boolean initiator) {
        this.initiator = initiator;
    }

    /**
     * Gets the emptyAccount attribute.
     *
     * @return Returns the emptyAccount
     */

    public boolean isEmptyAccount() {
        return emptyAccount;
    }

    /**
     * Sets the emptyAccount attribute.
     *
     * @param emptyAccount The emptyAccount to set.
     */
    public void setEmptyAccount(boolean emptyAccount) {
        this.emptyAccount = emptyAccount;
    }

    /**
     * Gets the emptyProfile attribute.
     *
     * @return Returns the emptyProfile
     */

    public boolean isEmptyProfile() {
        return emptyProfile;
    }

    /**
     * Gets the multipleApplications attribute.
     *
     * @return Returns the multipleApplications
     */

    public boolean isMultipleApplications() {
        return multipleApplications;
    }

    /**
     * Sets the multipleApplications attribute.
     *
     * @param multipleApplications The multipleApplications to set.
     */
    public void setMultipleApplications(boolean multipleApplications) {
        this.multipleApplications = multipleApplications;
    }

    /**
     * Sets the emptyProfile attribute.
     *
     * @param emptyProfile The emptyProfile to set.
     */
    public void setEmptyProfile(boolean emptyProfile) {
        this.emptyProfile = emptyProfile;
    }

    protected String getStatusCodeFieldName() {
        return "dummyAppDocStatus";
    }

    @Override
    public void populateHeaderFields(WorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        final CardApplicationDocument document = (CardApplicationDocument) getDocument();

        String status = TemWorkflowConstants.RouteNodeNames.APPLICATION;
        String appDocStatus = document.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus();

        getDocInfo().add(new HeaderField(getDataDictionaryAttributeName(getStatusCodeFieldName()), StringUtils.isBlank(appDocStatus)?status:appDocStatus));
    }

    /**
     * Add necessary boilerplate prefix and suffix to lookup a DataDictionary Header field
     *
     * @param attrName the name of the attribute you're looking for
     */
    protected String getDataDictionaryAttributeName(final String attrName) {
        return new StringBuilder("DataDictionary.").append(getDocument().getClass().getSimpleName()).append(".attributes.").append(attrName).toString();
    }

    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }
}
