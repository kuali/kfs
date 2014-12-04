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
  

