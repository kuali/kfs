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
package org.kuali.module.pdp.dao;

import java.util.Iterator;
import java.util.List;

import org.kuali.module.pdp.bo.DisbursementNumberRange;
import org.kuali.module.pdp.bo.PaymentDetail;

public interface PaymentDetailDao {
    public PaymentDetail get(Integer id);
    public void save(PaymentDetail pd);
    public PaymentDetail getDetailForEpic(String custPaymentDocNbr, String fdocTypeCode);
    public List getDisbursementNumberRanges(String campus);
    public void saveDisbursementNumberRange(DisbursementNumberRange range);

    /**
     * This will return an iterator of all the cancelled payment details that haven't
     * already been processed
     * 
     * @param organization
     * @param subUnit
     * @return
     */
    public Iterator getUnprocessedCancelledDetails(String organization, String subUnit);

    /**
     * This will return an iterator of all the paid payment details that haven't
     * already been processed
     * 
     * @param organization
     * @param subUnit
     * @return
     */
    public Iterator getUnprocessedPaidDetails(String organization,String subUnit);
}
