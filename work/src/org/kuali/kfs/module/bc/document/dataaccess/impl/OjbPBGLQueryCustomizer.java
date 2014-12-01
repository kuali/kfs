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
