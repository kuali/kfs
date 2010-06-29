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
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * This class provides an implementation for the Tax Lot Lines in the Endowment transactional documents.
 */
public class EndowmentTransactionTaxLotLine extends PersistableBusinessObjectBase {
//  private KualiInteger lineNumber;
    private String documentNumber;
    private Integer documentLineNumber;
    private String documentLineTypeCode;
    private Integer transactionHoldingLotNumber;
    private BigDecimal lotUnits;
    private BigDecimal lotHoldingCost;
    private BigDecimal lotLongTermGainLoss;
    private BigDecimal lotShortTermGainLoss;
    private Date lotAcquiredDate;

    private EndowmentTransactionLine transactionLine;


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.TRANSACTION_LINE_DOCUMENT_NUMBER, this.documentNumber);
        m.put(EndowPropertyConstants.TRANSACTION_LINE_TYPE_CODE, this.documentLineTypeCode);
        m.put(EndowPropertyConstants.TRANSACTION_LINE_NUMBER, this.documentLineNumber);
        m.put(EndowPropertyConstants.TRANSACTION_HOLDING_LOT_NUMBER, this.transactionHoldingLotNumber);
        return m;

    }

    /**
     * Gets the lineNumber.
     * 
     * @return lineNumber
     */
/*    public KualiInteger getLineNumber() {
        return lineNumber;
    }
*/
    /**
     * Sets the lineNumber.
     * 
     * @param lineNumber
     */
/*    public void setLineNumber(KualiInteger lineNumber) {
        this.lineNumber = lineNumber;
    }
*/
    /**
     * Gets the documentNumber.
     * 
     * @return documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber.
     * 
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the documentLineNumber.
     * 
     * @return documentLineNumber
     */
    public Integer getDocumentLineNumber() {
        return documentLineNumber;
    }

    /**
     * Sets the documentLineNumber.
     * 
     * @param documentLineNumber
     */
    public void setDocumentLineNumber(Integer documentLineNumber) {
        this.documentLineNumber = documentLineNumber;
    }

    /**
     * Gets the documentLineTypeCode.
     * 
     * @return documentLineTypeCode
     */
    public String getDocumentLineTypeCode() {
        return documentLineTypeCode;
    }

    /**
     * Sets the documentLineTypeCode.
     * 
     * @param documentLineTypeCode
     */
    public void setDocumentLineTypeCode(String documentLineTypeCode) {
        this.documentLineTypeCode = documentLineTypeCode;
    }

    /**
     * Gets the transactionHoldingLotNumber.
     * 
     * @return transactionHoldingLotNumber
     */
    public Integer getTransactionHoldingLotNumber() {
        return transactionHoldingLotNumber;
    }

    /**
     * Sets the transactionHoldingLotNumber.
     * 
     * @param transactionHoldingLotNumber
     */
    public void setTransactionHoldingLotNumber(Integer transactionHoldingLotNumber) {
        this.transactionHoldingLotNumber = transactionHoldingLotNumber;
    }

    /**
     * Gets the lotUnits.
     * 
     * @return lotUnits
     */
    public BigDecimal getLotUnits() {
        return lotUnits;
    }

    /**
     * Sets the lotUnits.
     * 
     * @param lotUnits
     */
    public void setLotUnits(BigDecimal lotUnits) {
        this.lotUnits = lotUnits;
    }

    /**
     * Gets the lotHoldingCost.
     * 
     * @return lotHoldingCost
     */
    public BigDecimal getLotHoldingCost() {
        return lotHoldingCost;
    }

    /**
     * Sets the lotHoldingCost.
     * 
     * @param lotHoldingCost
     */
    public void setLotHoldingCost(BigDecimal lotHoldingCost) {
        this.lotHoldingCost = lotHoldingCost;
    }

    /**
     * Gets the lotLongTermGainLoss.
     * 
     * @return lotLongTermGainLoss
     */
    public BigDecimal getLotLongTermGainLoss() {
        return lotLongTermGainLoss;
    }

    /**
     * Sets the lotLongTermGainLoss.
     * 
     * @param lotLongTermGainLoss
     */
    public void setLotLongTermGainLoss(BigDecimal lotLongTermGainLoss) {
        this.lotLongTermGainLoss = lotLongTermGainLoss;
    }

    /**
     * Gets the lotShortTermGainLoss.
     * 
     * @return lotShortTermGainLoss
     */
    public BigDecimal getLotShortTermGainLoss() {
        return lotShortTermGainLoss;
    }

    /**
     * Sets the lotShortTermGainLoss.
     * 
     * @param lotShortTermGainLoss
     */
    public void setLotShortTermGainLoss(BigDecimal lotShortTermGainLoss) {
        this.lotShortTermGainLoss = lotShortTermGainLoss;
    }

    /**
     * Gets the transactionLine.
     * 
     * @return transactionLine
     */
    public EndowmentTransactionLine getTransactionLine() {
        return transactionLine;
    }

    /**
     * Sets the transactionLine.
     * 
     * @param transactionLine
     */
    public void setTransactionLine(EndowmentTransactionLine transactionLine) {
        this.transactionLine = transactionLine;
    }

    /**
     * Gets the lotAcquiredDate.
     * 
     * @return lotAcquiredDate
     */
    public Date getLotAcquiredDate() {
        return lotAcquiredDate;
    }

    /**
     * Sets the lotAcquiredDate.
     * 
     * @param lotAcquiredDate
     */
    public void setLotAcquiredDate(Date lotAcquiredDate) {
        this.lotAcquiredDate = lotAcquiredDate;
    }
}
