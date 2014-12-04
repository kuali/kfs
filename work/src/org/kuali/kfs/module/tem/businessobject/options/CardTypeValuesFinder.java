/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
