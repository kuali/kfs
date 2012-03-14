/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.ojb.broker.util.GUID;

/**
 * 
 * This class wraps an OJB Guid so that it conforms to the format (and using the algorithm) described in
 * RFC 4122 entitled " A Universally Unique IDentifier (UUID) URN Namespace" 
 */
public class Guid {
 
    private final static char HYPHEN='-';
    private final static String DIGITS="0123456789ABCDEF";
    private String stringValue=null;
    private GUID guid;
    
    public Guid() {
        
        guid = new GUID(); // This OJB class is deprecated; remove this line when we upgrade OJB
        
        
        String guidString=guid.toString();
        // this is roughly the prefered way with the new OJB GUIDFactory:
        // String guidString=org.apache.ojb.broker.util.GUIDFactory.next(); 
        
    
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        sha.update(guidString.getBytes());
        byte[] hash=sha.digest();
        
        StringBuffer result=new StringBuffer();
        for (int i=0; i<hash.length; i++) {
            result.append(toHex(hash[i]));
        }
        
        // hyphenate
        for (int i=20; i>4; i-=4) {
            result.insert(i,HYPHEN);
        }
        
        // truncate
        result.delete(32,40);
        stringValue=result.toString();
    }    
    
    public static String toHex(byte b) {

        int ub=b<0?b+256:b;
        
        StringBuffer result=new StringBuffer(2);
        result.append(DIGITS.charAt(ub/16));
        result.append(DIGITS.charAt(ub%16));
        
        return result.toString();
    }

    @Override
    public String toString() {
        return stringValue;
    }
    
}
