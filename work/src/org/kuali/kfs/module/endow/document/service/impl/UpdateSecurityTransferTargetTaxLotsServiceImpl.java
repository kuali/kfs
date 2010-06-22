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

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.SecurityTransferDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.UpdateSecurityTransferTargetTaxLotsService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class UpdateSecurityTransferTargetTaxLotsServiceImpl implements UpdateSecurityTransferTargetTaxLotsService {

    private HoldingTaxLotService taxLotService;
    private SecurityService securityService;
    private KEMService kemService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.UpdateSecurityTransferTargetTaxLotsService#updateTransactionLineTaxLots(org.kuali.kfs.module.endow.document.SecurityTransferDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public void updateTransactionLineTaxLots(SecurityTransferDocument document, EndowmentTransactionLine transLine) {
        // calculate the transaction line amount

        // get the source transaction line and compute the unit value by dividing the source amount by the source number of units
        EndowmentSourceTransactionLine sourceTransactionLine = (EndowmentSourceTransactionLine) document.getSourceTransactionLines().get(0);

        if (ObjectUtils.isNotNull(sourceTransactionLine)) {
            BigDecimal sourceAmount = sourceTransactionLine.getTransactionAmount().bigDecimalValue();
            BigDecimal sourceUnits = sourceTransactionLine.getTransactionUnits().bigDecimalValue();
            BigDecimal unitValue = KEMCalculationRoundingHelper.divide(sourceAmount, sourceUnits, 5);

            BigDecimal targetAmount = KEMCalculationRoundingHelper.multiply(transLine.getTransactionUnits().bigDecimalValue(), unitValue, 2);
            // set transaction line target amount
            transLine.setTransactionAmount(new KualiDecimal(targetAmount));

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
                taxLotLine.setDocumentNumber(document.getDocumentNumber());
                taxLotLine.setDocumentLineNumber(transLine.getTransactionLineNumber());
                taxLotLine.setTransactionHoldingLotNumber(1);

            }

            taxLotLine.setLotUnits(transLine.getTransactionUnits().bigDecimalValue());
            taxLotLine.setLotHoldingCost(targetAmount);


            EndowmentTransactionSecurity endowmentTransactionSecurity = document.getTargetTransactionSecurity();

            Security security = securityService.getByPrimaryKey(endowmentTransactionSecurity.getSecurityID());

            // if security tax lot indicator is 'No'
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
            // if security tax lot indicator is 'Yes'
            else {
                taxLotLine.setLotAcquiredDate(kemService.getCurrentDate());
            }

            if (newLine) {
                transLine.getTaxLotLines().add(taxLotLine);
            }

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
