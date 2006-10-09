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

import org.kuali.Constants;
import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.uidraw.KeyLabelPair;
import org.kuali.module.gl.bo.UniversityDate;

/**
 * This class...
 * 
 * 
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
        labels.add(new KeyLabelPair(Constants.MONTH1, Constants.MONTH1));
        labels.add(new KeyLabelPair(Constants.MONTH2, Constants.MONTH2));
        labels.add(new KeyLabelPair(Constants.MONTH3, Constants.MONTH3));
        labels.add(new KeyLabelPair(Constants.MONTH4, Constants.MONTH4));

        labels.add(new KeyLabelPair(Constants.MONTH5, Constants.MONTH5));
        labels.add(new KeyLabelPair(Constants.MONTH6, Constants.MONTH6));
        labels.add(new KeyLabelPair(Constants.MONTH7, Constants.MONTH7));
        labels.add(new KeyLabelPair(Constants.MONTH8, Constants.MONTH8));

        labels.add(new KeyLabelPair(Constants.MONTH9, Constants.MONTH9));
        labels.add(new KeyLabelPair(Constants.MONTH10, Constants.MONTH10));
        labels.add(new KeyLabelPair(Constants.MONTH11, Constants.MONTH11));
        labels.add(new KeyLabelPair(Constants.MONTH12, Constants.MONTH12));

        labels.add(new KeyLabelPair(Constants.MONTH13, Constants.MONTH13));
        labels.add(new KeyLabelPair(Constants.ANNUAL_BALANCE, Constants.ANNUAL_BALANCE));
        labels.add(new KeyLabelPair(Constants.BEGINNING_BALANCE, Constants.BEGINNING_BALANCE));
        labels.add(new KeyLabelPair(Constants.CG_BEGINNING_BALANCE, Constants.CG_BEGINNING_BALANCE));

        return labels;
    }

}
