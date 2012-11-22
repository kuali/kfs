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
import org.kuali.kfs.module.tem.businessobject.CorporateCardPseudoNumber;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.businessobject.TEMProfileAccount;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;


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
        return getConfigurationService().getPropertyValueAsString(TemKeyConstants.CORP_CARD_DOCUMENT_USER_AGREEMENT);
    }
    public String getDepartmentHeadAgreementText() {
        return getConfigurationService().getPropertyValueAsString(TemKeyConstants.CORP_CARD_DOCUMENT_DEPT_HEAD_AGREEMENT);
    }


    @Override
    public void applyToBank() {
        // TODO Auto-generated method stub
        boolean generateNumber = getParameterService().getParameterValueAsBoolean(TemConstants.PARAM_NAMESPACE, TemConstants.CORP_CARD_APPLICATION, TemConstants.GENERATE_CC_NUMBER_IND);
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
     * @see org.kuali.rice.kns.document.DocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        String status = getAppDocStatus();
        if (this.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equalsIgnoreCase(DocumentStatus.FINAL.getCode())
                || this.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equalsIgnoreCase(DocumentStatus.PROCESSED.getCode())){
            TEMProfileAccount profileAccount = new TEMProfileAccount();
            profileAccount.setAccountNumber(this.getPseudoNumber());
            Date now = new Date();
            profileAccount.setEffectiveDate(new java.sql.Date(now.getTime()));
            String code = getParameterService().getParameterValueAsString(TemConstants.PARAM_NAMESPACE, TemConstants.CORP_CARD_APPLICATION, TemConstants.CORP_CARD_CODE);
            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE, code);
            List<CreditCardAgency> creditCardAgencyList = (List<CreditCardAgency>) getBusinessObjectService().findMatching(CreditCardAgency.class, fieldValues);
            CreditCardAgency creditCardAgency = creditCardAgencyList.get(0);
            profileAccount.setCreditCardAgency(creditCardAgency);
            profileAccount.setCreditCardAgencyId(creditCardAgency.getId());
            profileAccount.setName(creditCardAgency.getCreditCardOrAgencyName());

            profileAccount.setActive(true);
            String text = getConfigurationService().getPropertyValueAsString(TemKeyConstants.CARD_NOTE_TEXT);
            DateFormatter formatter = new DateFormatter();
            String note = MessageFormat.format(text, formatter.format(now), getDocumentHeader().getDocumentNumber());
            profileAccount.setNote(note);
            getTemProfile().getAccounts().add(profileAccount);
            getBusinessObjectService().save(temProfile);

        }
    }

}
