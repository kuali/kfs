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

import java.sql.Date;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.businessobject.TemProfileAccount;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

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
        Calendar cal = Calendar.getInstance();
        setBankAppliedDate(new java.sql.Date(cal.getTimeInMillis()));
    }

    @Override
    public void approvedByBank() {
        Calendar cal = Calendar.getInstance();
        setBankApprovedDate(new java.sql.Date(cal.getTimeInMillis()));
    }

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        DocumentStatus status = getDocumentHeader().getWorkflowDocument().getStatus();
        if (status.equals(DocumentStatus.PROCESSED)){
            TemProfileAccount profileAccount = new TemProfileAccount();
            Calendar cal = Calendar.getInstance();
            profileAccount.setEffectiveDate(new java.sql.Date(cal.getTimeInMillis()));
            String code = getParameterService().getParameterValueAsString(TemCTSCardApplicationDocument.class, TemConstants.CENTRAL_TRAVEL_SYSTEM_CARD_CODE);
            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE, code);
            List<CreditCardAgency> creditCardAgencyList = (List<CreditCardAgency>) getBusinessObjectService().findMatching(CreditCardAgency.class, fieldValues);
            CreditCardAgency creditCardAgency = creditCardAgencyList.get(0);
            profileAccount.setCreditCardAgency(creditCardAgency);
            profileAccount.setCreditCardOrAgencyCode(creditCardAgency.getCreditCardOrAgencyCode());
            profileAccount.setName(creditCardAgency.getCreditCardOrAgencyName());
            profileAccount.setActive(true);
            profileAccount.setAccountNumber(temProfile.getEmployeeId());
            String text = getConfigurationService().getPropertyValueAsString(TemKeyConstants.CARD_NOTE_TEXT);
            DateFormatter formatter = new DateFormatter();
            String note = MessageFormat.format(text, formatter.format(new java.util.Date()), getDocumentHeader().getDocumentNumber());
            profileAccount.setNote(note);
            getTemProfile().getAccounts().add(profileAccount);
            getBusinessObjectService().save(temProfile);
        }
    }
    @Override
    public String getUserAgreementText() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.CTS_CARD_DOCUMENT_USER_AGREEMENT);
    }


}
