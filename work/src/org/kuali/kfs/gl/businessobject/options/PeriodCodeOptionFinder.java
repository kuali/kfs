/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.gl.web.optionfinder;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
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
        UniversityDate ud = SpringServiceLocator.getUniversityDateService().getCurrentUniversityDate();
        return ud.getUniversityFiscalAccountingPeriod();
    }

    /**
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();
        labels.add(new KeyLabelPair(KFSConstants.MONTH1, KFSConstants.MONTH1));
        labels.add(new KeyLabelPair(KFSConstants.MONTH2, KFSConstants.MONTH2));
        labels.add(new KeyLabelPair(KFSConstants.MONTH3, KFSConstants.MONTH3));
        labels.add(new KeyLabelPair(KFSConstants.MONTH4, KFSConstants.MONTH4));

        labels.add(new KeyLabelPair(KFSConstants.MONTH5, KFSConstants.MONTH5));
        labels.add(new KeyLabelPair(KFSConstants.MONTH6, KFSConstants.MONTH6));
        labels.add(new KeyLabelPair(KFSConstants.MONTH7, KFSConstants.MONTH7));
        labels.add(new KeyLabelPair(KFSConstants.MONTH8, KFSConstants.MONTH8));

        labels.add(new KeyLabelPair(KFSConstants.MONTH9, KFSConstants.MONTH9));
        labels.add(new KeyLabelPair(KFSConstants.MONTH10, KFSConstants.MONTH10));
        labels.add(new KeyLabelPair(KFSConstants.MONTH11, KFSConstants.MONTH11));
        labels.add(new KeyLabelPair(KFSConstants.MONTH12, KFSConstants.MONTH12));

        labels.add(new KeyLabelPair(KFSConstants.MONTH13, KFSConstants.MONTH13));
        labels.add(new KeyLabelPair(KFSConstants.ANNUAL_BALANCE, KFSConstants.ANNUAL_BALANCE));
        labels.add(new KeyLabelPair(KFSConstants.BEGINNING_BALANCE, KFSConstants.BEGINNING_BALANCE));
        labels.add(new KeyLabelPair(KFSConstants.CG_BEGINNING_BALANCE, KFSConstants.CG_BEGINNING_BALANCE));

        return labels;
    }

}
