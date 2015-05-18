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

import groovy.mock.interceptor.MockFor
import static org.junit.Assert.assertEquals
import org.junit.Test
import org.kuali.rice.kns.web.ui.Field
import org.kuali.rice.kns.web.ui.ResultRow
import org.kuali.rice.kns.web.struts.form.LookupForm
import org.kuali.rice.kns.service.BusinessObjectDictionaryService
import static org.junit.Assert.assertTrue
import org.kuali.rice.krad.bo.BusinessObject
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions

/**
 * Tests the KualiLookupableImpl base class.  This test was initially created during investigation
 * of the Kuali lookup framework.  It is probably too tightly coupled to the implementation, and should not
 * be taken at face value as a spec for the code under test.
 */
class KualiLookupableImplTest {
    private KualiLookupableImpl lookupable = new KualiLookupableImpl()

    @Test
    void testSetBusinessObjectClass() {
        assertLookupableCall("setBusinessObjectClass", { b -> assertEquals(String.class, b) }) {
            it.setBusinessObjectClass(String.class)
        }
        assertEquals(String.class, lookupable.getBusinessObjectClass())
    }

    @Test
    void testSetParameters() {
        def params = [foo: "bar"]
        assertLookupableCall("setParameters", { p -> assertEquals(params, p) }) {
            it.setParameters(params)
        }
    }

    @Test
    void testGetParameters() {
        assertLookupableCall("getParameters", {}) {
            it.getParameters()
        }
    }

    @Test
    void testGetColumns() {
        assertLookupableCall("getColumns", {}) {
            it.getColumns()
        }
    }

    @Test
    void testValidateSearchParameters() {
        def params = [foo: "bar"]
        assertLookupableCall("validateSearchParameters", { p -> assertEquals(params, p) }) {
            it.validateSearchParameters(params)
        }
    }

    @Test
    void testGetSearchResults() {
        def params = [foo: "bar"]
        assertLookupableCall("getSearchResults", { p -> assertEquals(params, p) }) {
            it.getSearchResults(params)
        }
    }

    @Test
    void testGetSearchResultsUnbounded() {
        def params = [foo: "bar"]
        assertLookupableCall("getSearchResultsUnbounded", { p -> assertEquals(params, p) }) {
            it.getSearchResultsUnbounded(params)
        }
    }

    @Test
    void testGetCreateNewUrlAllowed() {
        def ctx = new MockFor(LookupableHelperService)
        ctx.ignore.setBusinessObjectClass() {}
        ctx.demand.allowsMaintenanceNewOrCopyAction() { true }
        def delegate = ctx.proxyDelegateInstance()
        lookupable.setLookupableHelperService(delegate)

        lookupable.setBusinessObjectClass(String.class)
        assertEquals("""<a title="Create a new record" href="maintenance.do?businessObjectClassName=java.lang.String&methodToCall=start"><img src="images/tinybutton-createnew.gif" alt="create new" width="70" height="15"/></a>""", lookupable.getCreateNewUrl())

        ctx.verify(delegate)
    }

    @Test
    void testGetCreateNewUrlDisallowed() {
        assertLookupableCall("allowsMaintenanceNewOrCopyAction", { false }) {
            assertEquals("", it.getCreateNewUrl())
        }
    }

    @Test
    void testGetHtmlMenuBar() {
        assertBusinessObjectDictionaryServiceCall("getLookupMenuBar", { "menu bar" }) {
            assertEquals("menu bar", it.getHtmlMenuBar())
        }
    }

    @Test
    void testGetSupplementalMenuBar() {
        assertLookupableCall("getSupplementalMenuBar", {}) {
            it.getSupplementalMenuBar()
        }
    }

    @Test
    void testGetRows() {
        assertLookupableCall("getRows", {}) {
            it.getRows()
        }
    }

    @Test
    void testGetTitle() {
        assertLookupableCall("getTitle", {}) {
            it.getTitle()
        }
    }

    @Test
    void testGetReturnLocation() {
        assertLookupableCall("getReturnLocation", {}) {
            it.getReturnLocation()
        }
    }

    @Test
    void testGetReturnUrl() {
        def bo = [:] as BusinessObject
        def fieldConv = [:]
        def lookupimpl = "abcd"
        def restrictions = [:] as BusinessObjectRestrictions

        def ctx = new MockFor(LookupableHelperService)
        ctx.demand.getReturnKeys() { [] }
        ctx.demand.getReturnUrl() { a, b, c, d, e -> assertEquals(bo, a); assertEquals(fieldConv, b); assertEquals(lookupimpl, c); assertEquals(restrictions, e); }
        def del = ctx.proxyDelegateInstance()

        lookupable.setLookupableHelperService(del)
        lookupable.getReturnUrl(bo, fieldConv, lookupimpl, restrictions)
        ctx.verify(del)
    }

    @Test
    void testGetReturnKeys() {
        assertLookupableCall("getReturnKeys", {}) {
            it.getReturnKeys()
        }
    }

    @Test
    void testGetExtraButtonSource() {
        assertBusinessObjectDictionaryServiceCall("getExtraButtonSource", {}) {
            it.getExtraButtonSource()
        }
    }

    @Test
    void testGetExtraButtonParams() {
        assertBusinessObjectDictionaryServiceCall("getExtraButtonParams", {}) {
            it.getExtraButtonParams()
        }
    }

