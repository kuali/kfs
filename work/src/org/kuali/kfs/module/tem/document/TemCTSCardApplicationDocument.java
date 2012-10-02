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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.businessobject.TEMProfileAccount;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.web.format.DateFormatter;

public class TemCTSCardApplicationDocument extends CardApplicationDocumentBase implements CardApplicationDocument {
    protected static Logger LOG = Logger.getLogger(TemCTSCardApplicationDocument.class);
    
    private Date bankAppliedDate;
    private Date bankApprovedDate;
    
    public Date getBankAppliedDate() {
        return bankAppliedDate;
    }
    public void setBankAppliedDate(Date bankAppliedDate) {
        this.bankAppliedDate = bankAppliedDate;
    }
    public Date getBankApprovedDate() {
        return bankApprovedDate;
    }
    public void setBankApprovedDate(Date bankApprovedDate) {
        this.bankApprovedDate = bankApprovedDate;
    }
    
    @Override
    public void applyToBank() {
        setBankAppliedDate(new Date());
    }
    
    @Override
    public void approvedByBank() {
        setBankApprovedDate(new Date());
    }
    
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);    
        String status = getAppDocStatus();
        if (this.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equalsIgnoreCase(KEWConstants.ROUTE_HEADER_FINAL_CD)){
            TEMProfileAccount profileAccount = new TEMProfileAccount();
            Date now = new Date();
            profileAccount.setEffectiveDate(new java.sql.Date(now.getTime()));
            String code = getParameterService().getParameterValue(TemConstants.PARAM_NAMESPACE, TemConstants.TravelParameters.DOCUMENT_DTL_TYPE, TemConstants.CTS_CARD_CODE);
            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE, code);
            List<CreditCardAgency> creditCardAgencyList = (List<CreditCardAgency>) getBusinessObjectService().findMatching(CreditCardAgency.class, fieldValues);
            CreditCardAgency creditCardAgency = creditCardAgencyList.get(0);
            profileAccount.setCreditCardAgency(creditCardAgency);
            profileAccount.setCreditCardAgencyId(creditCardAgency.getId());
            profileAccount.setName(creditCardAgency.getCreditCardOrAgencyName());
            profileAccount.setActive(true);
            profileAccount.setAccountNumber(temProfile.getEmployeeId());
            String text = getKualiConfigurationService().getPropertyString(TemKeyConstants.CARD_NOTE_TEXT);
            DateFormatter formatter = new DateFormatter();
            String note = MessageFormat.format(text, formatter.format(now), getDocumentHeader().getDocumentNumber());
            profileAccount.setNote(note);
            getTemProfile().getAccounts().add(profileAccount);
            getBusinessObjectService().save(temProfile);
        }
    }
    @Override
    public String getUserAgreementText() {
        // TODO Auto-generated method stub
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(TemKeyConstants.CTS_CARD_DOCUMENT_USER_AGREEMENT);
    }
    
    
}
