<!DOCTYPE html>
<%--
  Created by dani0.
  Date: 4/25/2021
  Time: 9:04 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="hu.alkfejl.constants.Constants" %>
<%@ page import="hu.alkfejl.listener.OnlineUserListener" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="hu.alkfejl.model.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <title>TicTacToe</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link href="../css/font.css" rel="stylesheet" type="text/css">
    <link href="../css/custom.css" rel="stylesheet" type="text/css">
    <link href="../img/logo.png" rel="icon">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="../js/user_leaves_page.js"></script>
    <script src="../js/pvp_async_message.js"></script>

    <%
        // doService method
        if (request.getParameter(Constants.FORM_INPUT_NAME_OPPONENT) != null) {
            int opponentPlayerID = Integer.parseInt(request.getParameter(Constants.FORM_INPUT_NAME_OPPONENT));
            OnlineUserListener listener = (OnlineUserListener) session.getServletContext().getAttribute(OnlineUserListener.KEY);
            HttpSession opponentsSession = listener.findSessionFromPlayerID(opponentPlayerID);
            opponentsSession.setAttribute(Constants.SESSION_OPPONENT_PLAYER_ID, session.getAttribute(Constants.SESSION_PLAYER_ID)); // Set my id in their session
            opponentsSession.setAttribute(Constants.SESSION_OPPONENT_PLAYER_NAME, session.getAttribute(Constants.SESSION_PLAYER_NAME));
            opponentsSession.setAttribute(Constants.SESSION_BOARD_SIZE, request.getParameter(Constants.FORM_INPUT_NAME_BOARD_SIZE)); // Need to know this to draw the game board
            opponentsSession.setAttribute(Constants.SESSION_PLAYER_SYMBOL, Player.Symbol.O);

            session.setAttribute(Constants.SESSION_OPPONENT_PLAYER_ID, opponentPlayerID); // Set their id in my session
            session.setAttribute(Constants.SESSION_OPPONENT_PLAYER_NAME, listener.onlinePlayers().stream().filter(playerHuman -> playerHuman.getId() == opponentPlayerID).findFirst().orElse(new PlayerHuman()).getName());
            session.setAttribute(Constants.SESSION_PLAYER_SYMBOL, Player.Symbol.X);

            Party party = new Party();
            party.setBoardSize(Integer.parseInt(request.getParameter(Constants.FORM_INPUT_NAME_BOARD_SIZE)));
            party.setTimeOfParty(Timestamp.valueOf(LocalDateTime.now()));
            SimpleGameLogic gameLogic = new SimpleGameLogic();
            gameLogic.setParty(party);
            session.setAttribute(Constants.SESSION_GAME_STATE, gameLogic); // GameLogic Object lives ONLY IN PLAYER X'S session
            session.setAttribute(Constants.SESSION_MOVE_LIST, new ArrayList<Move>()); // Move list lives ONLY IN PLAYER X'S session
        }
    %>
</head>
<body style="background-color: #476D7C">
<nav class="navbar navbar-light" style="background-color: #1D3E53">
    <div class="container-fluid px-5">
        <span class="navbar-brand">
            <img src="../img/logo.png" alt="logo" width="200" height="200" style="transform: scale(0.5);">
        </span>
        <span class="navbar-text" style="font-size: 20px; color: #FFC93C">
            Player X:
            <c:choose>
                <c:when test="${sessionScope.get(Constants.SESSION_PLAYER_SYMBOL).ordinal() == 0}">
                    <span> ${sessionScope.get(Constants.SESSION_PLAYER_NAME)}</span>
                </c:when>
                <c:otherwise>
                    <span> ${sessionScope.get(Constants.SESSION_OPPONENT_PLAYER_NAME)}</span>
                </c:otherwise>
            </c:choose>
        </span>
        <span class="navbar-text" style="font-size: 20px; color: #FFC93C">
            Player O:
            <c:choose>
                <c:when test="${sessionScope.get(Constants.SESSION_PLAYER_SYMBOL).ordinal() == 1}">
                    <span> ${sessionScope.get(Constants.SESSION_PLAYER_NAME)}</span>
                </c:when>
                <c:otherwise>
                    <span> ${sessionScope.get(Constants.SESSION_OPPONENT_PLAYER_NAME)}</span>
                </c:otherwise>
            </c:choose>
        </span>
        <span class="navbar-text" style="font-size: 20px; color: #FFC93C">
            Turn: <span id="turn">X</span>
        </span>
    </div>
</nav>
<div class="container mt-5" style="width: 650px; height: 650px">
    <c:choose>
        <c:when test="${param[Constants.FORM_INPUT_NAME_BOARD_SIZE] > 0}">
            <c:set var="size" value="${param[Constants.FORM_INPUT_NAME_BOARD_SIZE] - 1}" scope="page"/>
        </c:when>
        <c:otherwise>
            <c:set var="size" value="${sessionScope.get(Constants.FORM_INPUT_NAME_BOARD_SIZE) - 1}" scope="page"/>
        </c:otherwise>
    </c:choose>
    <c:forEach var="i" begin="0" end="${size}">
        <div class="row" style="height: calc(650px / ${size + 1});">
            <c:forEach var="j" begin="0" end="${size}">
                <div class="col" style="padding: 0;">
                    <form>
                        <input type="hidden" name="${Constants.FORM_INPUT_NAME_COORDINATES}" value="${i}_${j}">
                    </form>
                    <div class="square">
                        <c:if test="${i == 0}">
                            <c:choose>
                                <c:when test="${j == 0}">
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${size + 1});
                                                    border: 2px solid #1D3E53;
                                                    border-top: 4px solid #1D3E53;
                                                    border-left: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:when>
                                <c:when test="${j == size}">
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${size + 1});
                                                    border: 2px solid #1D3E53;
                                                    border-top: 4px solid #1D3E53;
                                                    border-right: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${size + 1});
                                                    border: 2px solid #1D3E53;
                                                    border-top: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <c:if test="${i == size}">
                            <c:choose>
                                <c:when test="${j == 0}">
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${size + 1});
                                                    border: 2px solid #1D3E53;
                                                    border-bottom: 4px solid #1D3E53;
                                                    border-left: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:when>
                                <c:when test="${j == size}">
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${size + 1});
                                                    border: 2px solid #1D3E53;
                                                    border-bottom: 4px solid #1D3E53;
                                                    border-right: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${size + 1});
                                                    border: 2px solid #1D3E53;
                                                    border-bottom: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <c:choose>
                            <c:when test="${j == 0 && i != 0 && i != size}">
                                <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                        style="height: calc(650px / ${size + 1});
                                                border: 2px solid #1D3E53;
                                                border-left: 4px solid #1D3E53;
                                                color: #77ABB7;
                                                background-color: #254B62">
                                </button>
                            </c:when>
                            <c:when test="${j == size && i != 0 && i != size}">
                                <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                        style="height: calc(650px / ${size + 1});
                                                border: 2px solid #1D3E53;
                                                border-right: 4px solid #1D3E53;
                                                color: #77ABB7;
                                                background-color: #254B62">
                                </button>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${j != size && i != size}">
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${size + 1});
                                                    border: 2px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:forEach>
    <c:remove var="size"/>
</div>
</body>
</html>
