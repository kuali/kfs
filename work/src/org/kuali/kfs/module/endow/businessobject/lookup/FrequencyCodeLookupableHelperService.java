/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.businessobject.lookup;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

public class FrequencyCodeLookupableHelperService extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {

        // if user did not enter a frequency code
        if (!fieldValues.containsKey(KFSConstants.GENERIC_CODE_PROPERTY_NAME) || StringUtils.isEmpty(fieldValues.get(KFSConstants.GENERIC_CODE_PROPERTY_NAME))) {

            // build search criteria based on information entered and store in this variable
            String frequencyTypeValueBase = KFSConstants.EMPTY_STRING;

            // get the frequency type
            if (fieldValues.containsKey(EndowPropertyConstants.FREQUENCY_TYPE)) {

                String frequencyTypeValue = fieldValues.get(EndowPropertyConstants.FREQUENCY_TYPE);

                if (!StringUtils.isEmpty(frequencyTypeValue)) {
                    frequencyTypeValueBase += frequencyTypeValue;
                }

                // if frequency type is dayly there is nothing else to select, ignore everything else

                // if frequency type is weekly, check for the day of the week selected
                if (frequencyTypeValue.equalsIgnoreCase(EndowConstants.FrequencyTypes.WEEKLY)) {

                    if (fieldValues.containsKey(EndowPropertyConstants.FREQUENCY_WEEK_DAY)) {

                        String weekDay = fieldValues.get(EndowPropertyConstants.FREQUENCY_WEEK_DAY);

                        if (!StringUtils.isEmpty(weekDay)) {
                            frequencyTypeValueBase += weekDay;
                        }

                    }
                }

                // if frequency type is semi-monthly, select a day of the month, it should be among first 15 and then next one will
                // be 15 days later
                if (frequencyTypeValue.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_MONTHLY)) {

                    if (fieldValues.containsKey(EndowPropertyConstants.FREQUENCY_DAY_IN_MONTH)) {

                        String monthDay = fieldValues.get(EndowPropertyConstants.FREQUENCY_DAY_IN_MONTH);

                        if (!StringUtils.isEmpty(monthDay)) {
                            frequencyTypeValueBase += monthDay;
                        }

                    }
                }

                // if frequency type is monthly check day in month, if not selected check month end
                if (frequencyTypeValue.equalsIgnoreCase(EndowConstants.FrequencyTypes.MONTHLY)) {

                    if (fieldValues.containsKey(EndowPropertyConstants.FREQUENCY_MONTHLY_OCCURENCE)) {
                        String monthlyOccurence = fieldValues.get(EndowPropertyConstants.FREQUENCY_MONTHLY_OCCURENCE);

                        if (EndowConstants.FrequencyMonthly.DATE.equalsIgnoreCase(monthlyOccurence)) {
                            if (fieldValues.containsKey(EndowPropertyConstants.FREQUENCY_DAY_IN_MONTH)) {

                                String monthDay = fieldValues.get(EndowPropertyConstants.FREQUENCY_DAY_IN_MONTH);

                                if (!StringUtils.isEmpty(monthDay)) {
                                    frequencyTypeValueBase += monthDay;
                                }

                            }
                        }

                        if (EndowConstants.FrequencyMonthly.MONTH_END.equalsIgnoreCase(monthlyOccurence)) {
                            frequencyTypeValueBase += EndowConstants.FrequencyMonthly.MONTH_END;
                        }

                    }

                }

                // if frequency type is quarterly or semi-annually or annually, check selected month and day in month or month end
                if (frequencyTypeValue.equalsIgnoreCase(EndowConstants.FrequencyTypes.QUARTERLY) || frequencyTypeValue.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_ANNUALLY) || frequencyTypeValue.equalsIgnoreCase(EndowConstants.FrequencyTypes.ANNUALLY)) {

                    if (fieldValues.containsKey(EndowPropertyConstants.FREQUENCY_MONTH)) {
                        String month = fieldValues.get(EndowPropertyConstants.FREQUENCY_MONTH);

                        // if month was not selected than it is replaced by a wildcard
                        if (StringUtils.isEmpty(month)) {
                            frequencyTypeValueBase += KFSConstants.WILDCARD_CHARACTER;
                        }
                        else {
                            frequencyTypeValueBase += month;
                        }

                    }
                    if (fieldValues.containsKey(EndowPropertyConstants.FREQUENCY_MONTHLY_OCCURENCE)) {
                        String monthlyOccurence = fieldValues.get(EndowPropertyConstants.FREQUENCY_MONTHLY_OCCURENCE);

                        if (EndowConstants.FrequencyMonthly.DATE.equalsIgnoreCase(monthlyOccurence)) {
                            if (fieldValues.containsKey(EndowPropertyConstants.FREQUENCY_DAY_IN_MONTH)) {

                                String monthDay = fieldValues.get(EndowPropertyConstants.FREQUENCY_DAY_IN_MONTH);

                                if (!StringUtils.isEmpty(monthDay)) {
                                    frequencyTypeValueBase += monthDay;
                                }

                            }
                        }

                        if (EndowConstants.FrequencyMonthly.MONTH_END.equalsIgnoreCase(monthlyOccurence)) {
                            frequencyTypeValueBase += EndowConstants.FrequencyMonthly.MONTH_END;
                        }
                    }
                }

            }

            // the percentage sign is added for the cases where not all the code part were specified by the user
            frequencyTypeValueBase += KFSConstants.PERCENTAGE_SIGN;
            fieldValues.put(KFSConstants.GENERIC_CODE_PROPERTY_NAME, frequencyTypeValueBase);
        }

        // remove the helped fields from the fieldValues as they are not user for the search
        fieldValues.remove(EndowPropertyConstants.FREQUENCY_TYPE);
        fieldValues.remove(EndowPropertyConstants.FREQUENCY_DAY_IN_MONTH);
        fieldValues.remove(EndowPropertyConstants.FREQUENCY_WEEK_DAY);
        fieldValues.remove(EndowPropertyConstants.FREQUENCY_MONTH);
        fieldValues.remove(EndowPropertyConstants.FREQUENCY_MONTHLY_OCCURENCE);

        List searchResults = super.getSearchResults(fieldValues);

        return searchResults;
    }

}
