/*
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
/**
 * Bind to click events on toggle buttons
 */
jQuery(function() {
    /**
     * Toggle the named input value between YES and NO, and then call customLookupChanged
     * to submit form to refresh.
     */
    function toggleSearchType(input_name) {
        var input = jQuery('input[name=' + input_name + ']');
        input.val(input.val() == "YES" ? "NO" : "YES");
        customLookupChanged();
    }
    jQuery("#toggleAdvancedSearch" ).click(function() { toggleSearchType("isAdvancedSearch"); });
    jQuery("#toggleSuperUserSearch").click(function() { toggleSearchType("superUserSearch"); });
    jQuery("#resetSavedSearch"     ).click(function() { toggleSearchType("resetSavedSearch"); });
});

/**
 * Called on an action that requires the lookup to be refreshed
 * Invokes performCustomAction.
 */
function customLookupChanged() {
    var methodToCallElement=document.createElement("input");
    methodToCallElement.setAttribute("type","hidden");
    methodToCallElement.setAttribute("name","methodToCall");
    methodToCallElement.setAttribute("value","refresh");
    document.forms[0].appendChild(methodToCallElement);

    var refreshCallerElement=document.createElement("input");
    refreshCallerElement.setAttribute("type","hidden");
    refreshCallerElement.setAttribute("name","refreshCaller");
    refreshCallerElement.setAttribute("value","customLookupAction");
    document.forms[0].appendChild(refreshCallerElement);

    document.forms[0].submit();
}
