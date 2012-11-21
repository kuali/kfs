/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

@SuppressWarnings("deprecation")
public class CardTypeValuesFinder extends KeyValuesBase {

    /**
     * Get the card type values based on available imported expenses
     *
     * Always include actual expense as the first option on the type
     *
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {

        TravelDocument document = ((TravelFormBase)KNSGlobalVariables.getKualiForm()).getTravelDocument();
        List<ImportedExpense> importedExpenses = document.getImportedExpenses();
        Map<String,KeyValue> map = new LinkedHashMap<String, KeyValue>();

        String defaultCardType = document.getDefaultAccountingLineCardAgencyType();

        //default to always include actual expense type
        map.put(defaultCardType, new ConcreteKeyValue(defaultCardType, defaultCardType));

        for (ImportedExpense expense : importedExpenses) {
            String cardType = StringUtils.defaultString(expense.getCardType());
            if (!map.containsKey(cardType)){
                map.put(cardType, new ConcreteKeyValue(cardType,cardType));
                //remove the default card type (if its blank) - since there is a new default
                if (map.containsKey(defaultCardType) && StringUtils.isBlank(defaultCardType)){
                    map.remove(defaultCardType);
                }
            }
        }
        return new ArrayList<KeyValue>(map.values());
    }

}
