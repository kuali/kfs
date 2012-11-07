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
package org.kuali.kfs.sys.document.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.impl.parameter.ParameterEvaluatorImpl;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.impl.ReusableParameterEvaluator;
import org.kuali.rice.kns.util.MaxAgeSoftReference;

/**
 * An interface that represents a generic validation.
 */
public abstract class GenericValidation extends ParameterizedValidation implements Validation {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericValidation.class);
    private boolean quitOnFail = false;
    
    protected ParameterService parameterService;
    private static ParameterEvaluatorService parameterEvaluatorService;

    @SuppressWarnings("rawtypes")
    protected static final Map<Class,Map<String,MaxAgeSoftReference<Collection<String>>>> parameterValuesCache = new HashMap<Class, Map<String,MaxAgeSoftReference<Collection<String>>>>();
    @SuppressWarnings("rawtypes")
    protected static final Map<Class,Map<String,MaxAgeSoftReference<ReusableParameterEvaluator>>> parameterEvaluatorCache = new HashMap<Class, Map<String,MaxAgeSoftReference<ReusableParameterEvaluator>>>();
    @SuppressWarnings("rawtypes")
    protected static final Map<Class,Map<String,MaxAgeSoftReference<Boolean>>> parameterExistenceCache = new HashMap<Class, Map<String,MaxAgeSoftReference<Boolean>>>();
    private static int parameterCacheMaxAgeSeconds = 300; // 5 minutes

    /**
     * This version of validate actually sets up the parameter list and then calls validate(Object[] parameters)
     * @param event the event that requested this validation
     * @return true if validation succeeded and the process required validation should continue, false otherwise
     */
    @Override
    public boolean stageValidation(AttributedDocumentEvent event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Staging validation for: "+getClass().getName()+" for event "+event.getClass().getName());
        }
        populateParametersFromEvent(event);
        return validate(event);
    }
    
    /**
     * Returns whether the validation process should quit on the failure of this validation
     * @return true if the validation process should quit, false otherwise
     */
    @Override
    public boolean shouldQuitOnFail() {
        return quitOnFail;
    }
    
    /**
     * Sets whether this rule should quit on fail or not
     * @param quitOnFail true if the validation process should end if this rule fails, false otherwise
     */
    public void setQuitOnFail(boolean quitOnFail) {
        this.quitOnFail = quitOnFail;
    }

    @SuppressWarnings({ "rawtypes" })
    protected Collection<String> getParameterValues( Class documentClass, String parameterName ) {
        Map<String,MaxAgeSoftReference<Collection<String>>> perClassMap = parameterValuesCache.get(documentClass);
        if ( perClassMap == null ) {
            perClassMap = new HashMap<String, MaxAgeSoftReference<Collection<String>>>();
            parameterValuesCache.put(documentClass, perClassMap);
}
        MaxAgeSoftReference<Collection<String>> paramRef = perClassMap.get(parameterName);
        // if the key does not exist or the contents of the key have timed out, then...
        if ( paramRef == null || paramRef.get() == null ) {
            paramRef = new MaxAgeSoftReference<Collection<String>>(parameterCacheMaxAgeSeconds, getParameterValuesInternal(documentClass, parameterName));
            perClassMap.put(parameterName, paramRef);
        }
        return paramRef.get();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Collection<String> getParameterValuesInternal( Class componentClass, String parameterName ) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Looking for parameter for class: " + componentClass + " and name " + parameterName );
        }
        if ( componentClass == null || componentClass.equals( FinancialSystemTransactionalDocumentBase.class ) ) {
            LOG.debug( "Arrived at the top of the hierarchy - using 'Document' as the component" );
            if ( doesParameterExist(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName) ) {
                return parameterService.getParameterValuesAsString(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName);
            } else {
                LOG.debug( "But...it doesn't exist - so returning an empty list." );
                return Collections.emptyList();
            }
        }
        boolean parameterExists = doesParameterExist(componentClass, parameterName);
        if ( parameterExists ) {
            LOG.debug( "Parameter exists, returning value." );
            return parameterService.getParameterValuesAsString(componentClass, parameterName);
        } else {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Parameter does not exist, recursing to superclass." );
            }
            return getParameterValuesInternal(componentClass.getSuperclass(), parameterName);
        }
    }

    // JHK: commented out for now - the ReusableParameterEvaluator is not thread-safe, so we can not cache them
    // outside of a ThreadLocal
