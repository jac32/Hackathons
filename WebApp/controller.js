var socket;
var SERVER_URL = "ws://172.20.10.4:4321";

var W_KEY_CODE = 87
var A_KEY_CODE = 65
var S_KEY_CODE = 83
var D_KEY_CODE = 68

function connect() {
    socket = new WebSocket(SERVER_URL);
}


/*
  TODO: Send command to move forward via socket
*/
function forwardMove(distance) {
    console.log("forwardMove");
    socket.send("forwardMove");
}

$(document).keypress(function(e){
    if (e.which == W_KEY_CODE){
        $("#forwardButton").click();
    }
});

/* 
  TODO: Send command to move backward via socket
*/
function backwardMove(distance) {
    socket.send("backwardMove");
}

$(document).keypress(function(e){
    if (e.which == S_KEY_CODE){
        $("#backwardButton").click();
    }
});

/*
  TODO: Send command to turn left by a number of degrees via socket
  - degrees will be rounded to the nearest 5 
*/
function leftTurn(angle) {
    socket.send("leftTurn");
}

$(document).keypress(function(e){
    if (e.which == A_KEY_CODE){
        $("#leftTurnButton").click();
    }
});

/*
  TODO: Send command to turn right by a number of degrees via socket
  - degrees will be rounded to the nearest 5 
*/
function rightTurn(angle) {
    socket.send("rightTurn");
}

$(document).keypress(function(e){
    if (e.which == D_KEY_CODE){
        $("#rightTurnButton").click();
    }
});
