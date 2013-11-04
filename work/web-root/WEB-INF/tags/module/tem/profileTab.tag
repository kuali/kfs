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
<c:set var="profileAttributes" value="${DataDictionary.TemProfile.attributes}" />
<c:set var="travelerAttributes" value="${DataDictionary.TravelerDetail.attributes}" />
<kul:tab tabTitle="TEM Profile" defaultOpen="true">
	<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<td colspan="2" class="tab-subhead">&nbsp;</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.travelerTypeCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.travelerTypeCode }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.lastUpdate}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.lastUpdate }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.updatedBy}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.updatedBy }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.firstName}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.firstName }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.middleName}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.middleName }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.lastName}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.lastName }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.employeeId}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.employeeId }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.homeDepartment}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.homeDepartment }</td>
			</tr>
			
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.dateOfBirth}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.dateOfBirth }</td>
			</tr>
			<tr>
				<td colspan="2" class="tab-subhead">Default Accounting</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.defaultChartCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.defaultChartCode }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.defaultAccount}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.defaultAccount }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.defaultSubAccount}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.defaultSubAccount }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.defaultProjectCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.defaultProjectCode }</td>
			</tr>
			<tr>
				<td colspan="2" class="tab-subhead">Billing Info</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.streetAddressLine1}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.streetAddressLine1 }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.streetAddressLine2}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.streetAddressLine2 }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.cityName}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.cityName }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.stateCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.stateCode }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.zipCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.zipCode }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.countryCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.countryCode }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.phoneNumber}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.phoneNumber }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.emailAddress}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.emailAddress }</td>
			</tr>
		</table>

	</div>

</kul:tab>

<!--
<bean parent="MaintainableSubSectionHeaderDefinition" p:name="TEM Profile"/>
				<bean parent="MaintainableFieldDefinition" p:name="profileId" p:required="true" p:unconditionallyReadOnly="true" p:defaultValueFinderClass="org.kuali.kfs.module.tem.businessobject.options.TemProfileIdFinder" />
				<bean parent="MaintainableFieldDefinition" p:name="travelerTypeCode" p:required="true" p:noLookup="true" />
				<bean parent="MaintainableFieldDefinition" p:name="lastUpdate" p:required="false" p:unconditionallyReadOnly="true" />
				<bean parent="MaintainableFieldDefinition" p:name="updatedBy" p:required="false" p:unconditionallyReadOnly="true" />
				<bean parent="MaintainableFieldDefinition" p:name="firstName" p:required="true" />
				<bean parent="MaintainableFieldDefinition" p:name="middleName" p:required="false" />
				<bean parent="MaintainableFieldDefinition" p:name="lastName" p:required="true" />
				<bean parent="MaintainableFieldDefinition" p:name="employeeId" p:required="false" p:unconditionallyReadOnly="true" />
				<bean parent="MaintainableFieldDefinition" p:name="homeDepartment" p:required="true" p:lookupReadOnly="true" p:overrideFieldConversions="organizationCode:homeDeptOrgCode,chartOfAccountsCode:homeDeptChartOfAccountsCode" p:overrideLookupClass="org.kuali.kfs.coa.businessobject.Organization" />
				<bean parent="MaintainableFieldDefinition" p:name="driversLicenseNumber" p:required="false" />
				<bean parent="MaintainableFieldDefinition" p:name="driversLicenseState" p:required="false" />
				<bean parent="MaintainableFieldDefinition" p:name="driversLicenseExpDate" p:required="false" />
				<bean parent="MaintainableFieldDefinition" p:name="motorVehicleRecordCheck" p:required="false" />
				<bean parent="MaintainableFieldDefinition" p:name="dateOfBirth" p:required="true" />
				<bean parent="MaintainableFieldDefinition" p:name="citizenship" p:required="false" />
				<bean parent="MaintainableFieldDefinition" p:name="nonResidentAlien" p:required="true" p:defaultValue="" />
				<bean parent="MaintainableFieldDefinition" p:name="gender" p:required="true" />
				<bean parent="MaintainableSubSectionHeaderDefinition" p:name="Default Accounting"/>
				<bean parent="MaintainableFieldDefinition" p:name="defaultChartCode" p:required="true" />
				<bean parent="MaintainableFieldDefinition" p:name="defaultAccount" p:required="false" />
				<bean parent="MaintainableFieldDefinition" p:name="defaultSubAccount" p:required="false" />
				<bean parent="MaintainableFieldDefinition" p:name="defaultProjectCode" p:required="false" />
				<bean parent="MaintainableSubSectionHeaderDefinition" p:name="Address"/>
				<bean parent="MaintainableFieldDefinition" p:name="temProfileAddress.streetAddressLine1" p:required="true" p:noLookup="true" />
				<bean parent="MaintainableFieldDefinition" p:name="temProfileAddress.streetAddressLine2" p:noLookup="true" />
				<bean parent="MaintainableFieldDefinition" p:name="temProfileAddress.cityName" p:required="true" p:noLookup="true" />
				<bean parent="MaintainableFieldDefinition" p:name="temProfileAddress.stateCode" p:required="false" p:noLookup="true" />
				<bean parent="MaintainableFieldDefinition" p:name="temProfileAddress.zipCode" p:required="false" p:noLookup="true" />
				<bean parent="MaintainableFieldDefinition" p:name="temProfileAddress.countryCode" p:required="true" p:noLookup="true" />
				<bean parent="MaintainableSubSectionHeaderDefinition" p:name="Contact Info"/>
				<bean parent="MaintainableFieldDefinition" p:name="phoneNumber" p:required="false" />
				<bean parent="MaintainableFieldDefinition" p:name="emailAddress" p:required="false" />



-->