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

<c:set var="pointOfViewOrgAttributes" value="${DataDictionary.BudgetConstructionOrganizationReports.attributes}" />

 <table border="0" cellpadding="0" cellspacing="0" class="datatable" summary="">
 <tbody>
 	<tr>
 		<td class="subhead">Current Point of View Organization Selection</td>
 	</tr>
 	<tr>
	 	<td>
		 	<div align=center><br>   
				<table style="width:auto" border="0" cellpadding="0" cellspacing="0" class="nobord">
			    	<tr>
						<td width="200" style="border-style:solid; border-color:#999999; border-width:1px; padding:6px; background-color:#EAE9E9">
			            	<div align="center">
			                     <strong>Select Point of View: <br><br>
			                        <kul:htmlControlAttribute property="currentPointOfViewKeyCode" attributeEntry="${pointOfViewOrgAttributes.selectionKeyCode}" onchange="refreshPointOfView(this.form)" readOnly="false" styleClass="grid" />
			                      	<br>
			                     </strong>
			                </div>
			            </td>
			            <c:if test="${!empty KualiForm.pointOfViewOrg.chartOfAccountsCode}"> 	                    
			            <td width="30" class="nobord" >&nbsp;</td>
				        <td class="nobord" >
			    			<div align="left">
			            		 <p align="center"><strong>Currently Selected:</strong><strong><br><br></strong>
				                 <kul:htmlControlAttribute property="pointOfViewOrg.chartOfAccountsCode" attributeEntry="${pointOfViewOrgAttributes.chartOfAccountsCode}" readOnly="true" readOnlyBody="true">
				    	             <kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=${pointOfViewOrg.chartOfAccountsCode}" render="${!empty KualiForm.pointOfViewOrg.chartOfAccountsCode}">
				    	             ${KualiForm.pointOfViewOrg.chartOfAccountsCode}
				    	         	 </kul:inquiry>
			        	         </kul:htmlControlAttribute>
			            	     -
			                	 <kul:htmlControlAttribute property="pointOfViewOrg.organizationCode" attributeEntry="${pointOfViewOrgAttributes.organizationCode}" readOnly="true" readOnlyBody="true">
			                   	 	<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Organization" keyValues="chartOfAccountsCode=${KualiForm.pointOfViewOrg.chartOfAccountsCode}&amp;organizationCode=${KualiForm.pointOfViewOrg.organizationCode}" render="${!empty KualiForm.pointOfViewOrg.organizationCode}">
				    	             ${KualiForm.pointOfViewOrg.organizationCode}
			      			        </kul:inquiry>
				                 </kul:htmlControlAttribute>
				                 <span class="fineprint">
				                 	(
				                 		<kul:htmlControlAttribute 
				                 				property="pointOfViewOrg.organizationCode" 
				                 				attributeEntry="${pointOfViewOrgAttributes.organizationCode}"				                 				 
				                 				readOnly="true" 
				                 				readOnlyBody="true">${KualiForm.pointOfViewOrg.organization.organizationName}&nbsp;</kul:htmlControlAttribute>
				                 	)
			    	             </span>
			        		</div>
			            </td>
			            </c:if>
			        </tr>
			    </table><br/>
	        </div>
	 	</td>
	</tr>
 <tbody>	
</table>

<c:if test="${!empty KualiForm.previousBranchOrgs}">
	<bc:budgetConstructionOrgSelectionPreviousBranches />
</c:if>
		
<c:if test="${!empty KualiForm.selectionSubTreeOrgs}">
	<bc:budgetConstructionOrgSelectionSubTreeOrgs />
</c:if>  
  

