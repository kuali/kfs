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
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.maintenance.MaintenanceUtils;
import org.kuali.rice.krad.service.DocumentService;
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

    /**
     * Updates and saves the lastUpdate timestamp, also clears and saves the autoInactivationIndicator
     * if creating a new record or the existing record is activated/inactivated during editing.
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        LOG.debug("PayeeACHAccountMaintainable.saveBusinessObject()...");

        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        //DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        // Note:
        // We need to refer to the businessObject contained in this maintainable when setting the field values,
        // dont't use the old or new businessObject retrieved from the document, as they have DIFFERENT references.
        PayeeACHAccount payeeAchAccount = (PayeeACHAccount)getBusinessObject();

        // lastUpdate will be set by TimestampsBusinessObjectBase#prePersist called by OJB

        try {
            FinancialSystemMaintenanceDocument document = (FinancialSystemMaintenanceDocument) documentService.getByDocumentHeaderId(getDocumentNumber());
            PayeeACHAccount oldPayeeAchAccount = (PayeeACHAccount) document.getOldMaintainableObject().getBusinessObject();
            PayeeACHAccount newPayeeAchAccount = (PayeeACHAccount) document.getNewMaintainableObject().getBusinessObject();

            // if creating new record, or changed active indicator during editing, clear autoInactivationIndicator
            if (MaintenanceUtils.isMaintenanceDocumentCreatingNewRecord(getMaintenanceAction()) ||
                    newPayeeAchAccount.isActive() != oldPayeeAchAccount.isActive()) {
                payeeAchAccount.setAutoInactivationIndicator(false);
            }
        } catch (WorkflowException e) {
            LOG.error("Caught WorkflowException while saving PayeeACHAccount -> documentService.getByDocumentHeaderId(" + getDocumentNumber() + "). ", e);
            throw new RuntimeException(e);
        }

        // save new PayeeACHAccount record
        super.saveBusinessObject();
    }

}