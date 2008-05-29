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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.kuali.core.lookup.LookupResultsServiceImpl;
import org.kuali.core.service.impl.BusinessObjectServiceImpl;
import org.kuali.core.service.impl.KeyValuesServiceImpl;
import org.kuali.core.service.impl.KualiModuleUserPropertyServiceImpl;
import org.kuali.core.service.impl.LookupServiceImpl;
import org.kuali.core.service.impl.PersistenceServiceImpl;
import org.kuali.core.service.impl.PostDataLoadEncryptionServiceImpl;
import org.kuali.core.service.impl.SequenceAccessorServiceImpl;
import org.kuali.module.chart.service.impl.SubFundGroupServiceImpl;
import org.kuali.test.ConfigureContext;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.PreCommitSuite;
import org.springframework.aop.framework.AopProxyUtils;
@AnnotationTestSuite(PreCommitSuite.class)
@ConfigureContext
/**
 * This test checks that services are properly annotated as either Transactional
 * or @link NonTransactional.  The first test is a superset of the subsequent
 * test.  The first test will always fail if one of the subsequent test fails.
 * Acceptable annotations are either at the class level or on each of the public
 * methods, but not both.
 */
public class TransactionalAnnotationTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionalAnnotationTest.class);

    Map<Class, Boolean> seenClasses = new HashMap();
    List<Class> excludedClasses;
    Map<String, String> doubleAnnotatedTransactionalServices;
    Map<String, String> nonAnnotatedTransactionalServices;
    Map<String, Class> incorrectlyAnnotatedTransactionalServices;
    
    public void setUp() throws Exception {
        super.setUp();
/* TODO services that are in RICE and not annotated are excluded from the test.
   Annotate these classes */
        excludedClasses = new ArrayList<Class>();
        excludedClasses.add( BusinessObjectServiceImpl.class );
        excludedClasses.add( PersistenceServiceImpl.class );
        excludedClasses.add( SubFundGroupServiceImpl.class );
        excludedClasses.add( PostDataLoadEncryptionServiceImpl.class );
        excludedClasses.add( KualiModuleUserPropertyServiceImpl.class );
        excludedClasses.add( KeyValuesServiceImpl.class );
        excludedClasses.add( SequenceAccessorServiceImpl.class );
        excludedClasses.add( LookupResultsServiceImpl.class );
        excludedClasses.add( PostDataLoadEncryptionServiceImpl.class );
        excludedClasses.add( LookupServiceImpl.class );

    }

    public void testTransactionAnnotations() {
        getNonAnnotatedTransactionalServices();
        for (String beanName : new TreeSet<String>(incorrectlyAnnotatedTransactionalServices.keySet())) {
            LOG.error(String.format("Service Bean improperly annotated: %s <%s>\n", beanName, incorrectlyAnnotatedTransactionalServices.get(beanName).getName()));
        }
        int count = incorrectlyAnnotatedTransactionalServices.size();
        StringBuffer failureMessage = new StringBuffer("Transaction support for ").append(count).append(count == 1 ? " Service" : " Services").append(" improperly annotated: ");
        for (String serviceName : incorrectlyAnnotatedTransactionalServices.keySet()) {
            failureMessage.append("\t").append(serviceName).append(": ").append(incorrectlyAnnotatedTransactionalServices.get(serviceName));
        }
        assertTrue(failureMessage.toString(), incorrectlyAnnotatedTransactionalServices.isEmpty());

    }
    
    public void testNoTransactionAnnotations() {
        getNonAnnotatedTransactionalServices();
        for (String beanName : new TreeSet<String>(nonAnnotatedTransactionalServices.keySet())) {
            LOG.error(String.format("Service Bean not annotated: %s <%s>\n", beanName, nonAnnotatedTransactionalServices.get(beanName)));
        }
        int count = nonAnnotatedTransactionalServices.size();
        StringBuffer failureMessage = new StringBuffer("Transaction support for ").append(count).append(count == 1 ? " Service" : " Services").append(" not annotated: ");
        for (String serviceName : nonAnnotatedTransactionalServices.keySet()) {
            failureMessage.append("\t").append(serviceName).append(": ").append(nonAnnotatedTransactionalServices.get(serviceName));
        }
        assertTrue(failureMessage.toString(), nonAnnotatedTransactionalServices.isEmpty());

    }
    
    public void testDoubleTransactionAnnotations() {
        getNonAnnotatedTransactionalServices();
        for (String beanName : new TreeSet<String>(doubleAnnotatedTransactionalServices.keySet())) {
            LOG.error(String.format("Service Bean improperly annotated: %s <%s>\n", beanName, doubleAnnotatedTransactionalServices.get(beanName)));
        }
        int count = doubleAnnotatedTransactionalServices.size();
        StringBuffer failureMessage = new StringBuffer("Transaction support for ").append(count).append(count == 1 ? " Service" : " Services").append(" double annotated: ");
        for (String serviceName : doubleAnnotatedTransactionalServices.keySet()) {
            failureMessage.append("\t").append(serviceName).append(": ").append(doubleAnnotatedTransactionalServices.get(serviceName));
        }
        assertTrue(failureMessage.toString(), doubleAnnotatedTransactionalServices.isEmpty());

    }

    public void getNonAnnotatedTransactionalServices() {
        /* We only want to run getNonAnnotatedTransactionalSerivces once.
         * The tests actually just read the Maps that are generated here.
         */
        if (incorrectlyAnnotatedTransactionalServices != null) return;
        incorrectlyAnnotatedTransactionalServices = new HashMap();
        nonAnnotatedTransactionalServices = new HashMap();
        doubleAnnotatedTransactionalServices = new HashMap();
       
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
                if (beanClass.getName().startsWith("org.kuali") 
                        && !Modifier.isAbstract(beanClass.getModifiers()) 
                        && !beanClass.getName().endsWith("DaoOjb") 
                        && !beanClass.getName().endsWith("Factory") 
                        && !beanClass.getName().contains("Lookupable") 
                        && !isClassAnnotated(beanName, beanClass)) {
                    incorrectlyAnnotatedTransactionalServices.put(beanName, beanClass);
                }
            }
        }
        return;
    }

    private boolean isClassAnnotated(String beanName, Class beanClass) {
        boolean hasClassAnnotation = false;
        if (shouldHaveTransaction(beanClass)&& !excludedClasses.contains(beanClass)){
            if (beanClass.getAnnotation(org.springframework.transaction.annotation.Transactional.class) != null) {
                hasClassAnnotation = true;
            }
            if (beanClass.getAnnotation(org.kuali.kfs.annotation.NonTransactional.class) != null){
                hasClassAnnotation =  true;
            }


            boolean hasMethodAnnotation;

            for( Method beanMethod : beanClass.getDeclaredMethods()){
                if (Modifier.isPublic(beanMethod.getModifiers())){
                    hasMethodAnnotation = false;
                    if (beanMethod.getAnnotation(org.springframework.transaction.annotation.Transactional.class) != null) hasMethodAnnotation = true;
                    if (beanMethod.getAnnotation(org.kuali.kfs.annotation.NonTransactional.class) != null) hasMethodAnnotation = true;
                    if (hasMethodAnnotation == false && hasClassAnnotation == false) {
                        nonAnnotatedTransactionalServices.put(beanName, beanMethod.getName());
                        return false; 
                    }
                    if (hasMethodAnnotation == true && hasClassAnnotation == true){
                        doubleAnnotatedTransactionalServices.put(beanName, beanMethod.getName());
                        return false;
                    }
                }

            }
            return true;
        }
        return true;
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
