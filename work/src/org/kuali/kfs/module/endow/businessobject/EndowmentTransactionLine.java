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
