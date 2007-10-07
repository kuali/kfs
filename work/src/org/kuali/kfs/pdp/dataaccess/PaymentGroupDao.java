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

import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentProcess;

public interface PaymentGroupDao {
    /**
     * Get all the payments from a specific process of a certain type
     * 
     * @param pid
     * @param disbursementType
     * @return
     */
    public Iterator getByProcessIdDisbursementType(Integer pid,String disbursementType);

    /**
     * Get all the payment groups for a specific disbursement type & status code
     * 
     * @param disbursementType
     * @param paymentStatusCode
     * @return
     */
    public Iterator getByDisbursementTypeStatusCode(String disbursementType,String paymentStatusCode);

    public Iterator getByProcessId(Integer pid);
    public Iterator getByProcess(PaymentProcess p);
    public void save(PaymentGroup pg);
    public PaymentGroup get(Integer id);
    public List getByBatchId(Integer batchId);
    public List getByDisbursementNumber(Integer disbursementNbr);  
}
