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
package org.kuali.module.financial.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.module.financial.service.CashReceiptService;

public class CashReceiptServiceImpl implements CashReceiptService {
    private static final String TEST_CASH_RECEIPT_CAMPUS_CD = "HI";
    private static final String TEST_CASH_RECEIPT_VERIFICATION_UNIT = "HAWAII_CR_VERIFICATION_UNIT";


    /**
     * @see org.kuali.module.financial.service.CashReceiptService#getCashReceiptVerificationUnitWorkgroupNameByCampusCode(java.lang.String)
     */
    public String getCashReceiptVerificationUnitForCampusCode(String campusCode) {
        String vunit = null;

        if (StringUtils.isBlank(campusCode)) {
            throw new IllegalArgumentException("invalid (blank) campusCode");
        }

        // HACK: pretend that a lookup is actually happening
        if (campusCode.equals(TEST_CASH_RECEIPT_CAMPUS_CD)) {
            vunit = TEST_CASH_RECEIPT_VERIFICATION_UNIT;
        }
        else {
            vunit = Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_VERIFICATION_UNIT;
        }

        return vunit;
    }


    /**
     * @see org.kuali.module.financial.service.CashReceiptService#getCampusCodeForCashReceiptVerificationUnit(java.lang.String)
     */
    public String getCampusCodeForCashReceiptVerificationUnit(String unitName) {
        String campusCode = null;

        if (StringUtils.isBlank(unitName)) {
            throw new IllegalArgumentException("invalid (blank) unitName");
        }

        // HACK: pretend that a lookup is actually happening
        if (unitName.equals(TEST_CASH_RECEIPT_VERIFICATION_UNIT)) {
            campusCode = TEST_CASH_RECEIPT_CAMPUS_CD;
        }
        else {
            campusCode = Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_CAMPUS_LOCATION_CODE;
        }

        return campusCode;
    }


    /**
     * @see org.kuali.module.financial.service.CashReceiptService#getCashReceiptVerificationUnit(org.kuali.core.bo.user.KualiUser)
     */
    public String getCashReceiptVerificationUnit(KualiUser user) {
        String unitName = null;

        if (user == null) {
            throw new IllegalArgumentException("invalid (null) user");
        }

        // HACK: pretend that a lookup is actually happening
        unitName = Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_VERIFICATION_UNIT;

        return unitName;
    }
}
