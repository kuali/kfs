/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.test;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.util.TemObjectUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

@ConfigureContext
public class ParametersTest extends KualiTestBase {

    public static Logger LOG = Logger.getLogger(ParametersTest.class);

    private ParameterService parameterService = null;
    private static final String DTL_POSTFIX = "_DTL_TYPE";
    private static final boolean strictMode = true;

    protected static final String TRAVEL_AUTHORIZATION_COMPONENT = "TravelAuthorization";
    protected static final String TRAVEL_REIMBURSEMENT_COMPONENT = "TravelReimbursement";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        parameterService = getParameterService();
    }

    /**
     * Basic test for all the parameters that's pre-populated using parameters.sql
     */
    @Test
    public void testLookup1() throws Exception {
        //setup parameters for testing
        Map<Class, Parameter> criteria = new HashMap<Class, Parameter>();
        criteria.put(TemConstants.TravelParameters.class, setupParameter(PARAM_NAMESPACE, TemConstants.TravelParameters.DOCUMENT_DTL_TYPE));
        criteria.put(TemConstants.TravelAuthorizationParameters.class, setupParameter(PARAM_NAMESPACE, TRAVEL_AUTHORIZATION_COMPONENT));
        criteria.put(TemConstants.TravelReimbursementParameters.class, setupParameter(PARAM_NAMESPACE, TRAVEL_REIMBURSEMENT_COMPONENT));

        List<String> badParameters = testCriteria(criteria);

        if (strictMode) {
            assertTrue("Bad parameter(s): " + badParameters.toString(), badParameters.isEmpty());
        }
        else {
            if (!badParameters.isEmpty()) {
                LOG.warn("Bad parameter(s): " + badParameters.toString());
            }
        }
    }

    private List<String> testCriteria(Map<Class, Parameter> criteria) {
        List<String> badParameters = new ArrayList<String>();

        for (Class key : criteria.keySet()) {
            List<String> parameterList = getParametersByConstantClass(key);
            Parameter p = criteria.get(key);
            badParameters.addAll(checkParameters(p.getNamespaceCode(), p.getComponentCode(), parameterList));
        }

        return badParameters;
    }

    /**
     * Convenience method to populate a new parameter with namespace code and detailtype code
     */
    private Parameter setupParameter(String parameterNameSpace, String componentCode) {
        Parameter.Builder builder = Parameter.Builder.create("KFS", parameterNameSpace, componentCode, "", null);
        return builder.build();
    }

    /**
     * Perform parameter lookup
     */
    private List<String> checkParameters(String paramNamespace, String componentCode, List<String> parameterList) {
        List<String> badParameters = new ArrayList<String>();

        if (parameterList != null) {
            for (String parameterName : parameterList) {
                Parameter p = parameterService.getParameter(paramNamespace, componentCode, parameterName);
                if (p == null) {
                    badParameters.add(parameterName);
                }
            }
        }

        return badParameters;
    }

    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    /**
     * Helper method to extract parameters value from a Constant class using java reflection.
     */
    @SuppressWarnings("rawtypes")
    protected List<String> getParametersByConstantClass(Class c) {
        return getParametersByConstantClass(c, null);
    }

    /**
     * Helper method to extract parameters value from a Constant class using java reflection.
     * excludedFields is optional.
     */
    @SuppressWarnings("rawtypes")
    protected List<String> getParametersByConstantClass(Class c, List<String> excludedFields) {
        List<String> parameters = new ArrayList<String>();
        List<Field> fields = TemObjectUtils.getStaticFields(c);
        for (Field f : fields) {
            if (!f.getName().endsWith(DTL_POSTFIX) && (excludedFields == null || (excludedFields != null && !excludedFields.contains(f.getName())))) {
                try {
                    parameters.add((String) f.get(new String()));
                }
                catch (IllegalArgumentException ex) {
                    LOG.error("IllegalArgumentException.", ex);
                }
                catch (IllegalAccessException ex) {
                    LOG.error("IllegalAccessException.", ex);
                }
            }
        }

        return parameters;
    }
}
