/*
 * Copyright 2008-2009 The Kuali Foundation
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
function selectAllCheckboxes(formObj) {
	var checkbox=null;
	var masterCheckbox = formObj.selectAllCheckbox;	
	var nElements= formObj.rowCheckbox.length;
	if (nElements > 0) {
		for (var x= 0; x < nElements; x++)  {
			checkbox = formObj.rowCheckbox[x];
			checkbox.checked = masterCheckbox.checked;
		}
	} else {
		checkbox = formObj.rowCheckbox;
		checkbox.checked = masterCheckbox.checked;
	}			
}
