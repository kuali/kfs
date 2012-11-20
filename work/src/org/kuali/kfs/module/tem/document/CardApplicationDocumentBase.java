/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.document.TransactionalDocumentBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

public class CardApplicationDocumentBase extends TransactionalDocumentBase implements CardApplicationDocument {
    protected static Logger LOG = Logger.getLogger(CardApplicationDocumentBase.class);
    protected TEMProfile temProfile;
    protected Integer temProfileId;
    protected boolean userAgreement;
    private String dummyAppDocStatus;
    
    public TEMProfile getTemProfile() {
        return temProfile;
    }
    public void setTemProfile(TEMProfile temProfile) {
        this.temProfile = temProfile;
    }
    public Integer getTemProfileId() {
        return temProfileId;
    }
    public void setTemProfileId(Integer temProfileId) {
        this.temProfileId = temProfileId;
    }
    public boolean isUserAgreement() {
        return userAgreement;
    }
    public void setUserAgreement(boolean userAgreement) {
        this.userAgreement = userAgreement;
    }

    @Override
    public String getUserAgreementText() {
        // TODO Auto-generated method stub
        return null;
    }

    
    
    /**
     * Gets the dummyAppDocStatus attribute.
     * 
     * @return Returns the dummyAppDocStatus
     */
    
    public String getDummyAppDocStatus() {
        return dummyAppDocStatus;
    }
    /**	
     * Sets the dummyAppDocStatus attribute.
     * 
     * @param dummyAppDocStatus The dummyAppDocStatus to set.
     */
    public void setDummyAppDocStatus(String dummyAppDocStatus) {
        this.dummyAppDocStatus = dummyAppDocStatus;
    }
    
    protected String zeroBuffer(Long number) {
        String numberStr = number.toString();
        while (numberStr.length() < 16){
            numberStr = "0" + numberStr;
        }
        return numberStr;
    }
    
    protected SequenceAccessorService getSequenceAccessorService() {
        return SpringContext.getBean(SequenceAccessorService.class);
    }
    
    protected WorkflowDocumentService getWorkflowDocumentService() {
        return SpringContext.getBean(WorkflowDocumentService.class);
    }
    
    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }
    
    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }
    
    protected KualiConfigurationService getKualiConfigurationService() {
        return SpringContext.getBean(KualiConfigurationService.class);
    }
    
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }
    
    public DocumentDao getDocumentDao() {
        return SpringContext.getBean(DocumentDao.class);
    }
    
    public String getAppDocStatus() {
        String status = getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus();
        return StringUtils.defaultIfEmpty(status, TemConstants.TRAVEL_DOC_APP_DOC_STATUS_INIT);
    }
    @Override
    public void applyToBank() {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void sendAcknowledgement() {
        getTravelDocumentService().addAdHocRecipient(this, getTemProfile().getPrincipalId(), KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
        try {
            SpringContext.getBean(WorkflowDocumentService.class).acknowledge(this.getDocumentHeader().getWorkflowDocument(), null, this.getAdHocRoutePersons());
        }
        catch (WorkflowException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        
    }
    @Override
    public void approvedByBank() {
        // TODO Auto-generated method stub
        
    }
    
    public boolean saveAppDocStatus() {
        boolean saved = false;
        try {
            getWorkflowDocumentService().save(getDocumentHeader().getWorkflowDocument(), null);
            saved = true;
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return saved;
    }
}
