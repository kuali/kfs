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
function requiredsSet() {
    var message = '';
    if (jq('.agenda-namespace') != null &&
        jq('.agenda-namespace').val() == '') {
      message =  message + 'Please select a Namespace.\n';
    }
    if (jq('.agenda-name') != null &&
        jq('.agenda-name').val() == '') {
      message = message + 'Please enter the Agenda Name.\n';
    }
    if (jq('.agenda-context') != null &&
        jq('.agenda-context').val() == '') {
      message = message + 'Please Enter or Lookup (click the magnifying glass icon) the Agenda Context.\n';
    }
    return message;
}

