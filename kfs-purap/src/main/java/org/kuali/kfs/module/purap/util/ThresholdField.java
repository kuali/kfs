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
