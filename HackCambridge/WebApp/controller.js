var socket;
var SERVER_URL = "ws://172.20.4.223:4321";

var W_KEY_CODE = 87
var A_KEY_CODE = 65
var S_KEY_CODE = 83
var D_KEY_CODE = 68

var multiplier = 30;
var angle = 45;

var url = 'http://localhost:8080';




/* 
   Connect to the remote server socket
*/
function connect() {
    console.log("connecting");
    document.getElementById("connection").innerHTML = "connected";
    document.getElementById("connection").style.color = "#B3EE3A";
    socket = new WebSocket(SERVER_URL);
}


function sendCommand() {
    var command = document.getElementById("command")
    var data = {text: command.value};

    socket.send(command.value);
    console.log(command.value);
    if(command.value.split(" ")[0] === "say") {
	$.post(url, data, function(resp){
	    mood = resp.score*2
	    console.log("Response: " + resp.score);
	    sendMoodUpdate(resp.score);
	},'json');
    }
    

   
    recordCommand(command);
    command.innerHTML = "";
}

function sendMoodUpdate(mood) {
    console.log("Mood update: " + (Math.round(mood) + 3));
    socket.send("mood " + (Math.round(mood) + 3));
}

function recordCommand(command) {
    var history = document.getElementById("history");
    history.innerHTML = "<span class='command'>" + formatTime() + command.value + "</span></br>" + history.innerHTML;
}

/*
  Send command to move forward via socket
*/
function forwardMove(distance) {
    console.log("forwardMove " + multiplier);
    socket.send("forward_move " + multiplier);
}

$(document).keypress(function(e){
    if (e.which == W_KEY_CODE){
        $("#forwardButton").click();
    }
});

/* 
   Send command to move backward via socket
*/
function backwardMove(distance) {
    console.log("backwardMove " + multiplier);
    socket.send("backward_move " + multiplier);
}

$(document).keypress(function(e){
    if (e.which == S_KEY_CODE){
        $("#backwardButton").click();
    }
});


function x() {
    forwardMove(50);
    forwardMove(50);
    turnLeft(20);
}

/*
  Send command to turn left by a number of degrees via socket
  - degrees will be rounded to the nearest 5 
*/
function leftTurn(angle) {
    console.log("leftTurn " + angle);
    socket.send("left_turn " + angle);
}

$(document).keypress(function(e){
    if (e.which == A_KEY_CODE){
        $("#leftTurnButton").click();
    }
});

/*
  Send command to turn right by a number of degrees via socket
  - degrees will be rounded to the nearest 5 
*/
function rightTurn(angle) {
    console.log("rightTurn " + angle);
    socket.send("right_turn " + angle);
}

$(document).keypress(function(e){
    if (e.which == D_KEY_CODE){
        $("#rightTurnButton").click();
    }
});


function formatTime() {

    var dt = new Date();

    var hours = dt.getHours();
    var minutes = dt.getMinutes();
    var seconds = dt.getSeconds();

    // the above dt.get...() functions return a single digit
    // so I prepend the zero here when needed
    if (hours < 10) 
     hours = '0' + hours;

    if (minutes < 10) 
     minutes = '0' + minutes;

    if (seconds < 10) 
     seconds = '0' + seconds;

    return "<" + hours + ":" + minutes + ":" + seconds + "> ";
}       

