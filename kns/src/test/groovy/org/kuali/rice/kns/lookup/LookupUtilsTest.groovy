/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.lookup

import org.junit.Test
import org.kuali.rice.krad.util.KRADConstants
import static org.junit.Assert.assertEquals
import org.kuali.rice.kew.api.KewApiConstants
import static org.junit.Assert.assertFalse

/**
 *
 */
class LookupUtilsTest {
    @Test void testLookupUtilsPreprocessesStandardRangeFields() {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("afield", "avalue");
        fields.put(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + "arangefield", "a");
        fields.put("arangefield", "b");
        Map<String, String> processFields = LookupUtils.preProcessRangeFields(fields);
        assertEquals("avalue", fields.get("afield"));
        assertEquals("a..b", fields.get("arangefield"));
    }

    // KULRICE-5630 base lookup preprocessing does NOT generate expressions for searchable attributes
    // we leave this to a second pass by the DocumentSearchCriteriaTranslator
    @Test void testLookupUtilsDoesNotPreprocessDocumentSearchAttributes() {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("afield", "avalue");
        fields.put(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + "arangefield", "a");
        fields.put(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + "arangefield", "b");
        Map<String, String> processFields = LookupUtils.preProcessRangeFields(fields);
        assertEquals("avalue", fields.get("afield"));

        assertFalse("a..b".equals(fields.get(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + "arangefield")));
    }
}
