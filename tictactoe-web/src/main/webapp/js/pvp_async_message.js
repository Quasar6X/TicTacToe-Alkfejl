let refreshInterval;

/**
 * @param {Object} pvpResp - response from the Java Servlet
 * @param {Object} pvpResp.playerMove - move that on of the players made
 * @param {Object} pvpResp.playerMove.tile - where one of the players moved expressed as "x_y"
 * @param {number} pvpResp.state - state of the Game Board - 0 for DRAW, 1 for XWIN, 2 for OWIN, 3 for NONE
 * @param {boolean} pvpResp.valid - signals if the move was succesful
 * @param {number} pvpResp.symbol - 0 for X, 1 for O
 */
$(document).ready(function () {
    $(".sendBtn").click(function () {
        const form = $(this).closest(".square").closest(".col").find("form");
        const formData = $(form).serialize();
        $.ajax({
            type: 'POST',
            url: '../PvPController',
            data: formData
        }).done(function (pvpResp) {
            if (pvpResp.valid === true) {
                if (pvpResp.symbol === 0) {
                    $("#".concat(pvpResp.playerMove.tile)).html("X");
                } else {
                    $("#".concat(pvpResp.playerMove.tile)).html("O");
                }

                if (pvpResp.state !== 3) {
                    sendEndGameAjax(pvpResp.state, "true");
                }
            }
        });
    });
    refreshInterval = setInterval(checkBoard, 500);
});

document.addEventListener("visibilitychange", function () {
    if (document.visibilityState === 'hidden') {
        clearInterval(refreshInterval);
    } else {
        window.location.replace("../index.jsp");
    }
});

/**
 * @param {number} state - state of the Game Board - 0 for DRAW, 1 for XWIN, 2 for OWIN, 3 for NONE
 * @param {string} save - save the replay or not
 */
function sendEndGameAjax(state, save) {
    $.ajax({
        type: 'POST',
        url: "../GameEndControllerPvP",
        data: {
            state: state,
            save: save
        },
        dataType: 'json'
    }).done(function (resp) {
        window.location.replace(resp);
    });
}

function checkBoard() {
    $.ajax({
        type: 'POST',
        url: "../PvPController",
        data: {
            sendMoves: "true"
        },
        dataType: 'json'
    }).done(function (moveListResp) {
        $("#turn").html(moveListResp.turn);

        if (moveListResp.otherPlayerLeft === true) {
            sendEndGameAjax(moveListResp.state, "false");
            return;
        }

        for (let i = 0; i < moveListResp.moves.length; i++) {
            $("#".concat(moveListResp.moves[i].tile)).html(moveListResp.symbols[i]);
        }

        if (moveListResp.state !== 3) {
            sendEndGameAjax(moveListResp.state);
        }
    });
}
