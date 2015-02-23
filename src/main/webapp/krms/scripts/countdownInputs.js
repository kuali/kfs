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
var ENABLED = true;

function countdownInit() {
    jq('.countdown').each(function() {
        if (jq(this).val() != null && jq(this).val() != undefined) {
            var length = jq(this).val().length;
            var id = jq(this).prop("id");
            var maxLength = jq(this).prop("maxLength");
            if (id == null || maxLength == -1) {
//                alert('countdown css class must have id (' + id + ') and maxLength (' + maxLength + ') properties set');
                return;
            }
            if (document.getElementById(id+'_constraint_span') != null) {
                document.getElementById(id+'_constraint_span').innerHTML = "size " + maxLength + " (" + (maxLength - length) + " characters remaining)";
            }
        }
        jq(this).keyup(function() {
            var maxLength = jq(this).prop("maxLength");
            var length = jq(this).val().length;
            if (document.getElementById(id+'_constraint_span') != null) {
                document.getElementById(id+'_constraint_span').innerHTML = "size " + maxLength + " (" + (maxLength - length) + " characters remaining)";
            }
        });
    });
}

jq(document).ready(function() {
    if (ENABLED) {
        countdownInit();
    }
});
