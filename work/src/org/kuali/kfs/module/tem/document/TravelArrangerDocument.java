/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class TravelArrangerDocument extends FinancialSystemTransactionalDocumentBase {
    
    private Integer profileId;
    private String arrangerId;
    private String travelerName;
    private Boolean taInd = Boolean.FALSE;
    private Boolean trInd = Boolean.FALSE;
    private Boolean resign = Boolean.FALSE;
    private Boolean primaryInd = Boolean.FALSE;
    
    private TEMProfile profile;
    private Person arranger;
    
    /**
     * Gets the profileId attribute. 
     * @return Returns the profileId.
     */
    public Integer getProfileId() {
        return profileId;
    }
    /**
     * Sets the profileId attribute value.
     * @param profileId The profileId to set.
     */
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }
    /**
     * Gets the arrangerId attribute. 
     * @return Returns the arrangerId.
     */
    public String getArrangerId() {
        return arrangerId;
    }
    /**
     * Sets the arrangerId attribute value.
     * @param arrangerId The arrangerId to set.
     */
    public void setArrangerId(String arrangerId) {
        this.arrangerId = arrangerId;
    }
    
    
    /**
     * Gets the travelerName attribute. 
     * @return Returns the travelerName.
     */
    public String getTravelerName() {
        if(ObjectUtils.isNotNull(profile)) {
            return profile.getName();
        } else {
            return "";
        }
            
    }
    
    /**
     * Sets the travelerName attribute value.
     * @param travelerName The travelerName to set.
     */
    public void setTravelerName(String travelerName) {
        this.travelerName = travelerName;
    }
    /**
     * Gets the taInd attribute. 
     * @return Returns the taInd.
     */
    public Boolean getTaInd() {
        return taInd;
    }
    /**
     * Sets the taInd attribute value.
     * @param taInd The taInd to set.
     */
    public void setTaInd(Boolean taInd) {
        this.taInd = taInd;
    }
    /**
     * Gets the trInd attribute. 
     * @return Returns the trInd.
     */
    public Boolean getTrInd() {
        return trInd;
    }
    /**
     * Sets the trInd attribute value.
     * @param trInd The trInd to set.
     */
    public void setTrInd(Boolean trInd) {
        this.trInd = trInd;
    }
    
    /**
     * Gets the resign attribute. 
     * @return Returns the resign.
     */
    public Boolean getResign() {
        return resign;
    }
    /**
     * Sets the resign attribute value.
     * @param resign The resign to set.
     */
    public void setResign(Boolean resign) {
        this.resign = resign;
    }
    
    
    
    /**
     * Gets the primaryInd attribute. 
     * @return Returns the primaryInd.
     */
    public Boolean getPrimaryInd() {
        return primaryInd;
    }
    /**
     * Sets the primaryInd attribute value.
     * @param primaryInd The primaryInd to set.
     */
    public void setPrimaryInd(Boolean primaryInd) {
        this.primaryInd = primaryInd;
    }
    /**
     * Gets the profile attribute. 
     * @return Returns the profile.
     */
    public TEMProfile getProfile() {
        return profile;
    }
    /**
     * Sets the profile attribute value.
     * @param profile The profile to set.
     */
    public void setProfile(TEMProfile profile) {
        this.profile = profile;
    }
    /**
     * Gets the arranger attribute. 
     * @return Returns the arranger.
     */
    public Person getArranger() {
        return arranger;
    }
    /**
     * Sets the arranger attribute value.
     * @param arranger The arranger to set.
     */
    public void setArranger(Person arranger) {
        this.arranger = arranger;
    }
    
    public void initiateDocument() {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        this.setArrangerId(currentUser.getPrincipalId());
        this.setArranger(currentUser);
    }
    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {

        super.doRouteStatusChange(statusChangeEvent);
        if (KEWConstants.ROUTE_HEADER_FINAL_CD.equals(statusChangeEvent.getNewRouteStatus()) || KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
            //here is where we need to remove or add them as a new arranger
            if(this.resign) {
                getArrangerDocumentService().removeTravelProfileArranger(this);
            } else {
                getArrangerDocumentService().createTravelProfileArranger(this);
            }
        }
    }
    
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(TemWorkflowConstants.REQUIRES_TRAVELER_REVIEW))
            return requiresTravelerApprovalRouting();
        return false;
    }
    
    private boolean requiresTravelerApprovalRouting() {
        //If the arranger is not resigning then route to traveler
          if (!this.getResign()){
              this.refreshReferenceObject("profile");
              String principalId = this.getProfile().getPrincipalId();
              boolean routeToTraveler = false;
              
              if (principalId != null){
                  return true;
              }
          }
          return false;
      }

    protected TravelArrangerDocumentService getArrangerDocumentService() {
        return SpringContext.getBean(TravelArrangerDocumentService.class);
    }
    
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }
    
}
