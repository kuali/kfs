<%--
 Copyright 2007-2009 The Kuali Foundation
 
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
