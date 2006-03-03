package org.kuali.module.gl.bo;

import junit.framework.TestCase;

public class OriginEntryTest extends TestCase {

    public OriginEntryTest() {
        super();
    }

    public void testGetLine() throws Exception {
        
        String line = null;
        
        line = "2004BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                  ";
        assertEquals(line, new OriginEntry(line).getLine());
        
        line = "2004BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC12345CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                  ";
        assertEquals(line, new OriginEntry(line).getLine());
        
        line = "2004BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC00045CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                  ";
        assertEquals(line, new OriginEntry(line).getLine());
        
    }
    
}
