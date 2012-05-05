jQuery(document).ready(function($){

    // Show the awaiting image sequence
    $('#avatar').jsMovie({
        sequence: 'nico_blink####.png',
        from    : 1,
        to      : 120,
        folder  : "images/animation/blink/",
        height  : 116, width: 100,
        playOnLoad    : true,
        showPreLoader : false,
        repeat        : true    
    });


    // Set-up variables
    var req = new XMLHttpRequest(); // for getting the bot response
//    req.onreadystatechange = stateChangeHandler;  // monitoring // only lastone is permitted
    req.onreadystatechange = checkenable;
    req.onload = handler;                         // callback
    req.onerror = printerror;

    var first_ballon_to_show = 0;   // cache display state (improve performance)
    var need2show_nav = false;      // this is false, until overflow happens
    var send_enabled = true; // this enables/disables sending information (if waiting for response)


    $('form#userinput input[name=q]').focus();  // set focus on main input


    // Redefine submit behavior
    $('form#userinput').submit(function(){

        // check if there is request in progress
        if (req.readyState != 0 && req.readyState != 4) { return false; }

        var $this = $(this); // the form
        var userAgent =  $this.children('input[name=userAgent]').val();
        var $q = $this.children('input[name=q]');
        var q =  $q.val();

        if (q == "") { return false; }

        placeBalloon('user_balloon', q); // question balloon
        placeBalloon('load_bot_balloon', '<img src="https://www.google.com/tools/dlpage/res/chrome/images/chrome_throbber_fast_16.gif" />');

        req.open( // call the bot
            "GET",
            "http://localhost:8000/qasystem?q=" + q + "&userAgent=" + userAgent + "&type=json",
            true); // asynchronous
        req.send(null);

//        // Form data does not work with get
//        var sneData = new FormData(this); // the form
//        req.send(sendData);

        $q.val(''); // clear input
        $q.focus(); // set focus

        return false; // avoid default
    });


    // Hide/display navigation bar
    $("#dialog").mouseenter(function(event) {
        if(need2show_nav){
            $("#navbar").slideDown(75);
        }
    }).mouseleave(function(event) {
        $("#navbar").slideUp(100);
    });


    // Prev button click behavior
    $("#prev").click(function() {
        first_ballon_to_show = first_ballon_to_show - 1;  // update display cache
        if(first_ballon_to_show < 0){
            first_ballon_to_show = 0; // top reached
        }

        // set the scroll target
        var $gotoballon = $('#dialog table:eq(' + first_ballon_to_show + ')');

        // nice scroll
        $("#dialog").scrollTo($gotoballon, 100);

        // focus
        $('form#userinput input[name=q]').focus();
    });



    // Next button click behaior
    $("#next").click(function() {
        // calculate display state (cannot use cahe)
        var num_ballons = $('#dialog table').size();
        checkBallonStates(); // update state

        // set the scroll target
        var $gotoballon = $('#dialog table:eq(' + (first_ballon_to_show+1) + ')');

        // nice scroll
        $("#dialog").scrollTo($gotoballon, 100);

        // focus
        $('form#userinput input[name=q]').focus();
    });



    // Update state after scrolling
    $("#dialog").bind("scroll", checkBallonStates );

    // funtion called after geting the information from the request
    function handler() {
        // get response content
        // var resp = req.responseXML.getElementById("response").textContent;
        var resp = req.responseText;
        try{
            var json = JSON.parse(resp);
            resp = pickIcon(json);
            resp += jsonPrettyPrint(json);
        } catch(SystaxException){
            // it is not a valid json object, treat it as plain text
        }
        // make and place the balloon
        placeBalloon('bot_balloon', resp);
    }

    /**
     * Function called to place a bellon in the ballon list.
     * It also performs the automatic scroll
     * First param is the id of the balloon. Only two values are accepted:
     * - user_balloon
     * - bot_balloon
     * The second is the content. If it is html it will be evaluated as code.
     */
    function placeBalloon(balloon_type, balloon_content) {

        // Create the balloon from the content
        var balloon = constructBalloon (balloon_type, balloon_content);

        // Remove loading balloon
        $('.load_bot_balloon').remove();

        // place balloons
        $('#padding').before($(balloon));

        // Nice scroll to last balloon
        //var $last = $('#dialog .bot_balloon:last');
        var $last = $('#dialog table:last');
        var max_height = $('#dialog').height() - $('#padding').height();
        if ($last.height() > max_height){
            $("#dialog").scrollTo($last);
        }
        else{
            $("#dialog").scrollTo('100%', 0);  
        }

        // check it overflow happened
        if(!need2show_nav){
            var balloons = $('#dialog table');
            var accHeight = 0;
            for(var i = 0; i < balloons.size(); i++){
                accHeight = accHeight + $(balloons[i]).height();
            }
            need2show_nav = accHeight > $('#dialog').height();
        }

        // updates the state of the display
        checkBallonStates();
    }

    // This updates the state of the display, calculating the first ballon that is out of view 
    function checkBallonStates () {
        var ballons = $('#dialog table');
        var index = ballons.size();
        for (; index--; index >= 0){
            if( $(ballons[index]).offset().top < 0){
                first_ballon_to_show = index + 1;
                break;
            }
        }
    }


    // This constructs balloon html structure
    function constructBalloon (id, content) {
        return '<table class="' + id + '">\
                    <tr><td class="topleft"></td><td class="topright"></td></tr>\
                    <tr><td class="bottomleft"></td><td class="bottomright">'+ content +'</td></tr>\
                </table>';
    }

    //TODO: implement this
    function pickIcon(json) {
        //var root = json;
        return '<img class="icon" src="http://cdn1.iconfinder.com/data/icons/dot/128/suitcase_travel.png" />';
    }    

    // This constructs html nested lists from javascript/json object
    // TODO: clean up generated html
    function jsonPrettyPrint(json) {
        var string = "<ul>";
        for (var property in json) {
            var object = json[property];
            if (json instanceof Array) {
                string += jsonPrettyPrint(object);
            } else if (object instanceof Object) {
                string += "<b>"+ property +"</b>" + jsonPrettyPrint(object);
            }
            else{
                string += "<li><b>"+ property +": </b>"+ object +"</li>";
            }
        }
        string += "</ul>";
        return string;
    }

    // This shows an error message in the display
    function printerror() {
        $('.load_bot_balloon').remove();
        $('#msg_dialog').text("Ocurrió un error al conectar con el servidor");
        $('#msg_dialog').fadeIn().delay(3000).fadeOut('slow');
    }

    // enable/disable submit button depending on the httpreq status
    function checkenable() {
        switch(req.readyState){
            case 1: case 2: case 3:
              $('form#userinput input[type=submit]').attr('disabled','disabled');
              break; 
            case 0: case 4: 
              $('form#userinput input[type=submit]').removeAttr('disabled');
              break; 
        }
    }

    // debugging purposes only
    function stateChangeHandler() {
        switch(req.readyState){
            case 0:
              //$('#msg_dialog').text('0: request not initialized ');
              console.log('XMLHttpRequest status changed: 0 - request not initialized ');
              break; 
            case 1:
              //$('#msg_dialog').text('1: server connection established');
              console.log('XMLHttpRequest status changed: 1 - server connection established');
              break; 
            case 2:
              //$('#msg_dialog').text('2: request received ');
              console.log('XMLHttpRequest status changed: 2 - request recieved');
              break; 
            case 3:
              //$('#msg_dialog').text('3: processing request');
              console.log('XMLHttpRequest status changed: 3 - processing request');
              break; 
            case 4: 
              //$('#msg_dialog').text('4: request finished and response is ready');
              console.log('XMLHttpRequest status changed: 4 - request finished and response is ready');
              break; 
        }
    }
    
});

