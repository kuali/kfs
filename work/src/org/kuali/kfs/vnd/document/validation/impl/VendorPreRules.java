/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.vendor.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.rules.MaintenancePreRulesBase;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.bo.VendorHeader;
import org.kuali.module.vendor.bo.VendorTaxChange;

/**
 * 
 * PreRules checks for the VendorDetail that needs to occur while still in the 
 * Struts processing. This includes setting the vendorName field using the
 * values from vendorLastName and vendorFirstName, and could be used for
 * many other purposes.
 * 
 */
public class VendorPreRules extends MaintenancePreRulesBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VendorPreRules.class);

    private VendorDetail newVendorDetail;
    private VendorDetail copyVendorDetail;
    private DateTimeService dateTimeService;
    private String universalUserId;
    
    public VendorPreRules() {

    }
    
    public String getUniversalUserId() {
        if (ObjectUtils.isNull(universalUserId)) {
            this.universalUserId = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        }
        return this.universalUserId;
    }

    public void setUniversalUserId(String universalUserId) {
        this.universalUserId = universalUserId;
    }

    protected DateTimeService getDateTimeService() {
        if( ObjectUtils.isNull( this.dateTimeService ) ) {
            this.dateTimeService = SpringServiceLocator.getDateTimeService();
        }
        return this.dateTimeService;
    }
    
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        setVendorNamesAndIndicator(document);
        setVendorRestriction(document);
        setVendorTaxChange(document);
        return true;
    }
 
    /**
     * 
     * This method sets the convenience objects like newVendorDetail and oldVendorDetail, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {
        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newVendorDetail = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();
        copyVendorDetail = (VendorDetail) ObjectUtils.deepCopy(newVendorDetail);
        copyVendorDetail.refresh();
    }

    /**
     * 
     * This method sets the vendorFirstLastNameIndicator to true if the first name and
     * last name fields were filled in but the vendorName field is blank and it sets
     * the vendorFirstLastNameIndicator to false if the vendorName field is filled in
     * and the first name and last name fields were both blank.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    private void setVendorNamesAndIndicator(MaintenanceDocument document) {
        if (StringUtils.isBlank(copyVendorDetail.getVendorName()) &&
            !StringUtils.isBlank(copyVendorDetail.getVendorFirstName()) &&
            !StringUtils.isBlank(copyVendorDetail.getVendorLastName())) {

            newVendorDetail.setVendorFirstLastNameIndicator(true);
            newVendorDetail.setVendorFirstName(removeDelimiter(newVendorDetail.getVendorFirstName()));
            newVendorDetail.setVendorLastName(removeDelimiter(newVendorDetail.getVendorLastName()));
            
        } else if (!StringUtils.isBlank(copyVendorDetail.getVendorName()) &&
                   StringUtils.isBlank(copyVendorDetail.getVendorFirstName()) &&
                   StringUtils.isBlank(copyVendorDetail.getVendorLastName())) {
            newVendorDetail.setVendorFirstLastNameIndicator(false);
        }
    }
    
    /**
     * This method set the vendorRestrictedDate and vendorRestrictedPersonIdentifier
     * if the vendor restriction has changed from No to Yes.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    private void setVendorRestriction(MaintenanceDocument document) {
        VendorDetail oldVendorDetail = (VendorDetail)document.getOldMaintainableObject().getBusinessObject();
        Boolean oldVendorRestrictedIndicator = null;
        if( ObjectUtils.isNotNull( oldVendorDetail ) ) {
            oldVendorRestrictedIndicator = oldVendorDetail.getVendorRestrictedIndicator();
        }
        //If the Vendor Restricted Indicator will change, change the date and person id appropriately.
        if( ( ObjectUtils.isNull(oldVendorRestrictedIndicator) || (!oldVendorRestrictedIndicator) ) 
            && ObjectUtils.isNotNull(newVendorDetail.getVendorRestrictedIndicator())
            && newVendorDetail.getVendorRestrictedIndicator() ) {
            //Indicator changed from (null or false) to true.
            newVendorDetail.setVendorRestrictedDate( getDateTimeService().getCurrentSqlDate() );
            newVendorDetail.setVendorRestrictedPersonIdentifier( getUniversalUserId() );
        } else if( ObjectUtils.isNotNull(oldVendorRestrictedIndicator)
                   && oldVendorRestrictedIndicator
                   && ObjectUtils.isNotNull(newVendorDetail.getVendorRestrictedIndicator()) 
                   && (!newVendorDetail.getVendorRestrictedIndicator())) {
            //Indicator changed from true to false.
            newVendorDetail.setVendorRestrictedDate( null );
            newVendorDetail.setVendorRestrictedPersonIdentifier( null );
        }
        
    }
    
    /**
     * This method create the VendorTaxChange if there are any changes related to
     * either the tax number or the tax type code, set the tax change to the list
     * of changes and set the list to the new vendor detail
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    private void setVendorTaxChange(MaintenanceDocument document) {
        VendorDetail oldVendorDetail = (VendorDetail)document.getOldMaintainableObject().getBusinessObject();
        VendorHeader newVendorHeader = newVendorDetail.getVendorHeader();
        //If this is a pre-existing parent vendor, and if the Tax Number or the Tax Type Code will change, 
        //log the change in the Tax Change table.
        if( newVendorDetail.isVendorParentIndicator() ) {
            VendorHeader oldVendorHeader = oldVendorDetail.getVendorHeader();
      
            if( ObjectUtils.isNotNull( oldVendorHeader ) ) {  //Does not apply if this is a new parent vendor.
                String oldVendorTaxNumber = oldVendorHeader.getVendorTaxNumber();
                String oldVendorTaxTypeCode = oldVendorHeader.getVendorTaxTypeCode();
                
                String vendorTaxNumber = newVendorHeader.getVendorTaxNumber();
                String vendorTaxTypeCode = newVendorHeader.getVendorTaxTypeCode();          
                               
                if( (!StringUtils.equals( vendorTaxNumber, oldVendorTaxNumber )) ||
                    (!StringUtils.equals( vendorTaxTypeCode, oldVendorTaxTypeCode )) ) {
                    VendorTaxChange taxChange = new VendorTaxChange( newVendorDetail.getVendorHeaderGeneratedIdentifier(), 
                            getDateTimeService().getCurrentSqlDate(), oldVendorTaxNumber, oldVendorTaxTypeCode, getUniversalUserId() );
                    List<VendorTaxChange> changes = newVendorHeader.getVendorTaxChanges();
                    if( ObjectUtils.isNull( changes ) ) {
                        changes = new ArrayList();
                    }
                    changes.add( taxChange );
                    newVendorHeader.setVendorTaxChanges( changes );
                }
            }
        }        
    }
    
    /**
     * This method is a helper method to remove all the delimiters from the vendor name
     * 
     * @param str the original vendorName
     * 
     * @return result String the vendorName after the delimiters have been removed
     */
    private String removeDelimiter(String str) {
        String result = str.replaceAll(VendorConstants.NAME_DELIM, KFSConstants.BLANK_SPACE);
        return result;
    }


    @Override
    public boolean doRules(Document document) {
        VendorDetail vendorDetail = (VendorDetail) document.getDocumentBusinessObject();
        boolean proceed = super.doRules(document);
        
        if (proceed) {
            String questionText = SpringServiceLocator.getKualiConfigurationService().getPropertyString(VendorConstants.ACKNOWLEDGE_NEW_VENDOR_INFO_TEXT);
            questionText = questionText.replace("{0}", vendorDetail.getVendorName());
            questionText = questionText.replace("{1}", document.getDocumentNumber());
            proceed = super.askOrAnalyzeYesNoQuestion(VendorConstants.ACKNOWLEDGE_NEW_VENDOR_INFO, questionText);
        }

        if (!proceed) {
            abortRulesCheck();
        }

        return proceed;
    }

}
