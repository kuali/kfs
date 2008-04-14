/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.pdp.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.PayeeAchAccount;
import org.kuali.module.pdp.service.AchInformation;
import org.kuali.module.pdp.service.AchService;

public class AchServiceImpl implements AchService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.pdp.service.AchService#getAchInformation(java.lang.String, java.lang.String, java.lang.String)
     */
    public AchInformation getAchInformation(String idType, String payeeId, String psdTransactionCode) {
        LOG.debug("getAchInformation() started");

        Map fields = new HashMap();
        fields.put("active", Boolean.TRUE);
        fields.put("payeeIdentifierTypeCode", idType);
        fields.put("psdTransactionCode", psdTransactionCode);
        if (PdpConstants.PayeeIdTypeCodes.EMPLOYEE_ID.equals(idType)) {
            fields.put("personUniversalIdentifier", payeeId);
        }
        else if (PdpConstants.PayeeIdTypeCodes.SSN.equals(idType)) {
            fields.put("payeeSocialSecurityNumber", payeeId);
        }
        else if (PdpConstants.PayeeIdTypeCodes.PAYEE_ID.equals(idType)) {
            fields.put("disbVchrPayeeIdNumber", payeeId);
        }
        else if (PdpConstants.PayeeIdTypeCodes.FEIN.equals(idType)) {
            fields.put("payeeFederalEmployerIdentificationNumber", payeeId);
        }
        else if (PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(idType)) {
            String parts[] = payeeId.split("-");
            if (parts.length == 2) {
                try {
                    fields.put("vendorHeaderGeneratedIdentifier", new Integer(Integer.parseInt(parts[0])));
                    fields.put("vendorDetailAssignedIdentifier", new Integer(Integer.parseInt(parts[1])));
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        Collection rows = businessObjectService.findMatching(PayeeAchAccount.class, fields);
        if (rows.size() != 1) {
            LOG.debug("getAchInformation() not found rows = " + rows.size());

            return null;
        }
        else {
            LOG.debug("getAchInformation() found");

            Iterator i = rows.iterator();
            PayeeAchAccount paa = (PayeeAchAccount) i.next();
            AchInformation ai = new AchInformation();
            ai.setAchAccountType("22"); // TODO Fix this
            ai.setAchBankAccountNbr(paa.getBankAccountNumber());
            ai.setAchBankRoutingNbr(paa.getBankRoutingNumber());
            ai.setAdviceEmailAddress(paa.getPayeeEmailAddress());
            ai.setDepartmentCode(paa.getPsdTransactionCode());
            ai.setIdType(paa.getPayeeIdentifierTypeCode());
            ai.setPayeeId(payeeId);
            return ai;
        }
    }

    public void setBusinessObjectService(BusinessObjectService bos) {
        businessObjectService = bos;
    }
}
