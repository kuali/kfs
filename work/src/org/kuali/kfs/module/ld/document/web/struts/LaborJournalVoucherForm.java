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
package org.kuali.module.labor.web.struts.form;

import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.financial.document.JournalVoucherDocument;
import org.kuali.module.financial.web.struts.form.JournalVoucherForm;
import org.kuali.module.labor.LaborConstants.JournalVoucherOffsetType;
import org.kuali.module.labor.bo.PositionData;
import org.kuali.module.labor.document.LaborJournalVoucherDocument;

/**
 * Action form for the Labor Journal Voucher Document.
 */
public class LaborJournalVoucherForm extends JournalVoucherForm {
    private String originalOffsetTypeCode = JournalVoucherOffsetType.NO_OFFSET.typeCode;

    /**
     * Constructs a JournalVoucherForm instance.
     */
    public LaborJournalVoucherForm() {
        super();
        setDocument(new LaborJournalVoucherDocument());
        setSelectedBalanceType(new BalanceTyp());
        setOriginalBalanceType("");
    }

    /**
     * This method returns the journal voucher document associated with this form.
     * 
     * @return Returns the journalVoucherDocument.
     */
    public JournalVoucherDocument getJournalVoucherDocument() {
        return (LaborJournalVoucherDocument) getTransactionalDocument();
    }

    /**
     * This method sets the journal voucher document associated with this form.
     * 
     * @param journalVoucherDocument The journalVoucherDocument to set.
     */
    public void setJournalVoucherDocument(JournalVoucherDocument journalVoucherDocument) {
        setDocument(journalVoucherDocument);
    }

    /**
     * Gets the originalOffsetTypeCode attribute.
     * 
     * @return Returns the originalOffsetTypeCode.
     */
    public String getOriginalOffsetTypeCode() {
        return originalOffsetTypeCode;
    }

    /**
     * Sets the originalOffsetTypeCode attribute value.
     * 
     * @param originalOffsetTypeCode The originalOffsetTypeCode to set.
     */
    public void setOriginalOffsetTypeCode(String originalOffsetTypeCode) {
        this.originalOffsetTypeCode = originalOffsetTypeCode;
    }

    /**
     * Configure map for optional accounting line quickfinders.
     * 
     * @see org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase#getForcedLookupOptionalFields()
     */
    public Map getForcedLookupOptionalFields() {
        Map retval = super.getForcedLookupOptionalFields();

        String lookupField = KFSPropertyConstants.POSITION_NUMBER;
        retval.put(KFSPropertyConstants.POSITION_NUMBER, lookupField + ";" + PositionData.class.getName());

        lookupField = KFSPropertyConstants.PERSON_PAYROLL_IDENTIFIER;
        retval.put(KFSPropertyConstants.EMPLID, lookupField + ";" + UniversalUser.class.getName());

        return retval;
    }
}