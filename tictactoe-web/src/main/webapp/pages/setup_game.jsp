<!DOCTYPE html>
<%--
  Created by dani0.
  Date: 4/25/2021
  Time: 7:02 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="hu.alkfejl.constants.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <title>New Game</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link href="../img/logo.png" rel="icon">

    <script>
        function match() {
            let pattern = /^(?:[01]\d|2[0123]):([012345]\d):([012345]\d)$/;
            let turntime = document.getElementById('turntime').value;
            let gametime = document.getElementById('gametime').value;
            let submit = document.getElementById('submitBtn');

            submit.disabled = !(pattern.test(turntime) && pattern.test(gametime));
        }

        function slider() {
            let sliderValue = document.getElementsByName('boardsize')[0].value;
            document.getElementsByClassName('sliderMsg')[0].innerHTML = "Size: " + sliderValue; // index 0 is sufficient because there will only ever be 1 slider on the page
        }
    </script>

    <%
        if (request.getParameter(Constants.FORM_INPUT_NAME_IS_AI) != null && request.getParameter(Constants.FORM_INPUT_NAME_IS_AI).equals("false")) {
            if (session.getAttribute(Constants.SESSION_OPPONENT_PLAYER_ID) != null) {
                response.sendRedirect("pvp_page.jsp"); // Send the player to the game if he is already chose
            }
        }
    %>
</head>
<body>
<jsp:include page="/SetupGameController"/>
<div class="container">
    <div class="container d-grid gap-2 col-3 mx-auto mt-3">
        <div class="card">
            <div class="card-body p-4 d-flex flex-column justify-content-center align-items-center">
                <h3 class="card-title">Setup a new game</h3>
                    <c:choose>
                        <c:when test="${param[Constants.FORM_INPUT_NAME_IS_AI].equals('true')}">
                            <form method="post" action="pva_page.jsp">
                                <div class="mb-3">
                                    <label for="boardsize">Choose the board size:</label>
                                    <input id="boardsize" type="range" name="${Constants.FORM_INPUT_NAME_BOARD_SIZE}" value="5" min="3" max="15" step="1" oninput="slider()">
                                    <div class="sliderMsg">Size: 5</div>
                                </div>
                                <div class="mb-3">
                                    <label for="turntime" class="form-label">Turn time:</label>
                                    <input id="turntime" class="form-control" type="text" name="${Constants.FORM_INPUT_NAME_TURN_TIMER}" value="00:00:30"
                                           pattern="^(?:[01]\d|2[0123]):([012345]\d):([012345]\d)$"
                                           aria-describedby="turntimehelp" oninput="match()">
                                    <div id="turntimehelp" class="form-text">Time must be in the format of 'hh:mm:ss'</div>
                                </div>
                                <div class="mb-3">
                                    <label for="gametime" class="form-label">Game time:</label>
                                    <input id="gametime" class="form-control" type="text" name="${Constants.FORM_INPUT_NAME_GAME_TIMER}" value="00:10:00"
                                           pattern="^(?:[01]\d|2[0123]):([012345]\d):([012345]\d)$"
                                           aria-describedby="gametimehelp" oninput="match()">
                                    <div id="gametimehelp" class="form-text">Time must be in the format of 'hh:mm:ss'</div>
                                </div>
                                <input id="submitBtn" type="submit" value="Start" class="btn btn-primary">
                            </form>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${requestScope.is_empty}">
                                <script>
                                    alert("There are currently no online players :(");
                                    window.location.replace("../index.jsp")
                                </script>
                            </c:if>
                            <c:if test="${!requestScope.is_empty}">
                                <form method="post" action="pvp_page.jsp">
                                    <div class="mb-3">
                                        <label for="boardsize2">Choose the board size:</label>
                                        <input id="boardsize2" type="range" name="${Constants.FORM_INPUT_NAME_BOARD_SIZE}" value="5" min="3" max="15" step="1" oninput="slider()">
                                        <div class="sliderMsg">Size: 5</div>
                                    </div>
                                    <div class="mb-3">
                                        <label for="playerSelector" class="form-label">Choose your opponent:</label>
                                        <select id="playerSelector" class="form-select" name="${Constants.FORM_INPUT_NAME_OPPONENT}"
                                                size="${requestScope.playerIDs.size() - 1}">
                                            <c:forEach var="i" begin="0" end="${requestScope.playerIDs.size() - 1}">
                                                <option value="${requestScope.playerIDs.get(i)}">${requestScope.playerNames.get(i)}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <input type="submit" value="Start" class="btn btn-primary">
                                </form>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
            </div>
        </div>
    </div>
</div>
</body>
</html>
