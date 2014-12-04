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
