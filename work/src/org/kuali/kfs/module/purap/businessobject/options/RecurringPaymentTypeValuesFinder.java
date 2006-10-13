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
package org.kuali.module.purap.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.uidraw.KeyLabelPair;
import org.kuali.module.purap.bo.RecurringPaymentType;

/**
 * This class returns list containg A = Active or I = Inactive
 * 
 */
public class RecurringPaymentTypeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        KeyValuesService boService = SpringServiceLocator.getKeyValuesService();
        Collection codes = boService.findAll(RecurringPaymentType.class);
        List labels = new ArrayList();
        labels.add(new KeyLabelPair("", ""));
        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            RecurringPaymentType rpt = (RecurringPaymentType) iter.next();
            labels.add(new KeyLabelPair(rpt.getRecurringPaymentTypeCode(), rpt.getRecurringPaymentTypeDescription()));
        }

        return labels;
    }

}