/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.context;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.kuali.test.ConfigureContext;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.PreCommitSuite;
import org.springframework.aop.framework.AopProxyUtils;
@AnnotationTestSuite(PreCommitSuite.class)
@ConfigureContext
public class TransactionalAnnotationTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionalAnnotationTest.class);

    Map<Class, Boolean> seenClasses = new HashMap();

    public void testTransactionAnnotations() {
        Map<String, Class> nonAnnotatedTransactionalServices = getNonAnnotatedTransactionalServices();
        for (String beanName : new TreeSet<String>(nonAnnotatedTransactionalServices.keySet())) {
            LOG.error(String.format("Service Bean improperly annotated: %s <%s>\n", beanName, nonAnnotatedTransactionalServices.get(beanName).getName()));
        }
        int count = nonAnnotatedTransactionalServices.size();
        StringBuffer failureMessage = new StringBuffer("Transaction support for ").append(count).append(count == 1 ? " Service" : " Services").append(" improperly annotated: ");
        for (String serviceName : nonAnnotatedTransactionalServices.keySet()) {
            failureMessage.append("\t").append(serviceName).append(": ").append(nonAnnotatedTransactionalServices.get(serviceName));
        }
        assertTrue(failureMessage.toString(), nonAnnotatedTransactionalServices.isEmpty());

    }

    public Map getNonAnnotatedTransactionalServices() {
        Map<String, Class> nonAnnotatedTransactionalServices = new HashMap();
        String[] beanNames = SpringContext.getBeanNames();
        for (String beanName : beanNames) {
            Object bean = null;
            try {
                bean = SpringContext.getBean(beanName);
            }
            catch (Exception e) {
                LOG.warn("Caught exception while trying to obtain service: " + beanName);
            }
            if (bean != null) {
                Class beanClass = bean.getClass();
                if (beanClass.getName().startsWith("$Proxy")) {
                    beanClass = AopProxyUtils.getTargetClass(bean);
                }
                if (beanClass.getName().startsWith("org.kuali") && !Modifier.isAbstract(beanClass.getModifiers()) && !beanClass.getName().endsWith("DaoOjb") && !beanClass.getName().endsWith("Factory") && !isClassAnnotated(beanName, beanClass)) {
                    nonAnnotatedTransactionalServices.put(beanName, beanClass);
                }
            }
        }
        return nonAnnotatedTransactionalServices;
    }

    private boolean isClassAnnotated(String beanName, Class beanClass) {
        if (beanClass.getAnnotation(org.springframework.transaction.annotation.Transactional.class) != null) {
            return true;
        }
        return !shouldHaveTransaction(beanClass);
    }

    /*
     * Recursively seek evidence that a Transaction is necessary by examining the fields of the given class and recursively
     * investigating its superclass.
     */
    private boolean shouldHaveTransaction(Class beanClass) {
        Boolean result = seenClasses.get(beanClass);
        if (result != null)
            return result;
        if (seenClasses.containsKey(beanClass)) {
            return false;
        }
        seenClasses.put(beanClass, null); // placeholder to avoid recursive problems
        result = Boolean.FALSE;
        for (Field field : beanClass.getDeclaredFields()) {
            String name = field.getType().getName();
            String fieldTypeName = field.getType().getName();
            if (fieldTypeName.startsWith("org.apache.ojb")) {
                if (!fieldTypeName.equals("org.apache.ojb.broker.metadata.DescriptorRepository")) {
                    result = Boolean.TRUE;
                }
            }
            if (name.startsWith("org.kuali")) {
                if (name.contains("Dao")) {
                    result = Boolean.TRUE;
                }
                if (result == false) {
                    result = shouldHaveTransaction(field.getType());
                }
            }
        }
        if (result == false) {
            if (beanClass.getSuperclass() != null && beanClass.getSuperclass().getName().startsWith("org.kuali")) {
                result = shouldHaveTransaction(beanClass.getSuperclass());
            }
        }
        seenClasses.put(beanClass, result);
        return result;
    }
}
