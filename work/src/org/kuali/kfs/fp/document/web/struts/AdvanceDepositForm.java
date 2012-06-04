/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.fp.businessobject.AdvanceDepositDetail;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;

/**
 * This class is the struts form for Advance Deposit document.
 */
public class AdvanceDepositForm extends CapitalAccountingLinesFormBase implements CapitalAssetEditable {
    protected AdvanceDepositDetail newAdvanceDeposit;
    protected List<CapitalAssetInformation> capitalAssetInformation;

    /**
     * Constructs a AdvanceDepositForm.java.
     */
    public AdvanceDepositForm() {
        super();

        AdvanceDepositDetail advanceDepositDetail = new AdvanceDepositDetail();
        advanceDepositDetail.setDefaultBankCode();
        setNewAdvanceDeposit(advanceDepositDetail);

        capitalAssetInformation = new ArrayList<CapitalAssetInformation>();
        this.capitalAccountingLine.setCanCreateAsset(false); //This document can only edit asset information
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "AD";
    }
    
    /**
     * @return AdvanceDepositDocument
     */
    public AdvanceDepositDocument getAdvanceDepositDocument() {
        return (AdvanceDepositDocument) getDocument();
    }

    /**
     * @return AdvanceDepositDetail
     */
    public AdvanceDepositDetail getNewAdvanceDeposit() {
        return newAdvanceDeposit;
    }

    /**
     * @param newAdvanceDeposit
     */
    public void setNewAdvanceDeposit(AdvanceDepositDetail newAdvanceDeposit) {
        this.newAdvanceDeposit = newAdvanceDeposit;
    }

    /**
     * Overrides the parent to call super.populate and then tells each line to check the associated data dictionary and modify the
     * values entered to follow all the attributes set for the values of the accounting line.
     * 
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        // now run through all of the accounting lines and make sure they've been uppercased and populated appropriately
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(getNewAdvanceDeposit());

        List<AdvanceDepositDetail> advancedDeposits = getAdvanceDepositDocument().getAdvanceDeposits();
        for (AdvanceDepositDetail detail : advancedDeposits) {
            SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(detail);
        }
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#getCapitalAssetInformation()
     */
    public List<CapitalAssetInformation> getCapitalAssetInformation() {
        return capitalAssetInformation;
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#setCapitalAssetInformation(org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     */
    public void setCapitalAssetInformation(List<CapitalAssetInformation> capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }
}
