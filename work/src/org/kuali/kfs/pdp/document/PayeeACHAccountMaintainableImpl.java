/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.pdp.document;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpConstants.PayeeIdTypeCodes;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.util.ObjectUtils;

public class PayeeACHAccountMaintainableImpl extends FinancialSystemMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeACHAccountMaintainableImpl.class);

    @Override
    public List<Section> getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        PayeeACHAccount payeeACHAccount = (PayeeACHAccount) document.getNewMaintainableObject().getBusinessObject();
        String payeeIdTypeCode = payeeACHAccount.getPayeeIdentifierTypeCode();
        List<Section> sections = super.getSections(document, oldMaintainable);
        for (Section section : sections) {
            if (section.getSectionId().equalsIgnoreCase(PdpConstants.PayeeACHAccountDocumentStrings.EDIT_PAYEE_ACH_ACCOUNT)) {
                for (Row row : section.getRows()) {
                    for (Field field : row.getFields()) {
                        if (field.getFieldLabel().equalsIgnoreCase(PdpConstants.PayeeACHAccountDocumentStrings.PAYEE_EMAIL_ADDRESS)) {
                            if (ObjectUtils.isNull(payeeIdTypeCode) || (!StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.EMPLOYEE) && !StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.ENTITY))) {
                                field.setFieldRequired(true);
                            }else{
                                field.setFieldRequired(false);
                            }
                        }
                        if (field.getFieldLabel().equalsIgnoreCase(PdpConstants.PayeeACHAccountDocumentStrings.PAYEE_NAME)) {
                            if (ObjectUtils.isNull(payeeIdTypeCode) || (!StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.EMPLOYEE) && !StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.ENTITY) && !StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.VENDOR_ID))) {
                                field.setFieldRequired(true);
                            }else{
                                field.setFieldRequired(false);
                            }
                        }
                    }
                }
            }
        }

        return sections;
    }
}