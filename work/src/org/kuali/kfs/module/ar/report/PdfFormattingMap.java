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
package org.kuali.kfs.module.ar.report;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Decorator for a Map which guarantees that values returned from get are never null and usefully formatted to substitute into a template PDF
 */
public class PdfFormattingMap implements Map<String, String> {
    protected Map wrappedMap;
    private static volatile DateTimeService dateTimeService;
    private static volatile ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService;

    public PdfFormattingMap(Map mapToWrap) {
        if (ObjectUtils.isNull(mapToWrap)) {
            throw new IllegalArgumentException("Cannot wrap a null map");
        }
        this.wrappedMap = mapToWrap;
    }

    @Override
    public int size() {
        return wrappedMap.size();
    }

    @Override
    public boolean isEmpty() {
        return wrappedMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return wrappedMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return wrappedMap.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return stringifyValue(wrappedMap.get(key));
    }

    @Override
    public String put(String key, String value) {
        final Object returnedValue = wrappedMap.put(key, value);
        return ObjectUtils.isNull(returnedValue) ? KFSConstants.EMPTY_STRING : returnedValue.toString();
    }

    @Override
    public String remove(Object key) {
        final Object returnedValue = wrappedMap.remove(key);
        return ObjectUtils.isNull(returnedValue) ? KFSConstants.EMPTY_STRING : returnedValue.toString();
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        wrappedMap.putAll(m);
    }

    @Override
    public void clear() {
        wrappedMap.clear();

    }

    @Override
    public Set<String> keySet() {
        Set<String> keySet = new HashSet<>();
        for (Object key : wrappedMap.keySet()) {
            if (!ObjectUtils.isNull(key)) {
                keySet.add(key.toString());
            }
        }
        return keySet;
    }

    @Override
    public Collection<String> values() {
        List<String> values = new ArrayList<>();
        for (Object value : wrappedMap.values()) {
            if (!ObjectUtils.isNull(value)) {
                values.add(stringifyValue(value));
            }
        }
        return values;
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        Set<java.util.Map.Entry<String, String>> entrySet = new HashSet<>();
        for (Object entry : wrappedMap.entrySet()) {
            if (entry instanceof java.util.Map.Entry) {
                final String key = ObjectUtils.isNull(((java.util.Map.Entry)entry).getKey()) ? KFSConstants.EMPTY_STRING : ((java.util.Map.Entry)entry).getKey().toString();
                final String value = stringifyValue(((java.util.Map.Entry)entry).getValue());

                final java.util.Map.Entry<String,String> stringyEntry = new AbstractMap.SimpleImmutableEntry(key, value);
                entrySet.add(stringyEntry);
            }
        }
        return entrySet;
    }

    protected String stringifyValue(Object value) {
        if (ObjectUtils.isNull(value)) {
            return KFSConstants.EMPTY_STRING;
        } else if (value instanceof String) {
            return (String)value;
        } else if (value instanceof java.util.Date) {
            return getDateTimeService().toDateString((java.util.Date)value);
        } else if (value instanceof Boolean || Boolean.TYPE.equals(value.getClass())) {
            return stringifyBooleanForContractsGrantsInvoiceTemplate((Boolean)value);
        } else if (value instanceof KualiDecimal) {
            getContractsGrantsBillingUtilityService().formatForCurrency((KualiDecimal)value);
        }
        return org.apache.commons.lang.ObjectUtils.toString(value);
    }

    /**
     * Converts boolean to a String to display on pdf report
     * @param bool a boolean value
     * @return a String for the pdf based on the given boolean value
     */
    protected String stringifyBooleanForContractsGrantsInvoiceTemplate(boolean bool) { // the name is longer than the code : - ?
       return bool ? "Yes" : "No";
    }

    protected DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

    protected ContractsGrantsBillingUtilityService getContractsGrantsBillingUtilityService() {
        if (contractsGrantsBillingUtilityService == null) {
            contractsGrantsBillingUtilityService = SpringContext.getBean(ContractsGrantsBillingUtilityService.class);
        }
        return contractsGrantsBillingUtilityService;
    }
}