//    protected ReusableParameterEvaluator getParameterEvaluator( Class documentClass, String parameterName ) {
//        Map<String,MaxAgeSoftReference<ReusableParameterEvaluator>> perClassMap = parameterEvaluatorCache.get(documentClass);
//        if ( perClassMap == null ) {
//            perClassMap = new HashMap<String, MaxAgeSoftReference<ReusableParameterEvaluator>>();
//            parameterEvaluatorCache.put(documentClass, perClassMap);
//        }
//        MaxAgeSoftReference<ReusableParameterEvaluator> paramRef = perClassMap.get(parameterName);
//        // if the key does not exist or the contents of the key have timed out, then...
//        if ( paramRef == null || paramRef.get() == null ) {
//            paramRef = new MaxAgeSoftReference<ReusableParameterEvaluator>(parameterCacheMaxAgeSeconds, getParameterEvaluatorInternal(documentClass, parameterName));
//            perClassMap.put(parameterName, paramRef);
//        }
//        return paramRef.get();
//    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected ReusableParameterEvaluator getParameterEvaluatorInternal( Class componentClass, String parameterName ) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Looking for parameter for class: " + componentClass + " and name " + parameterName );
        }
        if ( componentClass == null || componentClass.equals( FinancialSystemTransactionalDocumentBase.class ) ) {
            LOG.debug( "Arrived at the top of the hierarchy - using 'Document' as the component" );
            if ( doesParameterExist(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName) ) {
                ParameterEvaluator pe = getParameterEvaluatorService().getParameterEvaluator(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName, "" );
                if ( pe == null || !(pe instanceof ParameterEvaluatorImpl) ) {
                    return null;
                }
                return new ReusableParameterEvaluator( (ParameterEvaluatorImpl)pe );
            } else {
                LOG.debug( "But...it doesn't exist - so returning null." );
                return null;
            }
        }
        boolean parameterExists = doesParameterExist(componentClass, parameterName);
        if ( parameterExists ) {
            LOG.debug( "Parameter exists, returning value." );
            ParameterEvaluator pe = getParameterEvaluatorService().getParameterEvaluator(componentClass, parameterName, "");
            if ( pe == null || !(pe instanceof ParameterEvaluatorImpl) ) {
                return null;
            }
            return new ReusableParameterEvaluator( (ParameterEvaluatorImpl)pe );
        } else {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Parameter does not exist, recursing to superclass." );
            }
            return getParameterEvaluatorInternal(componentClass.getSuperclass(), parameterName);
        }
    }

    @SuppressWarnings({ "rawtypes" })
    public boolean performParameterValidation(Class documentClass, AccountingLine accountingLine, String parameterName, String propertyName, String userEnteredPropertyName)
    {
        boolean isAllowed = true;
        String propertyValue = getPropertyValue(accountingLine, propertyName);
        ReusableParameterEvaluator pe = getParameterEvaluatorInternal(documentClass, parameterName);
        if ( pe != null ) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Testing property value: " + propertyValue + " against evaluator: " + pe);
            }
            isAllowed = pe.evaluateAndAddError( propertyValue, SourceAccountingLine.class, propertyName, userEnteredPropertyName );
        }
        return isAllowed;
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected boolean doesParameterExist( Class documentClass, String parameterName ) {
        Map<String,MaxAgeSoftReference<Boolean>> perClassMap = parameterExistenceCache.get(documentClass);
        if ( perClassMap == null ) {
            perClassMap = new HashMap<String, MaxAgeSoftReference<Boolean>>();
            parameterExistenceCache.put(documentClass, perClassMap);
        }
        MaxAgeSoftReference<Boolean> paramRef = perClassMap.get(parameterName);
        // if the key does not exist or the contents of the key have timed out, then...
        if ( paramRef == null || paramRef.get() == null ) {
            boolean parameterExists = false;
            try {
                parameterExists = parameterService.parameterExists(documentClass, parameterName);
            } catch ( IllegalArgumentException ex ) {
                parameterExists = false;
            }
            paramRef = new MaxAgeSoftReference<Boolean>( parameterCacheMaxAgeSeconds, parameterExists );
            perClassMap.put(parameterName, paramRef);
        }
        return paramRef.get();
    }

    protected String getPropertyValue(AccountingLine accountingLine, String propertyName) {
        String propertyValue = null;
        try {
            propertyValue = (String) PropertyUtils.getProperty(accountingLine, propertyName);
        } catch (Exception e) {
            throw new RuntimeException("PropertyUtils.getProperty failed to obtain property value for : " + propertyName + " on " + accountingLine, e);
        }
        return propertyValue;
    }

    public static void clearParameterValuesCache() {
        parameterValuesCache.clear();
        parameterEvaluatorCache.clear();
        parameterExistenceCache.clear();
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    protected ParameterEvaluatorService getParameterEvaluatorService() {
        if ( parameterEvaluatorService == null ) {
            parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        }
        return parameterEvaluatorService;
    }

}
