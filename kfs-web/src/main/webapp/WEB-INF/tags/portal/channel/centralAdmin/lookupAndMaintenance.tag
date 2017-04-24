<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Lookup and Maintenance" />
<div class="body">
	<c:if test="${ConfigProperties.module.capital.asset.enabled == 'true'}">
	    <strong>Capital Asset Management</strong><br />
	    <ul class="chan">
			<li><portal:portalLink displayTitle="true" title="Asset Global (Add)" url="${ConfigProperties.application.url}/kr/lookup.do?businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType&conversionFields=acquisitionTypeCode:acquisitionTypeCode&returnLocation=portal.do&docFormKey=88888888" /></li>
			<li><portal:portalLink displayTitle="true" title="Asset Payment" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetPayment&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
			<li><portal:portalLink displayTitle="true" title="Asset Retirement Global" url="${ConfigProperties.application.url}/kr/lookup.do?businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetRetirementReason&conversionFields=retirementReasonCode:retirementReasonCode&returnLocation=portal.do&docFormKey=88888888" /></li>
		</ul>
	</c:if>
    <strong>Chart of Accounts</strong><br />
    <ul class="chan">
	    <li><portal:portalLink displayTitle="true" title="Object Code Global" url="${ConfigProperties.application.url}/kr/maintenance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.ObjectCodeGlobal" /></li>
	</ul>
    <strong>Financial Processing</strong><br />
	<ul class="chan">
	    <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Travel Company" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.TravelCompanyCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	</ul>
</div>
<channel:portalChannelBottom />
