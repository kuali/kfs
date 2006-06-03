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
package org.kuali.module.gl.util;

import org.kuali.core.util.KualiDecimal;

/**
 * @author Anthony Potts
 *
 */
public class LedgerEntry {
    public String balanceType;
    public String originCode;
    public Integer fiscalYear;
    public String period;
    public int recordCount = 0;
    public KualiDecimal debitAmount = KualiDecimal.ZERO;
    public int debitCount = 0;
    public KualiDecimal creditAmount = KualiDecimal.ZERO;
    public int creditCount = 0;
    public KualiDecimal noDCAmount = KualiDecimal.ZERO;
    public int noDCCount = 0;

    public LedgerEntry() {
    }

    public LedgerEntry(String balanceType,String originCode,Integer fiscalYear,String period) {
        this.balanceType = balanceType;
        this.originCode = originCode;
        this.fiscalYear = fiscalYear;
        this.period = period;
    }
}
