/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.bo;

import org.kuali.core.bo.KualiCodeBase;


/**
 * This class represents a CashDetailTypeCode, which is used to differentiate records of the same general data structure from one
 * another. For example, a CashReceiptDocument has checks and so does a BursarDepositDocument. To be able to re-use the same table
 * and data structure, we need a field that can act as a flag. Another example involves the fact that a CashReceipt contains a coin
 * record, a currency record, and a change out record (aggregated coin and currency together). Since we use the same tables to
 * represent this, we need an attribute to help differentiate between the bunch especially when they all three must co-exist within
 * the same parent CashReceiptDocument instance.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class CashDetailTypeCode extends KualiCodeBase {
    private static final long serialVersionUID = -5228907091959656216L;
    private String description;

    /**
     * Constructs a CashDetailTypeCode business object.
     */
    public CashDetailTypeCode() {
        super();
    }

    /**
     * Gets the description attribute.
     * 
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute value.
     * 
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}