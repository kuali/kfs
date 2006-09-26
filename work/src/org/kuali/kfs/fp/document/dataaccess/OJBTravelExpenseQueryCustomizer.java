/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.dao.ojb;

import java.util.HashMap;
import java.util.Map;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.QueryCustomizer;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.Constants;
import org.kuali.PropertyConstants;

/**
 * Query customizer for to seperate out the pre-paid and non prepaid collections from the dv expense table.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class OJBTravelExpenseQueryCustomizer implements QueryCustomizer {
    private static final String prepaidAttributeName = "PREPAID";
    private static final String prepaidIndicatorField = PropertyConstants.DISB_VCHR_EXPENSE + "." + PropertyConstants.PREPAID_EXPENSE;
    private Map attributes = new HashMap();

    /**
     * @see org.apache.ojb.broker.accesslayer.QueryCustomizer#customizeQuery(java.lang.Object,
     *      org.apache.ojb.broker.PersistenceBroker, org.apache.ojb.broker.metadata.CollectionDescriptor,
     *      org.apache.ojb.broker.query.QueryByCriteria)
     */
    public Query customizeQuery(Object arg0, PersistenceBroker arg1, CollectionDescriptor arg2, QueryByCriteria arg3) {
        if ("TRUE".equals(getAttribute(prepaidAttributeName))) {
            arg3.getCriteria().addEqualTo(prepaidIndicatorField, Constants.ACTIVE_INDICATOR);
        }
        else {
            arg3.getCriteria().addEqualTo(prepaidIndicatorField, Constants.NON_ACTIVE_INDICATOR);
        }
        return arg3;
    }

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

}
