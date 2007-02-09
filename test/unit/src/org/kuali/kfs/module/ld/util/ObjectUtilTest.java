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
package org.kuali.module.labor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.module.labor.bo.LaborOriginEntry;

import junit.framework.TestCase;

public class ObjectUtilTest  extends TestCase {
    
    public void testBuildObject() throws Exception{
        SimpleAddress targetAddress = null;
        SimpleAddress sourceAddress = new SimpleAddress("1000 Main Street", "Source City", "Kuali", 10000);
        List<String> propertyList = new ArrayList<String>();

        targetAddress = new SimpleAddress(null, null, null, 9999);       
        ObjectUtil.buildObject(targetAddress, sourceAddress);
        assertTrue(targetAddress.equals(sourceAddress));
        
        targetAddress = new SimpleAddress(null, null, null, 9999);
        propertyList.clear();
        propertyList.add("street");
        ObjectUtil.buildObject(targetAddress, sourceAddress, propertyList);
        assertEquals(targetAddress.getStreet(), sourceAddress.getStreet());
        assertFalse(targetAddress.equals(sourceAddress));
        
        targetAddress = new SimpleAddress(null, null, null, 9999);
        String[] sourceAddressArray = {"1000 Main Street", "Source City", "Kuali", "10000"};
        propertyList.clear();
        propertyList.add("street");
        propertyList.add("city");
        propertyList.add("state");
        ObjectUtil.buildObject(targetAddress, sourceAddressArray, propertyList);
        assertFalse(targetAddress.equals(sourceAddress)); 
        
        propertyList.add("zip");
        ObjectUtil.buildObject(targetAddress, sourceAddressArray, propertyList);
        assertTrue(targetAddress.equals(sourceAddress));       
    }
    
    public void testCompareObject() throws Exception{
        SimpleAddress targetAddress = null;
        SimpleAddress sourceAddress = new SimpleAddress("1000 Main Street", "Source City", "Kuali", 10000);
        
        List<String> propertyList = new ArrayList<String>();
        propertyList.add("street");
        propertyList.add("city");
        propertyList.add("state");
        propertyList.add("zip");

        targetAddress = new SimpleAddress(null, null, null, 9999); 
        ObjectUtil.buildObject(targetAddress, sourceAddress);
        assertTrue(ObjectUtil.compareObject(targetAddress, sourceAddress, propertyList)); 
        
        sourceAddress.setZip(2000);
        assertFalse(ObjectUtil.compareObject(targetAddress, sourceAddress, propertyList));
        
        propertyList.remove("zip");
        assertTrue(ObjectUtil.compareObject(targetAddress, sourceAddress, propertyList)); 
    }
    
    public void testBuildPropertyMap() throws Exception{
        SimpleAddress address = new SimpleAddress("1000 Main Street", "Source City", "Kuali", 10000);
        
        List<String> propertyList = new ArrayList<String>();
        propertyList.add("street");
        propertyList.add("city");
        propertyList.add("state");

        Map propertyMap = ObjectUtil.buildPropertyMap(address, propertyList);
        assertEquals(address.getStreet(), propertyMap.get("street"));
        assertEquals(address.getCity(), propertyMap.get("city"));
        assertEquals(address.getState(), propertyMap.get("state"));
        assertFalse(address.getZip().equals(propertyMap.get("zip")));
        
        propertyList.add("zip");
        propertyMap = ObjectUtil.buildPropertyMap(address, propertyList);
        assertEquals(address.getZip(), propertyMap.get("zip"));
    }
    
    public void testConvertLineToBusinessObjectBasedOnDeliminatorAndKeyList() throws Exception{
        String delim = ";";
        String line = "1000 Main Street" + delim + "Source City" +delim + "Kuali" + delim + "10000";
        
        List<String> propertyList = new ArrayList<String>();
        propertyList.add("street");
        propertyList.add("city");
        propertyList.add("state");
        propertyList.add("zip");
        
        SimpleAddress address = new SimpleAddress();
        ObjectUtil.convertLineToBusinessObject(address, line, delim, propertyList);
        
        assertEquals(address.getStreet(), "1000 Main Street");
        assertEquals(address.getCity(), "Source City");
        assertEquals(address.getState(), "Kuali");
        assertEquals(address.getZip(), new Integer(10000));        
    }
    
    public void testConvertLineToBusinessObjectBasedOnDeliminatorAndKeyString() throws Exception{
        String delim = ";";
        String line = "1000 Main Street" + delim + "Source City" +delim + "Kuali" + delim + "10000";
        String fieldNames = "street" + delim + "city" + delim + "state" + delim + "zip";
        
        SimpleAddress address = new SimpleAddress();
        ObjectUtil.convertLineToBusinessObject(address, line, delim, fieldNames);
        
        assertEquals(address.getStreet(), "1000 Main Street");
        assertEquals(address.getCity(), "Source City");
        assertEquals(address.getState(), "Kuali");
        assertEquals(address.getZip(), new Integer(10000));        
    }
    
    public void testConvertLineToBusinessObjectBasedOnFieldLength() throws Exception{
        int[] fieldLength = {6, 4, 5, 5};
        String line = "StreetCityState10000";
        
        List<String> propertyList = new ArrayList<String>();
        propertyList.add("street");
        propertyList.add("city");
        propertyList.add("state");
        propertyList.add("zip");
        
        SimpleAddress address = new SimpleAddress();
        ObjectUtil.convertLineToBusinessObject(address, line, fieldLength, propertyList);
        
        assertEquals(address.getStreet(), "Street");
        assertEquals(address.getCity(), "City");
        assertEquals(address.getState(), "State");
        assertEquals(address.getZip(), new Integer(10000));        
    }
}