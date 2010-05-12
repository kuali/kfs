/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.UpdateAssetIncreaseDocumentTaxLotsService;

/**
 * This class...
 */
public class UpdateAssetIncreaseDocumentTaxLotsServiceImpl implements UpdateAssetIncreaseDocumentTaxLotsService {

    private HoldingTaxLotService taxLotService;
    private SecurityService securityService;


    /**
     * @see org.kuali.kfs.module.endow.document.service.UpdateAssetIncreaseDocumentTaxLotsService#updateTransactionLineTaxLots(boolean,
     *      org.kuali.kfs.module.endow.document.AssetIncreaseDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public void updateTransactionLineTaxLots(boolean isSource, AssetIncreaseDocument aiDocument, EndowmentTransactionLine transLine) {
        EndowmentTransactionTaxLotLine taxLotLine = null;
        boolean newLine = false;

        if (transLine.getTaxLotLines() != null && transLine.getTaxLotLines().size() > 0) {
            // there is only one tax lot line per each transaction line
            taxLotLine = transLine.getTaxLotLines().get(0);
        }
        else {
            // create and set a new tax lot line
            newLine = true;
            taxLotLine = new EndowmentTransactionTaxLotLine();
            taxLotLine.setDocumentNumber(aiDocument.getDocumentNumber());
            taxLotLine.setDocumentLineNumber(transLine.getTransactionLineNumber());
            taxLotLine.setTransactionHoldingLotNumber(1);

        }

        taxLotLine.setLotUnits(transLine.getTransactionUnits());
        taxLotLine.setLotHoldingCost(transLine.getTransactionAmount());


        // EndowmentTransactionSecurity endowmentTransactionSecurity = aiDocument.getTargetTransactionSecurity();
        // Security security = securityService.getByPrimaryKey(endowmentTransactionSecurity.getSecurityID());
        //
        // if (!security.getClassCode().isTaxLotIndicator()) {
        // HoldingTaxLot holdingTaxLot = taxLotService.getByPrimaryKey(transLine.getKemid(),
        // endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), 1,
        // transLine.getTransactionIPIndicatorCode());
        // taxLotLine.set
        // }
        // else {
        //
        // }

        if (newLine) {
            transLine.getTaxLotLines().add(taxLotLine);
        }

    }

    /**
     * Gets the taxLotService.
     * 
     * @return taxLotService
     */
    public HoldingTaxLotService getTaxLotService() {
        return taxLotService;
    }

    /**
     * Sets the taxLotService.
     * 
     * @param taxLotService
     */
    public void setTaxLotService(HoldingTaxLotService taxLotService) {
        this.taxLotService = taxLotService;
    }

    /**
     * Gets the securityService.
     * 
     * @return securityService
     */
    public SecurityService getSecurityService() {
        return securityService;
    }

    /**
     * Sets the securityService.
     * 
     * @param securityService
     */
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
