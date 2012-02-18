<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:ww="/struts-tags"
  xmlns:decorator="http://www.opensymphony.com/sitemesh/decorator"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
  xmlns:rokta="/rokta"
  version="2.0">

  <jsp:output doctype-root-element="html" omit-xml-declaration="true"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <jsp:directive.page contentType="text/html" />
  <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  
  <!--
	Copyright 2010 Alex Jones
	Licensed to the Apache Software Foundation (ASF) under one
	or more contributor license agreements.  See the NOTICE file
	distributed with this work for additional information
	regarding copyright ownership.  The ASF licenses this file
	to you under the Apache License, Version 2.0 (the
	"License"); you may not use this file except in compliance
	with the License.  You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing,
	software distributed under the License is distributed on an
	"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
	KIND, either express or implied.  See the License for the
	specific language governing permissions and limitations
	under the License.    
  -->
  <head>
    <title>West Ham Attendence</title>
    <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
    <style type="text/css">
    	.Sun {
    		background-color: yellow;
    	}
    	.Mon, .Tue, .Wed, .Thu, .Fri {
    		background-color: aqua;
    	}
    </style>
	</head>
	<body>
		<form action="">
			<select name="season">
				<c:forEach items="${seasons}" var="currentSeason">
					<c:choose>
						<c:when test="${currentSeason == season}">
							<option value="${currentSeason}" selected="selected">
								The <c:out value="${currentSeason}"/>/<c:out value="${currentSeason + 1}"/> season
							</option>
						</c:when>
						<c:otherwise>
							<option value="${currentSeason}">
								The <c:out value="${currentSeason}"/>/<c:out value="${currentSeason + 1}"/> season
							</option>						
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			<input type="submit" value="Go"/>
		</form>
		<form action="" method="post">
			<table>
				<c:forEach items="${months}" var="month">
					<tr><td><fmt:formatDate value="${month}" pattern="MMMM yyyy"/></td></tr>
					<c:forEach items="${gamesByMonth[month]}" var="game">
						<c:set var="class"><fmt:formatDate value="${game.datePlayed}" pattern="EEE"/></c:set>
						<tr class="${class}">
							<td><fmt:formatDate value="${game.datePlayed}" pattern="EEE dd HH:mm"/></td>
							<td><c:out value="${game.location}"/></td>
							<td><c:out value="${game.competition}"/></td>
							<td><c:out value="${game.opponents}"/></td>
							<td><c:out value="${game.result}"/></td>
							<td><c:out value="${game.televisionChannel}"/></td>
							<td>
								<c:choose>
									<c:when test="${game.attended}">
										<input name="attended" value="${game.id}" type="checkbox" checked="checked"/>	
									</c:when>
									<c:otherwise>
										<input name="attended" value="${game.id}" type="checkbox"/>
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:if test="${game.seasonTicketsAvailable != null}">
									<fmt:formatDate value="${game.seasonTicketsAvailable}" pattern="EEE dd MMM"/>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>
			</table>
			<input type="hidden" name="season" value="${season}"/>
			<input type="submit"/>
		</form>
		<h3>Results League</h3>
		<table>
			<tr>
				<th>Opponents</th><th>P</th><th>W</th><th>D</th><th>L</th><th>F</th><th>A</th><th>Pts.</th>
			</tr>
			<c:forEach items="${league}" var="leagueRow">
				<tr>
					<td><c:out value="${leagueRow.team}"/></td>
					<td><c:out value="${leagueRow.played}"/></td>
					<td><c:out value="${leagueRow.won}"/></td>
					<td><c:out value="${leagueRow.drawn}"/></td>
					<td><c:out value="${leagueRow.lost}"/></td>
					<td><c:out value="${leagueRow.for}"/></td>
					<td><c:out value="${leagueRow.against}"/></td>
					<td><c:out value="${leagueRow.points}"/></td>
				</tr>
			</c:forEach>
		</table>
	</body>
  </html>
</jsp:root>