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

<%@ attribute name="pcafAware" required="true" type="java.lang.Object"
			  description="The object containing the appointment funding lines"%>			  			  

<table style="border-top: 1px solid rgb(153, 153, 153); width:90%;" cellpadding="0" cellspacing="0" border="0" class="datatable">
	<tr>		
		<th style="width: 15%;">&nbsp;</th>
		<th style="width: 25%;">&nbsp;</th>
		<th style="width: 10%;">Amount</th>
		<th style="width: 5%;">&nbsp;</th>
		<th style="width: 5%;">&nbsp;</th>
		<th style="width: 10%;">Standard Hours</th>
		<th style="width: 10%;">FTE</th>
		<th style="width: 10%;">&nbsp;</th>
		<th style="width: 10%;">&nbsp;</th>
	</tr>
	
	<tr>
		<th style="text-align: right;">CSF:</th>
		<td>&nbsp;</td>		             	
		
		<td style="text-align: right;">		
         	<fmt:formatNumber value="${KualiForm.csfAmountTotal}" type="number" groupingUsed="true"/>
		</td>
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>	             	
		
		<td style="text-align: right;">
			<fmt:formatNumber value="${KualiForm.csfStandardHoursTotal}" type="number" groupingUsed="true" minFractionDigits="2" />
		</td>	             	
		
		<td style="text-align: right;">
         	<fmt:formatNumber value="${KualiForm.csfFullTimeEmploymentQuantityTotal}" type="number" groupingUsed="true" minFractionDigits="5" />
		</td>
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>	             	         
	</tr>
	
	<tr>
		<th style="text-align: right;">Request:</th>	
		
		<td>&nbsp;</td>

		<td style="text-align: right;">
			<fmt:formatNumber value="${KualiForm.appointmentRequestedAmountTotal}" type="number" groupingUsed="true"/>
		</td>	             	
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		
		<td style="text-align: right;">
         	<fmt:formatNumber value="${KualiForm.appointmentRequestedStandardHoursTotal}" type="number" groupingUsed="true" minFractionDigits="2" />	
		</td>	             	
		
		<td style="text-align: right;">
         	<fmt:formatNumber value="${KualiForm.appointmentRequestedFteQuantityTotal}" type="number" groupingUsed="true" minFractionDigits="5" />	
		</td>
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>	             		         
	</tr>
		
	<tr>
		<th style="text-align: right;">Leaves Request CSF:</th>	
		
		<td>&nbsp;</td>

        <td style="text-align: right;">
			<fmt:formatNumber value="${KualiForm.appointmentRequestedCsfAmountTotal}" type="number" groupingUsed="true"/>			
		</td>	             	
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		
		<td style="text-align: right;">
         	<fmt:formatNumber value="${KualiForm.appointmentRequestedCsfStandardHoursTotal}" type="number" groupingUsed="true" minFractionDigits="2" />	    	
		</td>

		<td style="text-align: right;">             	
         	<fmt:formatNumber value="${KualiForm.appointmentRequestedCsfFteQuantityTotal}" type="number" groupingUsed="true" minFractionDigits="5" />
		</td>
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>	         
	</tr>
</table>	
