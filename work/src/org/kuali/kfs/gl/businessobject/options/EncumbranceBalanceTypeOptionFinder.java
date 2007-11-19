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
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.OptionsService;

/**
 * An implementation of ValueFinder that returns all balance types, but which defaults to external encumbrance
 */
public class GLEncumbranceBalanceTypeOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the default value of this ValueFinder, in this case, external encumbrance
     * 
     * @return a String with the key of the default value
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        OptionsService os = SpringContext.getBean(OptionsService.class);
        Options o = os.getCurrentYearOptions();

        return o.getExtrnlEncumFinBalanceTypCd();
    }

    /**
     * Returns a list of all balance types
     * 
     * @return a List of key/value pairs to populate a drop down box
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();

        OptionsService os = SpringContext.getBean(OptionsService.class);
        Options o = os.getCurrentYearOptions();

        labels.add(new KeyLabelPair(o.getExtrnlEncumFinBalanceTypCd(), o.getExtrnlEncumFinBalanceTypCd() + " - " + o.getExtrnlEncumFinBalanceTyp().getFinancialBalanceTypeName()));
        labels.add(new KeyLabelPair(o.getIntrnlEncumFinBalanceTypCd(), o.getIntrnlEncumFinBalanceTypCd() + " - " + o.getIntrnlEncumFinBalanceTyp().getFinancialBalanceTypeName()));
        labels.add(new KeyLabelPair(o.getPreencumbranceFinBalTypeCd(), o.getPreencumbranceFinBalTypeCd() + " - " + o.getPreencumbranceFinBalType().getFinancialBalanceTypeName()));

        return labels;
    }
}
