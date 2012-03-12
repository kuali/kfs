/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.QueryCustomizer;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * This customizer constrains the relationship to PendingBudgetConstructionGeneralLedger so as to fetch expenditure or revenue lines
 * based on a set of object code types
 */
public class OjbPBGLQueryCustomizer implements QueryCustomizer {

    private Map attributes = new HashMap();
    private static final String revenueAttributeName = "REVENUE";
    private static final String objectCodeTypeField = KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE;

    /**
     * @see org.apache.ojb.broker.metadata.AttributeContainer#addAttribute(java.lang.String, java.lang.String)
     */
    public void addAttribute(String arg0, String arg1) {
        attributes.put(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.metadata.AttributeContainer#getAttribute(java.lang.String, java.lang.String)
     */
    public String getAttribute(String arg0, String arg1) {
        return (String) attributes.get(arg0);
    }

    /**
     * @see org.apache.ojb.broker.metadata.AttributeContainer#getAttribute(java.lang.String)
     */
    public String getAttribute(String arg0) {
        return (String) attributes.get(arg0);
    }

    /**
     * @see org.apache.ojb.broker.accesslayer.QueryCustomizer#customizeQuery(java.lang.Object,
     *      org.apache.ojb.broker.PersistenceBroker, org.apache.ojb.broker.metadata.CollectionDescriptor,
     *      org.apache.ojb.broker.query.QueryByCriteria)
     */
    public Query customizeQuery(Object arg0, PersistenceBroker arg1, CollectionDescriptor arg2, QueryByCriteria arg3) {
        Collection<String> paramValues;

        // these parameter service calls will throw an IllegalArgumentException exception if the parameter doesn't exist
        if ("TRUE".equals(getAttribute(revenueAttributeName))) {
            paramValues = BudgetParameterFinder.getRevenueObjectTypes();
        }
        else {
            paramValues = BudgetParameterFinder.getExpenditureObjectTypes();
        }
        
        ArrayList<String> revObjs = new ArrayList<String>();
        revObjs.addAll(paramValues);
        arg3.getCriteria().addIn(objectCodeTypeField, revObjs);
        
        return arg3;
    }

}