    @Test
    void testGetDefaultSortColumns() {
        assertLookupableCall("getDefaultSortColumns", {}) {
            it.getDefaultSortColumns()
        }
    }

    @Test
    void testCheckForAdditionalFields() {
        def params = [foo: "bar"]
        assertLookupableCall("checkForAdditionalFields", { p -> assertEquals(params, p) }) {
            it.checkForAdditionalFields(params)
        }
    }

    @Test
    void testGetBackLocation() {
        assertLookupableCall("getBackLocation", {}) {
            it.getBackLocation()
        }
    }

    @Test
    void testSetBackLocation() {
        assertLookupableCall("setBackLocation", { p -> assertEquals("back location", p) }) {
            it.setBackLocation("back location")
        }
    }

    @Test
    void testGetDocFormKey() {
        assertLookupableCall("getDocFormKey", {}) {
            it.getDocFormKey()
        }
    }

    // this method is public because unit tests depend upon it
    @Test
    void testSetDocFormKey() {
        assertLookupableCall("setDocFormKey", { p -> assertEquals("docformkey", p) }) {
            it.setDocFormKey("docformkey")
        }
    }

    @Test
    void testSetFieldConversions() {
        def params = [foo:"bar"]
        assertLookupableCall("setFieldConversions", { p -> assertEquals(params, p) }) {
            it.setFieldConversions(params)
        }
    }

    @Test
    void testSetReadOnlyFieldsList() {
        def params = [] as List<String>
        assertLookupableCall("setReadOnlyFieldsList", { p -> assertEquals(params, p) }) {
            it.setReadOnlyFieldsList(params)
        }
    }

    @Test
    void testSetAndGetLookupableHelperService() {
        def fake = [] as LookupableHelperService
        lookupable.setLookupableHelperService(fake)
        assertEquals(fake, lookupable.getLookupableHelperService())
    }

    @Test
    void testPerformLookup() {
        def form = new LookupForm()
        def results = [] as List<ResultRow>
        def bounded = false
        assertLookupableCall("performLookup", { f, r, b -> assertEquals(form, f); assertEquals(results, r); assertEquals(bounded, b); }) {
            it.performLookup(form, results, bounded.booleanValue())
        }
    }

    @Test
    void testIsSearchUsingOnlyPrimaryKeyValues() {
        assertLookupableCall("isSearchUsingOnlyPrimaryKeyValues", {}) {
            it.isSearchUsingOnlyPrimaryKeyValues()
        }
    }

    @Test
    void testGetPrimaryKeyFieldLabels() {
        assertLookupableCall("getPrimaryKeyFieldLabels", {}) {
            it.getPrimaryKeyFieldLabels()
        }
    }

    @Test
    void testPerformClear() {
        def form = new LookupForm()
        assertLookupableCall("performClear", { f -> assertEquals(form, f) }) {
            it.performClear(form)
        }
    }

    @Test
    void testShouldDisplayHeaderNonMaintActions() {
        assertLookupableCall("shouldDisplayHeaderNonMaintActions", {}) {
            it.shouldDisplayHeaderNonMaintActions()
        }
    }

    @Test
    void testShouldDisplayLookupCriteria() {
       assertLookupableCall("shouldDisplayLookupCriteria", {}) {
            it.shouldDisplayLookupCriteria()
        }
    }

    @Test
    void testPerformCustomAction() {
        assertLookupableCall("performCustomAction", { b -> assertTrue(b) }) {
            it.performCustomAction(true)
        }
    }

    @Test
    void testGetExtraField() {
        assertLookupableCall("getExtraField", {}) {
            it.getExtraField()
        }
    }

    @Test
    void testApplyFieldAuthorizationsFromNestedLookups() {
        def field = new Field()
        assertLookupableCall("applyFieldAuthorizationsFromNestedLookups", { f -> assertEquals(field, f) }) {
            it.applyFieldAuthorizationsFromNestedLookups(field)
        }
    }

    @Test
    void testSetAndGetExtraOnLoad() {
        lookupable.setExtraOnLoad("foo")
        assertEquals("foo", lookupable.getExtraOnLoad())
    }

    @Test
    void testApplyConditionalLogicForFieldDisplay() {
        assertLookupableCall("applyConditionalLogicForFieldDisplay", {}) {
            it.applyConditionalLogicForFieldDisplay()
        }
    }

    protected assertDelegateCall(clazz, method, method_closure, test_closure = null) {
        def ctx = new MockFor(clazz)
        def args = [ 1, method_closure ]
        ctx.demand.invokeMethod(method, args)
        def del = ctx.proxyDelegateInstance()

        if (test_closure) {
            test_closure.call(lookupable, del)
        }

        ctx.verify(del)
    }

    protected assertLookupableCall(method, method_closure, test_closure = null) {
        assertDelegateCall(LookupableHelperService, method, method_closure) {
            lookupable, del ->
            lookupable.setLookupableHelperService(del)
            test_closure?.call(lookupable)
        }
    }

    protected assertBusinessObjectDictionaryServiceCall(method, method_closure, test_closure = null) {
        assertDelegateCall(BusinessObjectDictionaryService, method, method_closure) {
            lookupable, del ->
            assertLookupableCall("getBusinessObjectDictionaryService", { del }) {
                test_closure?.call(lookupable)
            }
        }
    }
}