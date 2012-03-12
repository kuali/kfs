/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.util;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.bc.BCParameterKeyConstants;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class BudgetConstructionUtils {
    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);
    private static Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionUtils.class);
    /**
     * 
     * return a SQL IN list containing the budget construction expenditure object types
     * @return a null string if the system parameter does not exist or is empty
     */
    public static String getExpenditureINList() 
    {
        if (! parameterService.parameterExists(BudgetConstructionDocument.class,BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES))
        {
            LOG.warn(String.format("\n***Budget Construction Application Error***\nSQL will not be valid\nparameter %s does not exist\n",BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES));
            IllegalArgumentException ioex = new IllegalArgumentException("parameter "+BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES+" does not exist");
            throw (ioex);
        }
        
        Collection<String> expenditureObjectTypes = BudgetParameterFinder.getExpenditureObjectTypes();
        if (expenditureObjectTypes.isEmpty())
        {
            LOG.warn(String.format("\n***Budget Construction Application Error***\nSQL will not be valid\nparameter %s is empty\n",BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES));
            IllegalArgumentException bfex = new IllegalArgumentException("parameter "+BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES+" is empty");
            throw (bfex);
        }
        return inString(expenditureObjectTypes);
    }
    
    /**
     * 
     * return a SQL IN list containing the budget construction revenue object types
     * @return a null string if the system parameter does not exist or is empty
     */
    public static String getRevenueINList() 
    {
        if (! parameterService.parameterExists(BudgetConstructionDocument.class,BCParameterKeyConstants.REVENUE_OBJECT_TYPES))
        {
            LOG.warn(String.format("\n***Budget Construction Application Error***\nSQL will not be valid\nparameter %s does not exist\n",BCParameterKeyConstants.REVENUE_OBJECT_TYPES));
            IllegalArgumentException ioex = new IllegalArgumentException("parameter "+BCParameterKeyConstants.REVENUE_OBJECT_TYPES+" does not exist");
            throw (ioex);
        }
        Collection<String> revenueObjectTypes = BudgetParameterFinder.getRevenueObjectTypes();
        if (revenueObjectTypes.isEmpty())
        {
            LOG.warn(String.format("\n***Budget Construction Application Error***\nSQL will not be valid\nparameter %s is empty\n",BCParameterKeyConstants.REVENUE_OBJECT_TYPES));
            IllegalArgumentException bfex = new IllegalArgumentException("parameter "+BCParameterKeyConstants.REVENUE_OBJECT_TYPES+" is empty");
            throw (bfex);
        }
        return inString(revenueObjectTypes);
    }
    
    /**
     * 
     * build a string of placeholders for a parameterized java.sql IN clause
     * @param parameterCount the number of parameters in the IN clause
     * @return the String (?,?,?) with the correct nubmer of parameters
     */       
    protected static String inString(Integer parameterCount)
    {
        // there should be at least one parameter in the IN string
        // but allow people to screw up and have an IN condition which is never satisfied
        if (parameterCount == 0)
        {
            return new String("('')");
        }
        StringBuilder sb = new StringBuilder(20);
        sb = sb.append("(?");
        for (int i = 1; i < parameterCount; i++)
        {
            sb.append(",?");
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 
     * build a SQL IN clause from the array of parameters passed in
     * @param inListValues: components of the IN list
     * @return an empty string if the IN list will be empty
     */
    protected static String inString (Collection<String> inListValues)
    {
        if ( inListValues.isEmpty() ) {
            return "";
        }
        // the delimiter for strings in the DB is assumed to be a single quote.
        // this is the ANSI-92 standard.
        // if the ArrayList input is empty, IN ('') is returned.
        StringBuilder inBuilder = new StringBuilder(150);
        inBuilder.append("('");
        boolean isFirst = true;
        for ( String val : inListValues ) {
            if ( isFirst ) {
                isFirst = false;
            } else {
                inBuilder.append("','");    
            }
            inBuilder.append( val );
        }
        inBuilder.append("')");
        
        return inBuilder.toString();
    }

}
