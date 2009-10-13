/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.enums.Enum;
import org.kuali.kfs.module.purap.PurapPropertyConstants;

public final class ThresholdField extends Enum {

    public static final ThresholdField CHART_OF_ACCOUNTS_CODE = new ThresholdField(PurapPropertyConstants.CHART_OF_ACCOUNTS_CODE);
    public static final ThresholdField ORGANIZATION_CODE = new ThresholdField(PurapPropertyConstants.ORGANIZATION_CODE);
    public static final ThresholdField ACCOUNT_TYPE_CODE = new ThresholdField(PurapPropertyConstants.ACCOUNT_TYPE_CODE);
    public static final ThresholdField SUBFUND_GROUP_CODE = new ThresholdField(PurapPropertyConstants.SUB_FUND_GROUP_CODE);
    public static final ThresholdField FINANCIAL_OBJECT_CODE = new ThresholdField(PurapPropertyConstants.FINANCIAL_OBJECT_CODE);
    public static final ThresholdField COMMODITY_CODE = new ThresholdField(PurapPropertyConstants.ITEM_COMMODITY_CODE);
    public static final ThresholdField VENDOR_HEADER_GENERATED_ID = new ThresholdField(PurapPropertyConstants.VENDOR_HEADER_GENERATED_ID);
    public static final ThresholdField VENDOR_DETAIL_ASSIGNED_ID = new ThresholdField(PurapPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID);
    public static final ThresholdField VENDOR_NUMBER = new ThresholdField(PurapPropertyConstants.VENDOR_NUMBER,false);
    public static final ThresholdField ACTIVE = new ThresholdField(PurapPropertyConstants.BO_ACTIVE,true);
    
    /**
     * Indicates that a field is available in DB or not
     */
    private boolean isPersistedField;
    
    private ThresholdField(String name) {
        this(name,true);
    }
    
    private ThresholdField(String name,
                           boolean isPersisitedField) {
        super(name);
        this.isPersistedField = isPersisitedField;
    }
  
    public static ThresholdField getEnum(String thresholdEnum) {
      return (ThresholdField) getEnum(ThresholdField.class, thresholdEnum);
    }
  
    public static Map getEnumMap() {
      return getEnumMap(ThresholdField.class);
    }
  
    public static List getEnumList() {
      return getEnumList(ThresholdField.class);
    }
  
    public static Iterator iterator() {
      return iterator(ThresholdField.class);
    }

    public boolean isPersistedField() {
        return isPersistedField;
    }
}
