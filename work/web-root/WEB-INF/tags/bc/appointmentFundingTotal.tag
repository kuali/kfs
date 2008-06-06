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

<%@ attribute name="pcafAware" required="true" type="java.lang.Object"
			  description="The object containing the appointment funding lines"%>			  			  

<table style="border-top: 1px solid rgb(153, 153, 153); width: inherit;" cellpadding="0" cellspacing="0" class="datatable">
	<tr>
		<th>&nbsp;</th>
		<th>Amount</th>
		<th>Standard Hours</th>
		<th>FTE</th>
	</tr>
	
	<tr>
		<th align="right">CSF:</th>		             	
		
		<td>
			<div class="right">		
         		<fmt:formatNumber value="${pcafAware.csfAmountTotal}" type="number" groupingUsed="true"/>
			</div>
		</td>	             	
		
		<td>
			<div class="right">
         		<fmt:formatNumber value="${pcafAware.csfStandardHoursTotal}" type="number" groupingUsed="true" minFractionDigits="2" />
			</div>
		</td>	             	
		
		<td>
			<div class="right">
         		<fmt:formatNumber value="${pcafAware.csfFullTimeEmploymentQuantityTotal}" type="number" groupingUsed="true" minFractionDigits="4" />
			</div>
		</td>	             	         
	</tr>
	
	<tr>
		<th align="right">Request:</th>	

		<td>
			<div class="right">		
         		<fmt:formatNumber value="${pcafAware.appointmentRequestedAmountTotal}" type="number" groupingUsed="true"/>
			</div>
		</td>	             	
		
		<td>
			<div class="right">
         		<fmt:formatNumber value="${pcafAware.appointmentRequestedStandardHoursTotal}" type="number" groupingUsed="true" minFractionDigits="2" />
			</div>
		</td>	             	
		
		<td>
			<div class="right">	
         		<fmt:formatNumber value="${pcafAware.appointmentRequestedFteQuantityTotal}" type="number" groupingUsed="true" minFractionDigits="4" />
			</div>
		</td>	             		         
	</tr>
		
	<tr>
		<th align="right">Leaves Request CSF:</th>	

         <td>
			<div class="right">
				<fmt:formatNumber value="${pcafAware.appointmentRequestedCsfAmountTotal}" type="number" groupingUsed="true"/>
			</div>
		</td>	             	
		
		<td>
			<div class="right">
         		<fmt:formatNumber value="${pcafAware.appointmentRequestedCsfStandardHoursTotal}" type="number" groupingUsed="true" minFractionDigits="2" />
	    	</div>
		</td>

		<td>
			<div class="right">             	
         		<fmt:formatNumber value="${pcafAware.appointmentRequestedCsfFteQuantityTotal}" type="number" groupingUsed="true" minFractionDigits="4" />
         	</div>
		</td>	         
	</tr>
</table>	