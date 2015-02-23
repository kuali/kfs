<%--
 Copyright 2008-2009 The Kuali Foundation
 
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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="afflnIdx" required="true" description="In the array of affiliations on the IdentityManagementPersonDocument, the index of the affiliation to display employee information records for." %>
<c:set var="docEmploymentInfoAttributes" value="${DataDictionary.PersonDocumentEmploymentInfo.attributes}" />

<kul:subtab lookedUpCollectionName="empInfo" width="${tableWidth}" subTabTitle="Employment Information">      
	<table cellpadding="0" cellspacing="0" summary="">
       	<tr>
            <th width="5%" rowspan="20" style="border-style:none">&nbsp;</th>
       		<th><div align="left">&nbsp</div></th>  
       		<kim:cell inquiry="${inquiry}" isLabel="true" attributeEntry="${docEmploymentInfoAttributes.employeeId}" noColon="true" />
       		<kim:cell inquiry="${inquiry}" isLabel="true" attributeEntry="${docEmploymentInfoAttributes.primary}" noColon="true" />
       		<kim:cell inquiry="${inquiry}" isLabel="true" attributeEntry="${docEmploymentInfoAttributes.employmentStatusCode}" noColon="true" />
       		<kim:cell inquiry="${inquiry}" isLabel="true" attributeEntry="${docEmploymentInfoAttributes.employmentTypeCode}" noColon="true" />
       		<kim:cell inquiry="${inquiry}" isLabel="true" attributeEntry="${docEmploymentInfoAttributes.baseSalaryAmount}" noColon="true" />
       		<kim:cell inquiry="${inquiry}" isLabel="true" attributeEntry="${docEmploymentInfoAttributes.primaryDepartmentCode}" noColon="true" />
	        <c:if test="${not inquiry}">	
	              	<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
	        </c:if>
        </tr>     
      	<c:if test="${not inquiry and not readOnlyEntity}">	          	
             <tr>
				<th class="infoline">Add:</th>
				
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].newEmpInfo.employeeId" attributeEntry="${docEmploymentInfoAttributes.employeeId}" readOnly="${readOnlyEntity}" />
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].newEmpInfo.primary" attributeEntry="${docEmploymentInfoAttributes.primary}" readOnly="${readOnlyEntity}" /> 
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].newEmpInfo.employmentStatusCode" attributeEntry="${docEmploymentInfoAttributes.employmentStatusCode}" readOnly="${readOnlyEntity}" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].newEmpInfo.employmentTypeCode" attributeEntry="${docEmploymentInfoAttributes.employmentTypeCode}" readOnly="${readOnlyEntity}" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].newEmpInfo.baseSalaryAmount" attributeEntry="${docEmploymentInfoAttributes.baseSalaryAmount}" readOnly="${readOnlyEntity}" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].newEmpInfo.primaryDepartmentCode" attributeEntry="${docEmploymentInfoAttributes.primaryDepartmentCode}" readOnly="${readOnlyEntity}" />              
                <td class="infoline">
					<div align="center">
						<html:image property="methodToCall.addEmpInfo.line${afflnIdx}.anchor${tabKey}"
						src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
					</div>
                </td>
       		</tr>         
  		</c:if>          
    	<c:forEach var="empInfo" items="${KualiForm.document.affiliations[afflnIdx].empInfos}" varStatus="status">
	       	<tr>
				<th class="infoline">
					<c:out value="${status.index+1}" />
				</th>
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].empInfos[${status.index}].employeeId"  attributeEntry="${docEmploymentInfoAttributes.employeeId}" readOnly="${readOnlyEntity}" />
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].empInfos[${status.index}].primary" attributeEntry="${docEmploymentInfoAttributes.primary}" readOnly="${readOnlyEntity}" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].empInfos[${status.index}].employmentStatusCode" attributeEntry="${docEmploymentInfoAttributes.employmentStatusCode}"  readOnlyAlternateDisplay="${fn:escapeXml(empInfo.employmentStatus.name)}" readOnly="${readOnlyEntity}" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].empInfos[${status.index}].employmentTypeCode" attributeEntry="${docEmploymentInfoAttributes.employmentTypeCode}" readOnlyAlternateDisplay="${fn:escapeXml(empInfo.employmentType.name)}" readOnly="${readOnlyEntity}" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].empInfos[${status.index}].baseSalaryAmount" attributeEntry="${docEmploymentInfoAttributes.baseSalaryAmount}" readOnly="${readOnlyEntity}" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.affiliations[${afflnIdx}].empInfos[${status.index}].primaryDepartmentCode" attributeEntry="${docEmploymentInfoAttributes.primaryDepartmentCode}" readOnly="${readOnlyEntity}" />
           			<c:if test="${not inquiry}">						
						<td>
							<div align=center>&nbsp;
				        	     <c:choose>
				        	       	<c:when test="${empInfo.edit  or readOnlyEntity}">
				        	          <img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
				        	       	</c:when>
				        	       	<c:otherwise>
				        	          <html:image property='methodToCall.deleteEmpInfo.line${afflnIdx}:${status.index}.anchor${currentTabIndex}'
											src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass='tinybutton'/>
				        	       	</c:otherwise>
				        	     </c:choose>  
							</div>
		                </td>
	           		</c:if>     
	    		</tr>	            
        	</c:forEach>        
		<tr>
            <td colspan=10 style="padding:0px; border-style:none; height:22px; background-color:#F6F6F6">&nbsp;</td>
        </tr>            
	</table>
</kul:subtab>
