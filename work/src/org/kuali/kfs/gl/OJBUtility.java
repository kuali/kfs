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
package org.kuali.module.gl.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.WrapDynaClass;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.kuali.core.dao.LookupDao;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;

/**
 * This class provides a set of utilities that can handle common tasks related to business objects.
 */
public class OJBUtility {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OJBUtility.class);

    public static final String LOOKUP_DAO = "lookupDao";

    /**
     * This method builds a map of business object with its property names and values
     * 
     * @param businessObject the given business object
     * @return the map of business object with its property names and values
     */
    public static LinkedHashMap buildPropertyMap(Object businessObject) {
        DynaClass dynaClass = WrapDynaClass.createDynaClass(businessObject.getClass());
        DynaProperty[] properties = dynaClass.getDynaProperties();
        LinkedHashMap propertyMap = new LinkedHashMap();

        try {
            for (int numOfProperty = 0; numOfProperty < properties.length; numOfProperty++) {
                String propertyName = properties[numOfProperty].getName();
                if (PropertyUtils.isWriteable(businessObject, propertyName)) {
                    Object propertyValue = PropertyUtils.getProperty(businessObject, propertyName);
                    propertyMap.put(propertyName, propertyValue);
                }
            }
        }
        catch (Exception e) {
            LOG.error("OJBUtility.buildPropertyMap()" + e);
        }
        return propertyMap;
    }

    /**
     * This method builds an OJB query criteria based on the input field map
     * 
     * @param fieldValues the input field map
     * @param businessObject the given business object
     * @return an OJB query criteria
     */
    public static Criteria buildCriteriaFromMap(Map fieldValues, Object businessObject) {
        Criteria criteria = new Criteria();

        try {
            Iterator propsIter = fieldValues.keySet().iterator();
            while (propsIter.hasNext()) {
                String propertyName = (String) propsIter.next();
                Object propertyValueObject = fieldValues.get(propertyName);
                String propertyValue = (propertyValueObject != null) ? propertyValueObject.toString().trim() : "";

                // if searchValue is empty and the key is not a valid property ignore
                boolean isCreated = createCriteria(businessObject, propertyValue, propertyName, criteria);
                if (!isCreated) {
                    continue;
                }
            }
        }
        catch (Exception e) {
            LOG.error("OJBUtility.buildCriteriaFromMap()" + e);
        }
        return criteria;
    }

    /**
     * limit the size of the result set from the given query operation
     * 
     * @param query the given query operation
     */
    public static void limitResultSize(Query query) {
        int startingIndex = 1;
        int endingIndex = getResultLimit().intValue();

        query.setStartAtIndex(startingIndex);
        query.setEndAtIndex(endingIndex);
    }

    /**
     * This method calculates the actual size of given selection results
     * 
     * @param result the given selection results
     * @param recordCount the possible number of the given results
     * @param fieldValues the input field map
     * @param businessObject the given business object
     * @return the actual size of given selection results
     */
    public static Long getResultActualSize(Collection result, Integer recordCount, Map fieldValues, Object businessObject) {
        int resultSize = result.size();
        Integer limit = getResultLimit();
        Long resultActualSize = new Long(resultSize);

        if (recordCount > limit) {
            long actualCount = recordCount.longValue() + resultSize - limit.longValue();
            resultActualSize = new Long(actualCount);
        }
        return resultActualSize;
    }

    /**
     * This method gets the size of a result set from the given search criteria
     * 
     * @param fieldValues the input field map
     * @param businessObject the given business object
     * @return the size of a result set from the given search criteria
     */
    public static Long getResultSizeFromMap(Map fieldValues, Object businessObject) {
        LookupDao lookupDao = SpringContext.getBean(LookupDao.class);
        return lookupDao.findCountByMap(businessObject, fieldValues);
    }

    /**
     * This method gets the limit of the selection results
     * 
     * @return the limit of the selection results
     */
    public static Integer getResultLimit() {
        // get the result limit number from configuration
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        String limitConfig = kualiConfigurationService.getParameterValue(KFSConstants.CORE_NAMESPACE, KFSConstants.Components.LOOKUP, KFSConstants.LOOKUP_RESULTS_LIMIT_URL_KEY);

        Integer limit = Integer.MAX_VALUE;
        if (limitConfig != null) {
            limit = Integer.valueOf(limitConfig);
        }
        return limit;
    }

    /**
     * This method build OJB criteria from the given property value and name
     */
    public static boolean createCriteria(Object businessObject, String propertyValue, String propertyName, Criteria criteria) {
        LookupDao lookupDao = SpringContext.getBean(LookupDao.class);
        return lookupDao.createCriteria(businessObject, propertyValue, propertyName, criteria);
    }
}