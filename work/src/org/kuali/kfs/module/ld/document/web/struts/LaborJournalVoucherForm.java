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
package org.kuali.kfs.module.ld.document.web.struts;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.fp.document.JournalVoucherDocument;
import org.kuali.kfs.fp.document.web.struts.JournalVoucherForm;
import org.kuali.kfs.module.ld.LaborConstants.JournalVoucherOffsetType;
import org.kuali.kfs.module.ld.businessobject.PositionData;
import org.kuali.kfs.module.ld.document.LaborJournalVoucherDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;

/**
 * Struts Action Form for the Labor Journal Voucher Document.
 */
public class LaborJournalVoucherForm extends JournalVoucherForm {

    private String originalOffsetTypeCode = JournalVoucherOffsetType.NO_OFFSET.typeCode;

    /**
     * Constructs a JournalVoucherForm instance.
     */
    public LaborJournalVoucherForm() {
        super();
    }

    /**
     * Populates source a accounting line encumbrance code (D, R or null) for a given source accounting line 
     * 
     * @see org.kuali.kfs.fp.document.web.struts.JournalVoucherForm#populateSourceAccountingLineEncumbranceCode(org.kuali.kfs.sys.businessobject.SourceAccountingLine)
     */
    @Override
    protected void populateSourceAccountingLineEncumbranceCode(SourceAccountingLine sourceLine) {
        BalanceType selectedBalanceType = getSelectedBalanceType();
        if (selectedBalanceType != null && StringUtils.isNotBlank(selectedBalanceType.getCode())) {
            sourceLine.setBalanceTyp(selectedBalanceType);
            sourceLine.setBalanceTypeCode(selectedBalanceType.getCode());

            /*
             * set the encumbrance update code appropriately. If balance type is an external encumbrance, update code can be D or R.
             * For all other encumbrance types it must be D. If the balance type is not an encumbrance type the update code is set
             * to null
             */
            if (selectedBalanceType.isFinBalanceTypeEncumIndicator()) {
                if (StringUtils.isBlank(sourceLine.getEncumbranceUpdateCode()) || !KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(sourceLine.getEncumbranceUpdateCode())) {
                    sourceLine.setEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
                }
            }
            else {
                sourceLine.setEncumbranceUpdateCode(null);
            }
        }
        else {
            // it's the first time in, the form will be empty the first time in set up default selection value
            selectedBalanceType = SpringContext.getBean(BalanceTypeService.class).getBalanceTypeByCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            setSelectedBalanceType(selectedBalanceType);
            setOriginalBalanceType(selectedBalanceType.getCode());

            sourceLine.setEncumbranceUpdateCode(null);
        }
    }

    /**
     * Returns the journal voucher document associated with this form.
     * 
     * @return Returns the journalVoucherDocument.
     */
    public JournalVoucherDocument getJournalVoucherDocument() {
        return (LaborJournalVoucherDocument) getTransactionalDocument();
    }

    /**
     * Sets the journal voucher document associated with this form.
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
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#getForcedLookupOptionalFields()
     */
    
//    public Map getForcedLookupOptionalFields() {
//        Map retval = super.getForcedLookupOptionalFields();

//        String lookupField = KFSPropertyConstants.POSITION_NUMBER;
//        retval.put(KFSPropertyConstants.POSITION_NUMBER, lookupField + ";" + PositionData.class.getName());

//        lookupField = KFSPropertyConstants.PERSON_PAYROLL_IDENTIFIER;
//        retval.put(KFSPropertyConstants.EMPLID, lookupField + ";" + Person.class.getName());

//        return retval;
//    }
}

