<%--
 Copyright 2007 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<div class="exportlinks">Export options:
<%--
KULRICE-5078: The MultipleValueLookup export functionality was failing in certain situations.  To force the multipleValueLookup use the export filter which disallows response flushing, 6578706f7274=1 was added to 
each export url.    This represents a fixed parameter called PARAMETER_EXPORTING in TableTagParameters that is used by the export filter to understand when output should not be flushed.
--%> 
<a href="multipleValueLookup.do?methodToCall=export&d-16544-e=1&businessObjectClassName=${KualiForm.businessObjectClassName}&lookupResultsSequenceNumber=${KualiForm.lookupResultsSequenceNumber}&6578706f7274=1" target="_blank"><span class="export csv">CSV </span></a>|
<a href="multipleValueLookup.do?methodToCall=export&d-16544-e=2&businessObjectClassName=${KualiForm.businessObjectClassName}&lookupResultsSequenceNumber=${KualiForm.lookupResultsSequenceNumber}&6578706f7274=1" target="_blank"><span class="export excel">spreadsheet </span></a>| 
<a href="multipleValueLookup.do?methodToCall=export&d-16544-e=3&businessObjectClassName=${KualiForm.businessObjectClassName}&lookupResultsSequenceNumber=${KualiForm.lookupResultsSequenceNumber}&6578706f7274=1" target="_blank"><span class="export xml">XML </span> </a>
</div>
