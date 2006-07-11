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
package org.kuali.module.gl.web.optionfinder;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.uidraw.KeyLabelPair;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.web.Constant;

/**
 * This class...
 * 
 * @author Bin Gao from Michigan State University
 */
public class GLPeriodCodeOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        UniversityDate ud = SpringServiceLocator.getDateTimeService().getCurrentUniversityDate();
        return ud.getUniversityFiscalAccountingPeriod();
    }

    /**
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();
        labels.add(new KeyLabelPair(Constant.MONTH1, Constant.MONTH1));
        labels.add(new KeyLabelPair(Constant.MONTH2, Constant.MONTH2));
        labels.add(new KeyLabelPair(Constant.MONTH3, Constant.MONTH3));
        labels.add(new KeyLabelPair(Constant.MONTH4, Constant.MONTH4));

        labels.add(new KeyLabelPair(Constant.MONTH5, Constant.MONTH5));
        labels.add(new KeyLabelPair(Constant.MONTH6, Constant.MONTH6));
        labels.add(new KeyLabelPair(Constant.MONTH7, Constant.MONTH7));
        labels.add(new KeyLabelPair(Constant.MONTH8, Constant.MONTH8));

        labels.add(new KeyLabelPair(Constant.MONTH9, Constant.MONTH9));
        labels.add(new KeyLabelPair(Constant.MONTH10, Constant.MONTH10));
        labels.add(new KeyLabelPair(Constant.MONTH11, Constant.MONTH11));
        labels.add(new KeyLabelPair(Constant.MONTH12, Constant.MONTH12));

        labels.add(new KeyLabelPair(Constant.MONTH13, Constant.MONTH13));
        labels.add(new KeyLabelPair(Constant.ANNUAL_BALNCE, Constant.ANNUAL_BALNCE));
        labels.add(new KeyLabelPair(Constant.BEGINNING_BALNCE, Constant.BEGINNING_BALNCE));
        labels.add(new KeyLabelPair(Constant.CG_BEGINNING_BALNCE, Constant.CG_BEGINNING_BALNCE));

        return labels;
    }

}
