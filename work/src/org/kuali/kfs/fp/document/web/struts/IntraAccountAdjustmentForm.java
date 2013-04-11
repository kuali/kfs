/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.fp.document.IntraAccountAdjustmentDocument;

public class IntraAccountAdjustmentForm extends CapitalAccountingLinesFormBase implements CapitalAssetEditable{

    private static final long serialVersionUID = 1L;

    protected List<CapitalAssetInformation> capitalAssetInformation;

    /**
     * Constructs a IntraAccountAdjustmentForm instance and sets
     *  up the IntraAccountTransferofFunds document.
     */
    public IntraAccountAdjustmentForm() {
        super();
        this.setCapitalAssetInformation(new ArrayList<CapitalAssetInformation>());
        this.capitalAccountingLine.setCanCreateAsset(false); //This document can only edit asset information
    }

    /**
     * @return Returns the IntraAccountTransferofFunds document.
     */
    public IntraAccountAdjustmentDocument getTransferOfFundsDocument() {
        return (IntraAccountAdjustmentDocument) getDocument();
    }
    @Override
    protected String getDefaultDocumentTypeName() {
        return "IAA";
    }


    @Override
    public List<CapitalAssetInformation> getCapitalAssetInformation() {
        return capitalAssetInformation;
    }

    @Override
    public void setCapitalAssetInformation(List<CapitalAssetInformation> capitalAssetInformation) {
       this.capitalAssetInformation =capitalAssetInformation;

    }


}