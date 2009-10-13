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
	function selectSources(all){
		var elms = document.getElementsByTagName("input");
		for(var i=0; i< elms.length; i++){
			if(elms[i].id !=null  && elms[i].id =='systemCheckbox' && !elms[i].disabled){
				elms[i].checked = all.checked;
			}
		}
	}
	function toggle(src,trg){
		var src = document.getElementById(src);
		var trg = document.getElementById(trg);
		if(src.checked){
			trg.disabled = true;
		}else{
			trg.disabled = false;
		}
	}
