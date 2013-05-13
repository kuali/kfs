/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

public interface EndowmentTransactionLine extends PersistableBusinessObject {

    /**
     * Get the documentNumber
     * 
     * @return documentNumber
     */
    public String getDocumentNumber();

    /**
     * Set the documentNumber
     * 
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber);

    /**
     * Gets the transactionLineTypeCode.
     * 
     * @return transactionLineTypeCode
     */
    public String getTransactionLineTypeCode();

    /**
     *Sets the transactionLineTypeCode.
     * 
     * @param transactionLineTypeCode
     */
    public void setTransactionLineTypeCode(String transactionLineTypeCode);

    /**
     * Gets the transactionLineNumber.
     * 
     * @return transactionLineNumber
     */
    public Integer getTransactionLineNumber();

    /**
     * Sets the transactionLineNumber.
     * 
     * @param transactionLineNumber
     */
    public void setTransactionLineNumber(Integer transactionLineNumber);

    /**
     * @return Returns the kemid.
     */
    public String getKemid();

    /**
     * @param kemid The kemid to set.
     */
    public void setKemid(String kemid);

    /**
     * @return Returns the kemid object.
     */
    public KEMID getKemidObj();

    /**
     * @param kemidObj The kemid object to set.
     */
    public void setKemidObj(KEMID kemidObj);

    /**
     * @return Returns the endowment transaction type code.
     */
    public String getEtranCode();

    /**
     * @param endowmentTransactionTypeCode The endowment transaction type code to set.
     */
    public void setEtranCode(String endowmentTransactionTypeCode);

    /**
     * @return Returns the EndowmentTransactionCode object.
     */
    public EndowmentTransactionCode getEtranCodeObj();

    /**
     * @param etranCodeObj the EndowmentTransactionCode object to set.
     */
    public void setEtranCodeObj(EndowmentTransactionCode etranCodeObj);

    /**
     * @return Returns the transaction line description.
     */
    public String getTransactionLineDescription();

    /**
     * @param description The transaction line description to set.
     */
    public void setTransactionLineDescription(String description);

    /**
     * @return Returns the income/principal indicator code.
     */
    public String getTransactionIPIndicatorCode();

    /**
     * @param ipIndicator The income/principal indicator code to set.
     */
    public void setTransactionIPIndicatorCode(String ipIndicator);

    /**
     * @return Returns the income/principal indicator.
     */
    public IncomePrincipalIndicator getIncomePrincipalIndicator();

    /**
     * @param incomePrincipalIndicator The income/principal indicator to set.
     */
    // Do we need this?
    public void setIncomePrincipalIndicator(IncomePrincipalIndicator incomePrincipalIndicator);

    /**
     * @return Returns the transaction amount.
     */
    public KualiDecimal getTransactionAmount();

    /**
     * @param amount The transaction amount to set.
     */
    public void setTransactionAmount(KualiDecimal amount);

    /**
     * @return Returns the corpus indicator -- Y or N.
     */
    public boolean getCorpusIndicator();

    /**
     * @param corpusIndicator The corpus indicator to set.
     */
    public void setCorpusIndicator(boolean corpusIndicator);

    /**
     * @return Returns the transaction units.
     */
    public KualiDecimal getTransactionUnits();

    /**
     * @param units The transaction units to set.
     */
    public void setTransactionUnits(KualiDecimal units);

    /**
     * @return Returns the line posted indicator -- Y or N.
     */
    public boolean isLinePosted();

    /**
     * @param linePostedIndicator The line posted indicator to set.
     */
    public void setLinePosted(boolean linePostedIndicator);

    /**
     * Gets the tax lot lines.
     * 
     * @return returns the tax lot lines for this transaction line
     */
    public List<EndowmentTransactionTaxLotLine> getTaxLotLines();

    /**
     * Sets the tax lot lines.
     * 
     * @param taxLotLines
     */
    public void setTaxLotLines(List<EndowmentTransactionTaxLotLine> taxLotLines);

    /**
     * Gets the unit adjustment amount.
     * 
     * @return returns unit adjustment amount for this transaction line
     */
    public BigDecimal getUnitAdjustmentAmount();

    /**
     * sets unitAdjustmentAmount
     */
    public void setUnitAdjustmentAmount(BigDecimal unitAdjustmentAmount);
}
