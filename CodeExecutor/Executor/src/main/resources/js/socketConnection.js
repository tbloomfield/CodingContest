 const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8081/ws'
});
 
 stompClient.onConnect = (frame) => {   
   var subscription = stompClient.subscribe('/user/queue/', function (dequeueMessage) {
     serverMessage("token=" + JSON.parse(dequeueMessage.body).jwtToken);
   } );
    $( "#disconnect" ).prop('disabled',false);
    $( "#connect" ).prop('disabled',true);
    $( "#enqueue" ).prop('disabled',false);    
        
    serverMessage("Connected"); 
}

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function connect() {   
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    serverMessage("Disconnected");
    $( "#disconnect" ).prop('disabled',true);
    $( "#enqueue" ).prop('disabled',true);
    $( "#connect" ).prop('disabled',false);
}

function enqueue() {
 stompClient.publish({ 
 	destination: "/virtualQueue/queue/enqueue"
 });
}

function serverMessage(message){ 
    $("#serverMessages").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#enqueue" ).click(() => enqueue());
    $( "#disconnect" ).click(() => disconnect());   
});

