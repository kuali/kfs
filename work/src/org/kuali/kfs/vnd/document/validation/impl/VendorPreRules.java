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
package org.kuali.kfs.vnd.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorKeyConstants;
import org.kuali.kfs.vnd.VendorUtils;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorType;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business Prerules applicable to VendorDetail documents. These PreRules checks for the VendorDetail that needs to occur while
 * still in the Struts processing. This includes setting the vendorName field using the values from vendorLastName and
 * vendorFirstName, and could be used for many other purposes.
 */
public class VendorPreRules extends MaintenancePreRulesBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VendorPreRules.class);

    protected VendorDetail newVendorDetail;
    protected String personId;

    public VendorPreRules() {
    }

    /**
     * Returns the Universal User Id of the current logged-in user
     * 
     * @return String the PersonId
     */

    public String getPersonId() {
        if (ObjectUtils.isNull(personId)) {
            this.personId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        }
        return this.personId;
    }

    /**
     * Sets up a convenience object and few other vendor attributes
     * 
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        setVendorNamesAndIndicator(document);
        setVendorRestriction(document);
        if (StringUtils.isBlank(question) || (question.equals(VendorConstants.CHANGE_TO_PARENT_QUESTION_ID))) {
            detectAndConfirmChangeToParent(document);
        }
        
        //check to page review ONLY if it is a new vendor
        if (newVendorDetail.getVendorHeaderGeneratedIdentifier() == null &&
                newVendorDetail.getVendorDetailAssignedIdentifier() == null) {
            displayReview(document);
        }
        return true;
    }

    /**
     * Sets the convenience objects like newVendorDetail and oldVendorDetail, so you have short and easy handles to the new and old
     * objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load all
     * sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    protected void setupConvenienceObjects(MaintenanceDocument document) {
        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newVendorDetail = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();
    }

    /**
     * Sets the vendorFirstLastNameIndicator to true if the first name and last name fields were filled in but the vendorName field
     * is blank and it sets the vendorFirstLastNameIndicator to false if the vendorName field is filled in and the first name and
     * last name fields were both blank.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    protected void setVendorNamesAndIndicator(MaintenanceDocument document) {
        if (StringUtils.isBlank(newVendorDetail.getVendorName()) && !StringUtils.isBlank(newVendorDetail.getVendorFirstName()) && !StringUtils.isBlank(newVendorDetail.getVendorLastName())) {

            newVendorDetail.setVendorFirstLastNameIndicator(true);
            newVendorDetail.setVendorFirstName(removeDelimiter(newVendorDetail.getVendorFirstName()));
            newVendorDetail.setVendorLastName(removeDelimiter(newVendorDetail.getVendorLastName()));

        }
        else if (!StringUtils.isBlank(newVendorDetail.getVendorName()) && StringUtils.isBlank(newVendorDetail.getVendorFirstName()) && StringUtils.isBlank(newVendorDetail.getVendorLastName())) {
            newVendorDetail.setVendorFirstLastNameIndicator(false);
        }
    }

    /**
     * Sets the vendorRestrictedDate and vendorRestrictedPersonIdentifier if the vendor restriction has changed from No to Yes.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    protected void setVendorRestriction(MaintenanceDocument document) {
        VendorDetail oldVendorDetail = (VendorDetail) document.getOldMaintainableObject().getBusinessObject();
        Boolean oldVendorRestrictedIndicator = null;
        if (ObjectUtils.isNotNull(oldVendorDetail)) {
            oldVendorRestrictedIndicator = oldVendorDetail.getVendorRestrictedIndicator();
        }
        // If the Vendor Restricted Indicator will change, change the date and person id appropriately.
        if ((ObjectUtils.isNull(oldVendorRestrictedIndicator) || (!oldVendorRestrictedIndicator)) && ObjectUtils.isNotNull(newVendorDetail.getVendorRestrictedIndicator()) && newVendorDetail.getVendorRestrictedIndicator()) {
            // Indicator changed from (null or false) to true.
            if(newVendorDetail.getVendorRestrictedDate() == null){
                newVendorDetail.setVendorRestrictedDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            }           
            if(newVendorDetail.getVendorRestrictedPersonIdentifier() == null) {
                newVendorDetail.setVendorRestrictedPersonIdentifier(getPersonId());
            }
        }
        else if (ObjectUtils.isNotNull(oldVendorRestrictedIndicator) && oldVendorRestrictedIndicator && ObjectUtils.isNotNull(newVendorDetail.getVendorRestrictedIndicator()) && (!newVendorDetail.getVendorRestrictedIndicator())) {
            // Indicator changed from true to false.
            newVendorDetail.setVendorRestrictedDate(null);
            newVendorDetail.setVendorRestrictedPersonIdentifier(null);
        }

    }

    /**
     * This is a helper method to remove all the delimiters from the vendor name
     * 
     * @param str the original vendorName
     * @return result String the vendorName after the delimiters have been removed
     */
    protected String removeDelimiter(String str) {
        String result = str.replaceAll(VendorConstants.NAME_DELIM, KFSConstants.BLANK_SPACE);
        return result;
    }


    /**
     * Displays a review if indicated by the vendor type and the associated text from that type
     * 
     * @param document - vendordetail document
     */
    public void displayReview(MaintenanceDocument document) {
        VendorDetail vendorDetail = (VendorDetail)  document.getDocumentBusinessObject();

        VendorType vendorType = vendorDetail.getVendorHeader().getVendorType();

        if (vendorType == null) {
            vendorType = new VendorType();
            vendorType.setVendorTypeCode(vendorDetail.getVendorHeader().getVendorTypeCode());
            vendorType = (VendorType) SpringContext.getBean(BusinessObjectService.class).retrieve(vendorType);
        }
        if (vendorType != null && vendorType.isVendorShowReviewIndicator()) {
            String questionText = vendorType.getVendorReviewText();

            //
            // Only recognize the review request if the question text isn't null.
            // Why preview something that doesn't exist?
            //
            if (questionText != null) {

                if (vendorDetail.getVendorName() != null) {
                    questionText = questionText.replace("{0}", vendorDetail.getVendorName());
                }
                else {
                    questionText = questionText.replace("{0}", "(not entered)");
                }
                questionText = questionText.replace("{1}", document.getDocumentNumber());
                Boolean proceed = super.askOrAnalyzeYesNoQuestion(VendorConstants.ACKNOWLEDGE_NEW_VENDOR_INFO, questionText);

                if (!proceed) {
                    abortRulesCheck();
                }
            }
        }
    }
    
   /**
     * This method displays a review if indicated by the vendor type and the associated text from that type This method screens the
     * current document for changes from division vendor to parent vendor. If the document does contain such a change, the question
     * framework is invoked to obtain the user's confirmation for the change. If confirmation is obtained, a note is added to the
     * old parent vendor. Indicators are set appropriately.
     * 
     * @param document The vendor-change-containing MaintenanceDocument under examination
     */
    protected void detectAndConfirmChangeToParent(MaintenanceDocument document) {
        boolean proceed = true;
        VendorDetail oldVendorDetail = (VendorDetail) document.getOldMaintainableObject().getBusinessObject();
        boolean oldVendorIsParent = oldVendorDetail.isVendorParentIndicator();
        boolean newVendorIsParent = newVendorDetail.isVendorParentIndicator();
        if (!oldVendorIsParent && newVendorIsParent) {
            // A change to division is being tried. Obtain confirmation.
            VendorDetail oldParentVendor = SpringContext.getBean(VendorService.class).getParentVendor(oldVendorDetail.getVendorHeaderGeneratedIdentifier());
            String oldParentVendorName = oldParentVendor.getVendorName();
            String oldParentVendorNumber = oldParentVendor.getVendorNumber();
            proceed = askOrAnalyzeYesNoQuestion(VendorConstants.CHANGE_TO_PARENT_QUESTION_ID, VendorUtils.buildMessageText(VendorKeyConstants.CONFIRM_VENDOR_CHANGE_TO_PARENT, oldVendorDetail.getVendorName() + "  (" + oldVendorDetail.getVendorNumber() + ")", oldParentVendorName + " (" + oldParentVendorNumber + ")"));
            if (proceed) {
                newVendorDetail.setVendorParentIndicator(true);
            }
            else {
                newVendorDetail.setVendorParentIndicator(false);
            }
        }
        if (!proceed) {
            abortRulesCheck();
        }
    }

}

