<!DOCTYPE html>
<%--
  Created by dani0.
  Date: 4/25/2021
  Time: 9:04 PM
--%>
<%@ page import="hu.alkfejl.model.Party" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="hu.alkfejl.model.SimpleGameLogic" %>
<%@ page import="hu.alkfejl.constants.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <title>TicTacToe</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link href="../css/font.css" rel="stylesheet" type="text/css">
    <link href="../css/custom.css" rel="stylesheet" type="text/css">
    <link href="../img/logo.png" rel="icon">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- JQuery -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.29.1/moment.min.js"></script> <!-- Moment.js -->
    <script src="../js/user_leaves_page.js"></script>
    <script src="../js/pva_async_message.js"></script>
    <%
        Party party = new Party();
        party.setBoardSize(Integer.parseInt(request.getParameter(Constants.FORM_INPUT_NAME_BOARD_SIZE)));
        party.setTimeOfParty(Timestamp.valueOf(LocalDateTime.now()));
        SimpleGameLogic gamelogic = new SimpleGameLogic();
        gamelogic.setParty(party);
        session.setAttribute(Constants.SESSION_GAME_STATE, gamelogic);
    %>
</head>
<body style="background-color: #476D7C">
<nav class="navbar navbar-light" style="background-color: #1D3E53;">
    <div class="container-fluid px-5">
        <span class="navbar-brand">
                <img src="../img/logo.png" alt="logo" width="200" height="200" style="transform: scale(0.5);">
        </span>
        <span class="navbar-text" style="font-size: 20px; color: #FFC93C">
                Game timer: <span id="gameTimerContainer">${param[Constants.FORM_INPUT_NAME_GAME_TIMER]}</span>
        </span>
        <span class="navbar-text" style="font-size: 20px; color: #FFC93C">
                Turn timer: <span id="turnTimerContainer">${param[Constants.FORM_INPUT_NAME_TURN_TIMER]}</span>
        </span>
    </div>
</nav>
<div class="container mt-5" style="width: 650px; height: 650px;">
    <c:forEach var="i" begin="0" end="${param[Constants.FORM_INPUT_NAME_BOARD_SIZE] - 1}">
        <div class="row" style="height: calc(650px / ${param[Constants.FORM_INPUT_NAME_BOARD_SIZE]});">
            <c:forEach var="j" begin="0" end="${param[Constants.FORM_INPUT_NAME_BOARD_SIZE] - 1}">
                <div class="col" style="padding: 0;">
                    <form>
                        <input type="hidden" name="${Constants.FORM_INPUT_NAME_COORDINATES}" value="${i}_${j}">
                    </form>
                    <div class="square">
                        <c:if test="${i == 0}">
                            <c:choose>
                                <c:when test="${j == 0}">
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${param[Constants.FORM_INPUT_NAME_BOARD_SIZE]});
                                                    border: 2px solid #1D3E53;
                                                    border-top: 4px solid #1D3E53;
                                                    border-left: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:when>
                                <c:when test="${j == param[Constants.FORM_INPUT_NAME_BOARD_SIZE] - 1}">
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${param[Constants.FORM_INPUT_NAME_BOARD_SIZE]});
                                                    border: 2px solid #1D3E53;
                                                    border-top: 4px solid #1D3E53;
                                                    border-right: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${param[Constants.FORM_INPUT_NAME_BOARD_SIZE]});
                                                    border: 2px solid #1D3E53;
                                                    border-top: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <c:if test="${i == param[Constants.FORM_INPUT_NAME_BOARD_SIZE] - 1}">
                            <c:choose>
                                <c:when test="${j == 0}">
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${param[Constants.FORM_INPUT_NAME_BOARD_SIZE]});
                                                    border: 2px solid #1D3E53;
                                                    border-bottom: 4px solid #1D3E53;
                                                    border-left: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:when>
                                <c:when test="${j == param[Constants.FORM_INPUT_NAME_BOARD_SIZE] - 1}">
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${param[Constants.FORM_INPUT_NAME_BOARD_SIZE]});
                                                    border: 2px solid #1D3E53;
                                                    border-bottom: 4px solid #1D3E53;
                                                    border-right: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${param[Constants.FORM_INPUT_NAME_BOARD_SIZE]});
                                                    border: 2px solid #1D3E53;
                                                    border-bottom: 4px solid #1D3E53;
                                                    color: #77ABB7;
                                                    background-color: #254B62">
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <c:choose>
                            <c:when test="${j == 0 && i != 0 && i != param[Constants.FORM_INPUT_NAME_BOARD_SIZE] - 1}">
                                <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                        style="height: calc(650px / ${param[Constants.FORM_INPUT_NAME_BOARD_SIZE]});
                                                border: 2px solid #1D3E53;
                                                border-left: 4px solid #1D3E53;
                                                color: #77ABB7;
                                                background-color: #254B62">
                                </button>
                            </c:when>
                            <c:when test="${j == param[Constants.FORM_INPUT_NAME_BOARD_SIZE] - 1 && i != 0 && i != param[Constants.FORM_INPUT_NAME_BOARD_SIZE] - 1}">
                                <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                        style="height: calc(650px / ${param[Constants.FORM_INPUT_NAME_BOARD_SIZE]});
                                                border: 2px solid #1D3E53;
                                                border-right: 4px solid #1D3E53;
                                                color: #77ABB7;
                                                background-color: #254B62">
                                </button>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${j != param[Constants.FORM_INPUT_NAME_BOARD_SIZE] - 1 && i != param[Constants.FORM_INPUT_NAME_BOARD_SIZE] - 1}">
                                    <button class="btn sendBtn w-100" value="sendBtn" id="${i}_${j}"
                                            style="height: calc(650px / ${param[Constants.FORM_INPUT_NAME_BOARD_SIZE]});
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
</div>
</body>
</html>
