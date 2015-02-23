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
/* namespace for rulebo maintenanceview */
rulebo_maintenanceview = (function() {
    // debounce impl cribbed from Underscore.js
    // http://underscorejs.org/#debounce
    function underscore_debounce(func, wait, immediate) {
        var timeout;
        return function() {
            var context = this, args = arguments;
            var later = function() {
                timeout = null;
                if (!immediate) func.apply(context, args);
            };
            if (immediate && !timeout) func.apply(context, args);
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    };

    return {
        // binds event handlers that enable or disable the Copy Rule button depending on whether
        // copyRuleName field value is empty
        "bind_copyRuleName": function() {
            var empty_re = /^\\s*$/;
            var copy_button = jq("#copyRuleButton");
            var name_field = jq("#copyRuleName");
            var namespace_field = jq("#ruleNamespace_attribute");

            function toggle_enabled(enabled) {
                if (enabled) {
                    copy_button.removeAttr("disabled");
                } else {
                    copy_button.attr("disabled", "disabled");
                }
            }

            function validate_copy_rule_name(rule_name, rule_namespace) {
                jq.ajax({
                    url: "krmsAgendaEditor",
                    data: {
                        methodToCall: "ajaxValidRuleName",
                        name: rule_name,
                        namespace: rule_namespace
                    }
                }).done(toggle_enabled);
            }

            function enable_or_disable_copyRuleButton() {
                var val = jq(this).val();
                if (!val || empty_re.exec(val)) {
                    toggle_enabled(false);
                } else {
                    validate_copy_rule_name(val, namespace_field.text());
                }
            }

            name_field.change(enable_or_disable_copyRuleButton);
            name_field.keyup(underscore_debounce(enable_or_disable_copyRuleButton, 500));
        }
    };
})();