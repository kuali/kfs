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
import org.kuali.kfs.module.endow.document.LiabilityIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.UpdateAssetIncreaseDocumentTaxLotsService;
import org.kuali.kfs.module.endow.document.service.UpdateLiabilityIncreaseDocumentTaxLotsService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class...
 */
public class UpdateLiabilityIncreaseDocumentTaxLotsServiceImpl implements UpdateLiabilityIncreaseDocumentTaxLotsService {

    private HoldingTaxLotService taxLotService;
    private SecurityService securityService;
    private KEMService kemService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.UpdateAssetIncreaseDocumentTaxLotsService#updateTransactionLineTaxLots(boolean,
     *      org.kuali.kfs.module.endow.document.AssetIncreaseDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public void updateTransactionLineTaxLots(boolean isSource, LiabilityIncreaseDocument document, EndowmentTransactionLine transLine) 
    {
        EndowmentTransactionTaxLotLine taxLotLine = null;

        if (transLine.getTaxLotLines() != null && transLine.getTaxLotLines().size() > 0) {
            // there is only one tax lot line per each transaction line
            taxLotLine = transLine.getTaxLotLines().get(0);
        }
        else {
            // Create and set a new tax lot line
            taxLotLine = new EndowmentTransactionTaxLotLine();
            //taxLotLine.setDocumentNumber(aiDocument.getDocumentNumber());
            //taxLotLine.setDocumentLineNumber(transLine.getTransactionLineNumber());
            taxLotLine.setTransactionHoldingLotNumber(1);
            
            //Adding Taxlot line
            transLine.getTaxLotLines().add(taxLotLine);

        }

        //Updating data in case of refresh     
        taxLotLine.setLotUnits(transLine.getTransactionUnits());
        taxLotLine.setLotHoldingCost(transLine.getTransactionAmount());

        String securityID = null;
        String registrationCode = null;
        if(isSource)
        {
            securityID = document.getSourceTransactionSecurity().getSecurityID();
            registrationCode = document.getSourceTransactionSecurity().getRegistrationCode();
        }
        else
        {
            securityID = document.getTargetTransactionSecurity().getSecurityID();
            registrationCode = document.getTargetTransactionSecurity().getRegistrationCode();
        }
            
        HoldingTaxLot holdingTaxLot = taxLotService.getByPrimaryKey(transLine.getKemid(), securityID, registrationCode, 1, transLine.getTransactionIPIndicatorCode());
        if (ObjectUtils.isNotNull(holdingTaxLot))
        {
            if (holdingTaxLot.getUnits().equals(KualiDecimal.ZERO) && holdingTaxLot.getCost().equals(KualiDecimal.ZERO)) 
            {
                taxLotLine.setLotAcquiredDate(kemService.getCurrentDate());
            }
            else
            {
                taxLotLine.setLotAcquiredDate(holdingTaxLot.getAcquiredDate());
            }
        }
        else
        {
            taxLotLine.setLotAcquiredDate(kemService.getCurrentDate());
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

    public KEMService getKemService() {
        return kemService;
    }

    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
    
}
