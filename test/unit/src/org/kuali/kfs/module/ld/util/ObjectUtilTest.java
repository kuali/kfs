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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.testobject.SimpleAddress;

public class ObjectUtilTest extends TestCase {

    public void testBuildObject() throws Exception {
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
        assertEquals(sourceAddress.getStreet(), targetAddress.getStreet());
        assertFalse(targetAddress.equals(sourceAddress));

        targetAddress = new SimpleAddress(null, null, null, 9999);
        String[] sourceAddressArray = { "1000 Main Street", "Source City", "Kuali", "10000" };
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

    public void testCompareObject() throws Exception {
        SimpleAddress targetAddress = null;
        SimpleAddress sourceAddress = new SimpleAddress("1000 Main Street", "Source City", "Kuali", 10000);

        List<String> propertyList = new ArrayList<String>();
        propertyList.add("street");
        propertyList.add("city");
        propertyList.add("state");
        propertyList.add("zip");

        targetAddress = new SimpleAddress(null, null, null, 9999);
        assertFalse(ObjectUtil.equals(targetAddress, sourceAddress, propertyList));

        targetAddress = new SimpleAddress("1000 Main Street", "Source City", "Kuali", 10000);
        assertTrue(ObjectUtil.equals(targetAddress, sourceAddress, propertyList));

        targetAddress = sourceAddress;
        assertTrue(ObjectUtil.equals(targetAddress, sourceAddress, propertyList));

        targetAddress = new SimpleAddress(null, null, null, 9999);
        ObjectUtil.buildObject(targetAddress, sourceAddress);
        assertTrue(ObjectUtil.equals(targetAddress, sourceAddress, propertyList));

        sourceAddress.setZip(2000);
        assertFalse(ObjectUtil.equals(targetAddress, sourceAddress, propertyList));

        propertyList.remove("zip");
        assertTrue(ObjectUtil.equals(targetAddress, sourceAddress, propertyList));
    }

    public void testBuildPropertyMap() throws Exception {
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

    public void testBuildPropertyMapWithBlankValues() throws Exception {
        SimpleAddress address = new SimpleAddress("", "", null, 1000);

        List<String> propertyList = new ArrayList<String>();
        propertyList.add("street");
        propertyList.add("city");
        propertyList.add("state");
        propertyList.add("zip");

        Map propertyMap = ObjectUtil.buildPropertyMap(address, propertyList);
        assertEquals(null, propertyMap.get("street"));
        assertEquals(null, propertyMap.get("city"));
        assertEquals(null, propertyMap.get("state"));
        assertEquals(address.getZip(), propertyMap.get("zip"));
        assertEquals(1, propertyMap.size());
    }

    public void testConvertLineToBusinessObjectBasedOnDeliminatorAndKeyList() throws Exception {
        String delim = ";";
        String line = "1000 Main Street" + delim + "Source City" + delim + "Kuali" + delim + "10000";

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

    public void testConvertLineToBusinessObjectBasedOnDeliminatorAndKeyString() throws Exception {
        String delim = ";";
        String line = "1000 Main Street" + delim + "Source City" + delim + "Kuali" + delim + "10000" + delim;
        String fieldNames = "street" + delim + "city" + delim + "state" + delim + "zip" + delim;

        SimpleAddress address = new SimpleAddress();
        ObjectUtil.convertLineToBusinessObject(address, line, delim, fieldNames);

        assertEquals("1000 Main Street", address.getStreet());
        assertEquals("Source City", address.getCity());
        assertEquals("Kuali", address.getState());
        assertEquals(new Integer(10000), address.getZip());
    }

    public void testConvertLineToBusinessObjectAtCompressedFormat() throws Exception {
        String delim = ";";
        String line = "" + delim + "" + delim + "" + delim + "" + delim;
        String fieldNames = "street" + delim + "city" + delim + "state" + delim + "zip" + delim;

        SimpleAddress address = new SimpleAddress("", "", "", null);
        ObjectUtil.convertLineToBusinessObject(address, line, delim, fieldNames);

        assertEquals(null, address.getStreet());
        assertEquals(null, address.getCity());
        assertEquals(null, address.getState());
        assertEquals(null, address.getZip());
    }

    public void testConvertLineToBusinessObjectBasedOnFieldLength() throws Exception {
        int[] fieldLength = { 6, 4, 5, 5 };
        String line = "StreetCityState10000";

        List<String> propertyList = new ArrayList<String>();
        propertyList.add("street");
        propertyList.add("city");
        propertyList.add("state");
        propertyList.add("zip");

        SimpleAddress address = new SimpleAddress();
        ObjectUtil.convertLineToBusinessObject(address, line, fieldLength, propertyList);

        assertEquals("Street", address.getStreet());
        assertEquals("City", address.getCity());
        assertEquals("State", address.getState());
        assertEquals(new Integer(10000), address.getZip());
    }

    public void testConvertLineToBusinessObjectBasedOnFieldLength_WithWhiteSpace() throws Exception {
        int[] fieldLength = { 8, 6, 7, 7 };
        String line = "Street  City  State  10000  ";

        List<String> propertyList = new ArrayList<String>();
        propertyList.add("street");
        propertyList.add("city");
        propertyList.add("state");
        propertyList.add("zip");

        SimpleAddress address = new SimpleAddress();
        ObjectUtil.convertLineToBusinessObject(address, line, fieldLength, propertyList);

        assertEquals("Street", address.getStreet());
        assertEquals("City", address.getCity());
        assertEquals("State", address.getState());
        assertEquals(new Integer(10000), address.getZip());
    }

    public void testSplit() throws Exception {
        String delim = ";";
        String line = "" + delim + "" + delim + "" + delim + "" + delim;

        List<String> tokens = ObjectUtil.split(line, delim);
        assertEquals(4, tokens.size());

        line = delim + line;
        tokens = ObjectUtil.split(line, delim);
        assertEquals(5, tokens.size());
    }

    public void testValueOfInteger() throws Exception {
        String integerType = "Integer";
        String[] value = { "-100", "0", "100", "", "12.9", "bad value" };
        String[] expected = { "-100", "0", "100", null, null, null };
        for (int i = 0; i < value.length; i++) {
            String tempvalue = expected[i];
            Integer expectedValue = tempvalue != null ? Integer.valueOf(expected[i]) : null;
            assertEquals(expectedValue, ObjectUtil.valueOf(integerType, value[i]));
        }
    }

    public void testValueOfKualiDecimal() throws Exception {
        String type = "KualiDecimal";
        String[] value = { "-100.00", "0", "100", "100.00", "", "bad value" };
        String[] expected = { "-100", "0", "100", "100", null, null };
        for (int i = 0; i < value.length; i++) {
            String tempvalue = expected[i];
            KualiDecimal expectedValue = tempvalue != null ? new KualiDecimal(expected[i]) : null;
            assertEquals(expectedValue, ObjectUtil.valueOf(type, value[i]));
        }
    }
    
    public void testValueOfKualiInteger() throws Exception {
        String type = "KualiInteger";
        String[] value = { "-100", "0", "100", "100.00", "", "bad value" };
        String[] expected = { "-100", "0", "100", null, null, null };
        for (int i = 0; i < value.length; i++) {
            String tempvalue = expected[i];
            KualiInteger expectedValue = tempvalue != null ? new KualiInteger(expected[i]) : null;
            assertEquals(expectedValue, ObjectUtil.valueOf(type, value[i]));
        }
    }

    public void testValueOfDate() throws Exception {
        String type = "Date";
        String[] value = { "2000-01-31", "2000/1/1", "1/1/2000", "", "bad value" };
        String[] expected = { "2000-01-31", null, null, null, null };
        for (int i = 0; i < value.length; i++) {
            String tempvalue = expected[i];
            Date expectedValue = tempvalue != null ? Date.valueOf(expected[i]) : null;
            assertEquals(expectedValue, ObjectUtil.valueOf(type, value[i]));
        }
    }

    public void testValueOfTimestamp() throws Exception {
        String type = "Timestamp";
        String[] value = { "2000-01-31 00:12:00.55", "2000-01-31", "2000/1/1", "1/1/2000", "", "bad value" };
        String[] expected = { "2000-01-31 00:12:00.55", "2000-01-31 00:00:00.0", null, null, null, null };
        for (int i = 0; i < value.length; i++) {
            String tempvalue = expected[i];
            Timestamp expectedValue = tempvalue != null ? Timestamp.valueOf(expected[i]) : null;
            assertEquals(expectedValue, ObjectUtil.valueOf(type, value[i]));
        }
    }

    public void testHasNullValueField() throws Exception {
        SimpleAddress sourceAddress = new SimpleAddress(null, null, null, 9999);
        assertTrue(ObjectUtil.hasNullValueField(sourceAddress));

        sourceAddress = new SimpleAddress("1000 Main Street", "Source City", "Kuali", 10000, new KualiDecimal(200000), new Date(1000000));
        assertTrue(!ObjectUtil.hasNullValueField(sourceAddress));
    }
    
    public void testParseNestedAttribute() throws Exception {
        LedgerBalance balance = new LedgerBalance();       
        String attributes = "laborObject.chartOfAccounts.chartOfAccountsCode";
        
        System.out.println(ObjectUtil.getNestedAttributeTypes(LedgerBalance.class, attributes));    
    }
}