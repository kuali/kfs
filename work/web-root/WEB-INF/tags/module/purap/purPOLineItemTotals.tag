<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="mainColumnCount" required="true" %>

<tr>
	<td colspan="${mainColumnCount}" class="subhead">
	    <span class="subhead-left">Purchase Order Line Item Totals</span>
	</td>
</tr>

<tr>
	<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.itemTotalPoEncumbranceAmount}" colspan="4"/>
	<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.itemTotalPoEncumbranceAmountRelieved}" colspan="4"/>
	<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.itemTotalPoPaidAmount}" colspan="${mainColumnCount-8}"/>
</tr>

<tr>
	<td class="infoline" colspan="4">
	    <div align="right">
	    <kul:htmlControlAttribute
		    attributeEntry="${documentAttributes.itemTotalPoEncumbranceAmount}"
		    property="document.itemTotalPoEncumbranceAmount"
		    readOnly="true" />
		</div>
	</td>
	<td class="infoline" colspan="4">
		<div align="right">
	    <kul:htmlControlAttribute
		    attributeEntry="${documentAttributes.itemTotalPoEncumbranceAmountRelieved}"
		    property="document.itemTotalPoEncumbranceAmountRelieved"
		    readOnly="true" />
		</div>
	</td>
	<td class="infoline" colspan="${mainColumnCount-8}">
		<div align="right">
	    <kul:htmlControlAttribute
		    attributeEntry="${documentAttributes.itemTotalPoPaidAmount}"
		    property="document.itemTotalPoPaidAmount"
		    readOnly="true" />	
		</div>
	</td>				    
</tr>

<tr>
	<th height=30 colspan="${mainColumnCount}">&nbsp;</th>
</tr>
