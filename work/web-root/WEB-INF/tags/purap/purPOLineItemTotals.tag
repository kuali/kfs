<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields." %>

<tr>
	<td colspan="10" class="subhead">
	    <span class="subhead-left">Purchase Order Line Item Totals</span>
	</td>
</tr>

<tr>
	<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.itemTotalPoEncumbranceAmount}" colspan="3" />
	<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.itemTotalPoEncumbranceAmountRelieved}" colspan="3" />
	<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.itemTotalPoPaidAmount}" colspan="4" />
</tr>

<tr>
	<td class="infoline" colspan="3">
	    <div align="right">
	    <kul:htmlControlAttribute
		    attributeEntry="${documentAttributes.itemTotalPoEncumbranceAmount}"
		    property="document.itemTotalPoEncumbranceAmount"
		    readOnly="true" />
		</div>
	</td>
	<td class="infoline" colspan="3">
		<div align="right">
	    <kul:htmlControlAttribute
		    attributeEntry="${documentAttributes.itemTotalPoEncumbranceAmountRelieved}"
		    property="document.itemTotalPoEncumbranceAmountRelieved"
		    readOnly="true" />
		</div>
	</td>
	<td class="infoline" colspan="4">
		<div align="right">
	    <kul:htmlControlAttribute
		    attributeEntry="${documentAttributes.itemTotalPoPaidAmount}"
		    property="document.itemTotalPoPaidAmount"
		    readOnly="true" />	
		</div>
	</td>				    
</tr>

<tr>
	<th height=30 colspan="10">&nbsp;</th>
</tr>
