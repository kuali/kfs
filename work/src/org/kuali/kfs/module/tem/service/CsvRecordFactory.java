/*
 * Copyright 2012 The Kuali Foundation
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
package org.kuali.kfs.module.tem.service;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.replace;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.util.type.KualiDecimal;


/**
 * Interface defining the structure for a CSV flat file record of stuff
 *
 * @see org.kuali.kfs.module.tem.businessobject.GroupTravelerCsvRecord;
 */
public class CsvRecordFactory<RecordType>  {

    public static Logger LOG = Logger.getLogger(CsvRecordFactory.class);

    final Class<RecordType> recordType;
    private Map<String,String> headerMap;

    public CsvRecordFactory(final Class<RecordType> recordType) {
        this.recordType = recordType;
    }

    public void setHeaderMap(final Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public Map<String, String> getHeaderMap() {
        return this.headerMap;
    }

    public class CsvRecordInvocationHandler implements InvocationHandler {
        protected Map<String, List<Integer>> header;
        protected String[] line;

        public CsvRecordInvocationHandler(final Map<String, List<Integer>> header, final String[] record) {
            setHeader(header);
            setRecord(record);
        }

        protected void setRecord(final String[] record) {
            this.line = record;
        }

        protected void setHeader(final Map<String, List<Integer>> header) {
            this.header = header;
        }

        protected PropertyDescriptor propertyFor(final PropertyDescriptor[] properties, final Method method) {
            for (final PropertyDescriptor property: properties) {
                if (method.getName().equals(property.getReadMethod().getName())) {
                    return property;
                }
            }

            return null;
        }

        protected String headerFor(final PropertyDescriptor property) {
            LOG.debug("Checking for header that matches property " + property.getName());
            if (headerMap.size() < 1) {
                LOG.warn("Your header map is empty. Won't ever resolve any properties");
            }
            for (final Map.Entry<String,String> entry : headerMap.entrySet()) {
                LOG.debug("Checking " + entry.getValue());
                if (property.getName().equalsIgnoreCase(entry.getValue())) {
                    if (header.containsKey(entry.getKey())) {
                        return entry.getKey();
                    }
                }
            }
            return null;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Object retval = null;

            final PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(recordType);
            final PropertyDescriptor property = propertyFor(properties, method);
            final String headerField = headerFor(property);

            final List<Integer> columns = header.get(headerField);
            StringBuffer value = new StringBuffer();

            if (columns != null) {
                for (final Integer column : columns) {
                    value.append(line[column]).append(" ");
                }
            }

            if (KualiDecimal.class.equals(method.getReturnType())) {
                if (value == null || isBlank(value.toString())) {
                    return KualiDecimal.ZERO;
                }
                retval = new KualiDecimal(convertToBigDecimal(value.toString().trim()));
            }
            else if (BigDecimal.class.equals(method.getReturnType())) {
                if (value == null || isBlank(value.toString())) {
                    return BigDecimal.ZERO;
                }
                retval = convertToBigDecimal(value.toString().trim());
            }
            else if (Boolean.class.equals(method.getReturnType())
                     || boolean.class.equals(method.getReturnType())) {
                retval = new Boolean(value.toString().trim());
                LOG.debug(headerField + " is " + retval);
            }
            else if (java.sql.Date.class.equals(method.getReturnType())) {
                if (!isBlank(value.toString())) {
                    try {
                        retval = new java.sql.Date(new SimpleDateFormat("MM/dd/yyyy").parse(value.toString().trim()).getTime());
                    }
                    catch (Exception e) {
                        // Ignore
                    }
                }
            }
            else {
                retval = value.toString().trim();
            }
            return retval;
        }

        public BigDecimal convertToBigDecimal(final String toConvert) {
            if (isBlank(toConvert)) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(replace(replace(toConvert, "$", ""), ",", ""));
        }
    }

    public RecordType newInstance(final Map<String, List<Integer>> header, final String[] record) throws Exception {
        return (RecordType) Proxy.newProxyInstance(recordType.getClassLoader(),
                                                   new Class[] { recordType },
                                                   new CsvRecordInvocationHandler(header, record));
    }
}
