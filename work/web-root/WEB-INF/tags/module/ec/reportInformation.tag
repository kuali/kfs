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

<kul:tab tabTitle="Report Information" defaultOpen="true" tabErrorKey="${KFSConstants.DV_CONTACT_TAB_ERRORS}">
	<c:set var="erAttributes" value="${DataDictionary.EffortCertificationDocument.attributes}" />
	<c:set var="document" value="${KualiForm.document}" />
	<c:set var="dateFormatPattern" value="MM/dd/yyyy"/>

 	<div class="tab-container" align=center > 
		<h3>Report Information</h3>
		
	 	<table summary="" cellpadding="0" cellspacing="0"><tbody>
			<tr>
				<th scope="row">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${erAttributes['employee.name']}" noColon="true" />
					</div>
				</th>
			    <td>
			    	<kul:inquiry 
						boClassName="org.kuali.rice.kim.api.identity.Person" 
						keyValues="employeeId=${document.emplid}&principalId=${document.employee.principalId}" 
						render="true">
						${document.employee.name}
					</kul:inquiry>
				</td>
			
				<th scope="row">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${erAttributes['effortCertificationReportDefinition.effortCertificationReportBeginFiscalYear']}" noColon="true" />
					</div>
				</th>
				<td>
					<fmt:formatDate 
						value="${KualiForm.reportPeriodBeginDate}" 
						pattern="${dateFormatPattern}"/>
				</td>
			</tr>
			
			<tr>
			  	<th scope="row">
			  		<div align="right">
			  			<kul:htmlAttributeLabel attributeEntry="${erAttributes.effortCertificationReportNumber}" noColon="true" />
			  		</div>
			    </th>
			    <td>
			         <kul:inquiry 
						boClassName="org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition" 
						keyValues="universityFiscalYear=${document.universityFiscalYear}&effortCertificationReportNumber=${document.effortCertificationReportNumber}" 
						render="true">
			         	${document.universityFiscalYear}-${document.effortCertificationReportNumber}
					</kul:inquiry>
					
					<c:if test="${document.effortCertificationDocumentCode}" >
					  &nbsp;&nbsp;<font color="red"><bean:message key="${EffortConstants.RECREATED_DOCUMENT_MESSAGE_KEY}" /></font>
					</c:if>
				</td>
			
				<th scope="row">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${erAttributes['effortCertificationReportDefinition.effortCertificationReportEndFiscalYear']}" noColon="true" />
					</div>
				</th>
				<td>
					<fmt:formatDate 
						value="${KualiForm.reportPeriodEndDate}" 
						pattern ="${dateFormatPattern}"/>
				</td>
			</tr>   
	     </tbody></table>
	</div>
</kul:tab>

