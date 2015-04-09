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
import org.kuali.kfs.module.tem.businessobject.TemProfileAccount;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.kew.api.document.DocumentStatus;
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
        boolean generateNumber = getParameterService().getParameterValueAsBoolean(TemCorporateCardApplicationDocument.class, TemConstants.GENERATE_CREDIT_CARD_NUMBER_IND);
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
        DocumentStatus status = getDocumentHeader().getWorkflowDocument().getStatus();
        if (status.equals(DocumentStatus.FINAL) || status.equals(DocumentStatus.PROCESSED)){
            TemProfileAccount profileAccount = new TemProfileAccount();
            profileAccount.setAccountNumber(this.getPseudoNumber());
            Date now = new Date();
            profileAccount.setEffectiveDate(new java.sql.Date(now.getTime()));
            String code = getParameterService().getParameterValueAsString(TemCorporateCardApplicationDocument.class, TemConstants.CORPORATE_CARD_CODE);
            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE, code);
            List<CreditCardAgency> creditCardAgencyList = (List<CreditCardAgency>) getBusinessObjectService().findMatching(CreditCardAgency.class, fieldValues);
            CreditCardAgency creditCardAgency = creditCardAgencyList.get(0);
            profileAccount.setCreditCardAgency(creditCardAgency);
            profileAccount.setCreditCardOrAgencyCode(creditCardAgency.getCreditCardOrAgencyCode());
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
