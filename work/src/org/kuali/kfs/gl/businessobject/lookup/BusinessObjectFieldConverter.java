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
package org.kuali.module.gl.util;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.kuali.PropertyConstants;

/**
 * This class...
 * @author Bin Gao from Michigan State University
 */
public class BusinessObjectFieldConverter {
    
    /**
     * This method converts the field values from normal GL business objects to GL transaction
     * @param fieldValues the map of field values for normal GL business objects 
     * @return the map of field values for GL transaction
     */
    public static Map convertToTransactionFieldValues(Map fieldValues){
        Map pendingEntryFieldValue = new HashMap();
        
        Iterator propsIter = fieldValues.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            String propertyValue = (String) fieldValues.get(propertyName);
            
            // convert property name from normal BO to GL transaction
            String transactionPropertyName = propertyName;            
            transactionPropertyName = convertToTransactionPropertyName(propertyName);
            
            // create a new entry for current property
            pendingEntryFieldValue.put(transactionPropertyName, propertyValue);
        }       
        return pendingEntryFieldValue;
    }
    
    /**
     * This method converts the property name of a normal business object to GL transaction
     * @param propertyName the property name of a normal business object
     * @return the property name of GL transaction
     */
    public static String convertToTransactionPropertyName(String propertyName){
        String transactionPropertyName = propertyName;
            
        // Map property names
        if(propertyName.equals(PropertyConstants.OBJECT_CODE)){
            transactionPropertyName = PropertyConstants.FINANCIAL_OBJECT_CODE;
        }
        else if(propertyName.equals(PropertyConstants.SUB_OBJECT_CODE)){
            transactionPropertyName = PropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
        }
        else if(propertyName.equals(PropertyConstants.OBJECT_TYPE_CODE)){
            transactionPropertyName = PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE;
        }
        else if(propertyName.equals(PropertyConstants.BALANCE_TYPE_CODE)){
            transactionPropertyName = PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE;
        }
        else if(propertyName.equals(PropertyConstants.DOCUMENT_TYPE_CODE)){
            transactionPropertyName = PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE;
        }
    
        return transactionPropertyName;
    }    
}
