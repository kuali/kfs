<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

        <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" cols="1" summary="need a summary here">
          <tbody>
            <tr>
              <td><img src="images-portal/topleftcorner.gif" alt="" width="6" height="9"></td>
              <td width="100%" colspan="3" class="uportal-channel-topborderimmute"><img src="images-portal/transparent_002.gif" alt="" width="1" height="1"></td>
              <td><img src="images-portal/toprightcorner.gif" alt="" width="6" height="9"></td>
            </tr>
            <tr>
              <td class="uportal-channel-iconbarlinesleft" valign="bottom"><img src="images-portal/channellinesbottom.gif" alt="" width="1" height="1"></td>
              <td nowrap="nowrap" class="uportal-background-light">
                <div align="left">
                  <portal:portalLink displayTitle="false" title='Action List' url='${ConfigProperties.workflow.base.url}/ActionList.do'><img src="images-portal/icon-port-actionlist.gif" width="91" height="19" border="0"></portal:portalLink>
                  <portal:portalLink displayTitle="false" title='Document Search' url='${ConfigProperties.workflow.base.url}/DocumentSearch.do'><img src="images-portal/icon-port-docsearch.gif" width="96" height="19" border="0"></portal:portalLink>
                  <portal:portalLink displayTitle="false" title='Workflow Services' url='${ConfigProperties.workflow.base.url}/Backdoor.do'><img src="images-portal/icon-port-wfservices.gif" width="141" height="19" border="0"></portal:portalLink>
                </div>
              </td>
              <td valign="middle" nowrap="nowrap" class="uportal-background-light">
                <div align="right">
                  <c:choose>
	                  <c:when test="${empty UserSession.loggedInUserNetworkId}" >
	                  	<strong>You are not logged in.</strong>
	                  </c:when>
	                  <c:otherwise>
		                <strong>Logged in User:&nbsp;${UserSession.loggedInUserNetworkId}</strong>
		              </c:otherwise>
		          </c:choose>
                </div>
              </td>
              <td valign="middle" nowrap="nowrap" class="uportal-background-light">
                <div align="right">
                  <input name="textfield" type="text" class="searchbox" size="10">
                  <input name="imageField" type="image" src="images-portal/search.gif" width="46" height="14" border="0">
                </div>
              </td>
              <td class="uportal-channel-channelrightborder">
                <img src="images-portal/transparent_002.gif" alt="" width="1" height="1">
              </td>
            </tr>
            <tr>
              <td><img src="images-portal/bottomleftcorner.gif" alt="" width="6" height="6"></td><td colspan="3" class="uportal-channel-bottomborder"><img src="images-portal/transparent_002.gif" alt="" width="1" height="1"></td><td><img src="images-portal/bottomrightcorner.gif" alt="" width="6" height="6"></td>
            </tr>
          </tbody>
        </table>
