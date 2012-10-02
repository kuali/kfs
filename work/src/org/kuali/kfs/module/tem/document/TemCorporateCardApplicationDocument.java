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


import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.CorporateCardPseudoNumber;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TEMProfileAccount;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.web.format.DateFormatter;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

public class TemCorporateCardApplicationDocument extends CardApplicationDocumentBase implements CardApplicationDocument {
    protected static Logger LOG = Logger.getLogger(TemCorporateCardApplicationDocument.class);

    private String pseudoNumber;
    private boolean departmentHeadAgreement;
    
    public String getPseudoNumber() {
        return pseudoNumber;
    }
    public void setPseudoNumber(String pseudoNumber) {
        this.pseudoNumber = pseudoNumber;
    }
    public boolean isDepartmentHeadAgreement() {
        return departmentHeadAgreement;
    }
    public void setDepartmentHeadAgreement(boolean departmentHeadAgreement) {
        this.departmentHeadAgreement = departmentHeadAgreement;
    }
    @Override
    public String getUserAgreementText() {
        // TODO Auto-generated method stub
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(TemKeyConstants.CORP_CARD_DOCUMENT_USER_AGREEMENT);
    }
    public String getDepartmentHeadAgreementText() {
        // TODO Auto-generated method stub
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(TemKeyConstants.CORP_CARD_DOCUMENT_DEPT_HEAD_AGREEMENT);
    }
    

    @Override
    public void applyToBank() {
        // TODO Auto-generated method stub
        boolean generateNumber = getParameterService().getIndicatorParameter(TemConstants.PARAM_NAMESPACE, TemConstants.TravelParameters.DOCUMENT_DTL_TYPE, TemConstants.GENERATE_CC_NUMBER_IND);
        if (generateNumber){
            Long number = getSequenceAccessorService().getNextAvailableSequenceNumber(TemConstants.TEM_CORP_CARD_PSEUDO_NUM_SEQ_NAME);
            String pseudoNumberStr = zeroBuffer(number);
            CorporateCardPseudoNumber pseudoNumber =  new CorporateCardPseudoNumber();
            getBusinessObjectService().save(pseudoNumber);
            this.setPseudoNumber(pseudoNumberStr);
            getDocumentDao().save(this);
            
        }
    }
    
    /**
     * @see org.kuali.rice.kns.document.DocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);    
        String status = getAppDocStatus();
        if (this.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equalsIgnoreCase(KEWConstants.ROUTE_HEADER_FINAL_CD)){
            TEMProfileAccount profileAccount = new TEMProfileAccount();
            profileAccount.setAccountNumber(this.getPseudoNumber());
            Date now = new Date();
            profileAccount.setEffectiveDate(new java.sql.Date(now.getTime()));
            String code = getParameterService().getParameterValue(TemConstants.PARAM_NAMESPACE, TemConstants.TravelParameters.DOCUMENT_DTL_TYPE, TemConstants.CORP_CARD_CODE);
            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE, code);
            List<CreditCardAgency> creditCardAgencyList = (List<CreditCardAgency>) getBusinessObjectService().findMatching(CreditCardAgency.class, fieldValues);
            CreditCardAgency creditCardAgency = creditCardAgencyList.get(0);
            profileAccount.setCreditCardAgency(creditCardAgency);
            profileAccount.setCreditCardAgencyId(creditCardAgency.getId());
            profileAccount.setName(creditCardAgency.getCreditCardOrAgencyName());
            
            profileAccount.setActive(true);
            String text = getKualiConfigurationService().getPropertyString(TemKeyConstants.CARD_NOTE_TEXT);
            DateFormatter formatter = new DateFormatter();
            String note = MessageFormat.format(text, formatter.format(now), getDocumentHeader().getDocumentNumber());
            profileAccount.setNote(note);
            getTemProfile().getAccounts().add(profileAccount);
            getBusinessObjectService().save(temProfile);
      
        }
    }

}
