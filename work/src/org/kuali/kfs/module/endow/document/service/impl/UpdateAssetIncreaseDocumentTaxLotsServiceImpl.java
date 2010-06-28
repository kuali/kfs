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
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.UpdateAssetIncreaseDocumentTaxLotsService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class provides an implementation for the transaction line related tax lots update for the AssetIncreaseDocument.
 */
public class UpdateAssetIncreaseDocumentTaxLotsServiceImpl implements UpdateAssetIncreaseDocumentTaxLotsService {

    private HoldingTaxLotService taxLotService;
    private SecurityService securityService;
    private KEMService kemService;


    /**
     * @see org.kuali.kfs.module.endow.document.service.UpdateAssetIncreaseDocumentTaxLotsService#updateTransactionLineTaxLots(org.kuali.kfs.module.endow.document.AssetIncreaseDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public void updateTransactionLineTaxLots(AssetIncreaseDocument aiDocument, EndowmentTransactionLine transLine) {
        EndowmentTransactionTaxLotLine taxLotLine = null;
        boolean newLine = false;

        // updating an existing tax lot
        if (transLine.getTaxLotLines() != null && transLine.getTaxLotLines().size() > 0) {
            // there is only one tax lot line per each transaction line
            taxLotLine = transLine.getTaxLotLines().get(0);
        }
        // or adding a new one
        else {
            // create and set a new tax lot line
            newLine = true;
            taxLotLine = new EndowmentTransactionTaxLotLine();
            taxLotLine.setDocumentNumber(aiDocument.getDocumentNumber());
            taxLotLine.setDocumentLineNumber(transLine.getTransactionLineNumber());
            taxLotLine.setTransactionHoldingLotNumber(1);

        }

        taxLotLine.setLotUnits(transLine.getTransactionUnits().bigDecimalValue());
        taxLotLine.setLotHoldingCost(transLine.getTransactionAmount().bigDecimalValue());


        EndowmentTransactionSecurity endowmentTransactionSecurity = aiDocument.getTargetTransactionSecurity();

        Security security = securityService.getByPrimaryKey(endowmentTransactionSecurity.getSecurityID());

        // if security tax lot indicator is 'No' and a tax lot exists for the kemid, security, registration code and income
        // principal indicator - set the lot acquired date to be the tax lot holding acquired date if units and cost is not zero;
        // otherwise set the date to be the current date
        if (ObjectUtils.isNotNull(security) && !security.getClassCode().isTaxLotIndicator()) {
            HoldingTaxLot holdingTaxLot = taxLotService.getByPrimaryKey(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), 1, transLine.getTransactionIPIndicatorCode());
            if (ObjectUtils.isNotNull(holdingTaxLot)) {
                if (holdingTaxLot.getUnits().equals(KualiDecimal.ZERO) && holdingTaxLot.getCost().equals(KualiDecimal.ZERO)) {
                    taxLotLine.setLotAcquiredDate(kemService.getCurrentDate());
                }
                else {
                    taxLotLine.setLotAcquiredDate(holdingTaxLot.getAcquiredDate());
                }
            }
            else {
                taxLotLine.setLotAcquiredDate(kemService.getCurrentDate());
            }

        }
        // if security tax lot indicator is 'Yes' set the lot acquired date to be the current date
        else {
            taxLotLine.setLotAcquiredDate(kemService.getCurrentDate());
        }

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

    /**
     * Gets the kemService.
     * 
     * @return kemService
     */
    public KEMService getKemService() {
        return kemService;
    }

    /**
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
}
