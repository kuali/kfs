<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

<%@ attribute name="channelTitle" required="true" %>
<%@ attribute name="channelUrl" required="true" %>



        <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" cols="1" summary="need a summary here">
          <tbody>
            <tr>
              <td><img src="images-portal/topleftcorner.gif" alt="" width="6" height="9"></td>
              <td class="uportal-channel-topborder" width="100%"><img src="images-portal/transparent_002.gif" alt="" width="1" height="1"></td>
              <td><img src="images-portal/toprightcorner.gif" alt="" width="6" height="9"></td>
            </tr>
            <tr>
              <td class="uportal-channel-headerleftborder"><img src="images-portal/transparent_002.gif" alt="" width="6" height="1"></td>
              <td class="uportal-background-semidark" nowrap="nowrap"><span class="uportal-channel-title">${channelTitle}</span></td>
              <td class="uportal-channel-headerrightborder"><img src="images-portal/transparent_002.gif" alt="" width="6" height="1"></td>
            </tr>
            <tr>
              <td><img src="images-portal/headerbottomleft.gif" alt="" width="6" height="8"></td>
              <td class="uportal-channel-headerbottomborder"><img src="images-portal/transparent_002.gif" alt="" width="1" height="1"></td>
              <td><img src="images-portal/headerbottomright.gif" alt="" width="6" height="8"></td>
            </tr>
            <tr>
              <td class="uportal-channel-iconbarlinesleft" valign="bottom"><img src="images-portal/channellinesbottom.gif" alt="" width="1" height="1"></td>
              <td class="uportal-background-light" nowrap="nowrap"><div align="right"><a onclick="window.open('${channelUrl}','detachedChannel','toolbar=yes,location=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes,width=800,height=600')" href="#"><img src="images-portal/chan-head-det1.gif" alt="detach" width="15" height="15" hspace="1" border="0" title="detach"></a><a href="portal.do?selectedTab=edocs"><img src="images-portal/chan-head-back4.gif" alt="back to my current tab" width="137" height="15" hspace="1" border="0" title="focus"></a></div></td>
              <td class="uportal-channel-channelrightborder"><img src="images-portal/transparent_002.gif" alt="" width="1" height="1"></td>
            </tr>
            <tr>
              <td><img src="images-portal/channeltopleft.gif" alt="" width="6" height="7"></td>
              <td class="uportal-channel-channeltopborder"><img src="images-portal/transparent_002.gif" alt="" width="1" height="7"></td>
              <td ><img src="images-portal/channeltopright.gif" alt="" width="6" height="7"></td>
            </tr>
            <tr>
              <td class="uportal-channel-channelleftborder"><img src="images-portal/transparent_002.gif" alt="" width="6" height="1"></td>
              <td class="uportal-background-content">
                <table width="100%" border="0" cellpadding="0" cellspacing="0" summary="need a summary here">
                  <tbody>
                    <tr>
                      <td width="100%" valign="top">
                         <iframe src="${channelUrl}" onload='javascript: setFocusedIframeHeight("iframeportlet", 500);' name="iframeportlet" id="iframeportlet" hspace="0" vspace="0" style="height: 500px;" title="E-Doc" frameborder="0" height="500" scrolling="auto" width="100%"></iframe>
                      </td>
                      <td>
                        <img src="images-portal/transparent_002.gif" alt="" width="1" height="1">
                      </td>
                    </tr>
                  </tbody>
                </table>
              </td>
              <td class="uportal-channel-channelrightborder"><img src="images-portal/transparent_002.gif" alt="" width="1" height="1"></td>
            </tr>
            <tr>
              <td><img src="images-portal/bottomleftcorner.gif" alt="" width="6" height="6"></td>
              <td class="uportal-channel-bottomborder"><img src="images-portal/transparent_002.gif" alt="" width="1" height="1"></td>
              <td><img src="images-portal/bottomrightcorner.gif" alt="" width="6" height="6"></td>
            </tr>
          </tbody>
        </table>
