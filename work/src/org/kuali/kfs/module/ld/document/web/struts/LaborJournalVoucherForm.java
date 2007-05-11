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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.financial.document.JournalVoucherDocument;
import org.kuali.module.financial.web.struts.form.JournalVoucherForm;
import org.kuali.module.financial.web.struts.form.VoucherForm;
import org.kuali.module.labor.LaborConstants.JournalVoucherOffsetType;
import org.kuali.module.labor.document.LaborJournalVoucherDocument;

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
     * @return Returns the originalOffsetTypeCode.
     */
    public String getOriginalOffsetTypeCode() {
        return originalOffsetTypeCode;
    }

    /**
     * Sets the originalOffsetTypeCode attribute value.
     * @param originalOffsetTypeCode The originalOffsetTypeCode to set.
     */
    public void setOriginalOffsetTypeCode(String originalOffsetTypeCode) {
        this.originalOffsetTypeCode = originalOffsetTypeCode;
    }
}