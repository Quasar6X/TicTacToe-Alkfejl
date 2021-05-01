let _moves = [];

let clockInterval; //interval object to run timers
let turnTimerHTML; //Original value
let runTurnTimer = false;
let runGameTimer = false;
let started = false;
let turnTime;
let gameTime;

/**
 * @param {Object} pvaResp - response from the Java Servlet
 * @param {number} pvaResp.state - state of the Game Board - 0 for DRAW, 1 for XWIN, 2 for OWIN, 3 for NONE
 * @param {Object} pvaResp.playerMove - move the player made
 * @param {string} pvaResp.playerMove.tile - where the player moved expressed as "x_y"
 * @param {Object} pvaResp.aiMove - move the AI made
 * @param {string} pvaResp.aiMove.tile - where the AI moved expressed as "x_y"
 */
$(document).ready(function () {
    $(".sendBtn").click(function () {
        if (!started) {
            const tTimerHTML = $("#turnTimerContainer").html();
            if (tTimerHTML !== "00:00:00" && !runTurnTimer) {
                turnTime = moment.duration(tTimerHTML);
                turnTimerHTML = tTimerHTML;
                runTurnTimer = true;
            }

            const gTimerHTML = $("#gameTimerContainer").html();
            if (gTimerHTML !== "00:00:00" && !runGameTimer) {
                gameTime = moment.duration(gTimerHTML);
                runGameTimer = true;
            }
            clockInterval = setInterval(runClocks, 1000);
            started = true;
        }

        if (runTurnTimer) {
            turnTime = moment.duration(turnTimerHTML);
        }

        const form = $(this).closest(".square").closest(".col").find("form");
        const formData = $(form).serialize();
        $.ajax({
            type: 'POST',
            url: "../PvAController",
            data: formData
        }).done(function (pvaResp) {
            _moves.push(pvaResp.playerMove);
            _moves.push(pvaResp.aiMove);
            $("#".concat(pvaResp.playerMove.tile)).html("X");

            if (pvaResp.aiMove) {
                $("#".concat(pvaResp.aiMove.tile)).html("O");
            }

            if (pvaResp.state !== 3) {
                sendEndGameAjax(pvaResp.state);
            }
        });
    });
});

/**
 * @param {number} state - state of the Game Board - 0 for DRAW, 1 for XWIN, 2 for OWIN, 3 for NONE
 */
function sendEndGameAjax(state) {
    runGameTimer = false;
    runTurnTimer = false;
    clearInterval(clockInterval);
    $.ajax({
        type: 'POST',
        url: "../GameEndControllerPvA",
        data: {
            state: state,
            moves: _moves
        },
        dataType: 'json'
    }).done(function (data) {
        window.location.replace(data)
    });
}

function runClocks() {
    if (runTurnTimer) {
        turnTime.subtract(1, 'seconds');
        if (turnTime.asSeconds() < 0) {
            turnTime = moment.duration(turnTimerHTML);
            $.ajax({
               type: 'POST',
               url: "../PvAController",
                data: {
                   skipTurn: 'true'
                },
                dataType: 'json'
            }).done(function (pvaResp) {
                _moves.push(pvaResp.aiMove);
                $("#".concat(pvaResp.aiMove.tile)).html("O");
                if (pvaResp.state !== 3) {
                    sendEndGameAjax(pvaResp.state);
                }
            });
        }
        $("#turnTimerContainer").html(msToTime(turnTime.asMilliseconds()));
    }
    if (runGameTimer) {
        gameTime.subtract(1, 'seconds');
        if (gameTime.asSeconds() < 0) {
            sendEndGameAjax(0);
            return;
        }
        $("#gameTimerContainer").html(msToTime(gameTime.asMilliseconds()));
    }
}

/**
 * @param {number} s - number of milliseconds to convert into time
 * @returns {string} - time in the format of hh:mm:ss
 */
function msToTime(s) {
    // Pad 2 digits
    function pad(n) {
        return ('00' + n).slice(-2);
    }
    const ms = s % 1000;
    s = (s - ms) / 1000;
    const secs = s % 60;
    s = (s - secs) / 60;
    const mins = s % 60;
    const hrs = (s - mins) / 60;

    return pad(hrs) + ':' + pad(mins) + ':' + pad(secs);
}
