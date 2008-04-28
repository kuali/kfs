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

<kul:tab tabTitle="Report Information" defaultOpen="true" tabErrorKey="${KFSConstants.DV_CONTACT_TAB_ERRORS}">
	<c:set var="erAttributes" value="${DataDictionary.EffortCertificationDocument.attributes}" />
	<c:set var="document" value="${KualiForm.document}" />
	<c:set var="dateFormatPattern" value="MM/dd/yyyy"/>

 	<div class="tab-container" align=center > 
		<div class="h2-container"><h2>Report Information</h2></div>
		
	 	<table summary="" cellpadding="0" cellspacing="0"><tbody>
			<tr>
				<th scope="row">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${erAttributes['employee.personName']}" noColon="true" />
					</div>
				</th>
			    <td>
			    	<kul:inquiry 
						boClassName="org.kuali.core.bo.user.UniversalUser" 
						keyValues="personPayrollIdentifier=${document.emplid}&personUniversalIdentifier=${document.employee.personUniversalIdentifier}" 
						render="true">
						${document.employee.personName}
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
						boClassName="org.kuali.module.effort.bo.EffortCertificationReportDefinition" 
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
