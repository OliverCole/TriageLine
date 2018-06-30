package com.nhshackday.twilio_intro;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
import com.twilio.twiml.voice.Say.Voice;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Redirect;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {

        get("/hello", (req, res) -> "Hello Web");

        post("/", (request, response) -> {
            Say say  = new Say.Builder(
                    "Welcome to the chemotherapy triage line. Are you experiencing any chest pain? Push 1 for yes and 2 for no.")
            		.voice(Voice.WOMAN)
                    .build();
            Gather gather = new Gather.Builder()
            		.numDigits(1)
            		.action("/g")
            		.build();
            
            VoiceResponse voiceResponse = new VoiceResponse.Builder()
                    .say(say)
                    .gather(gather)
                    .build();
            return voiceResponse.toXml();
        });

        post("/g", (request, response) -> {
        	boolean check = request.queryParams().contains("Digits");
        	if (request.queryParams("Digits").equals("1"))
        	{
        		return GoToANE().toXml();
        	}
        	else if (request.queryParams("Digits").equals("2"))
        	{
            	String resp = "Do you have a fever?";
                Say say  = new Say.Builder(
                        resp)
                		.voice(Voice.WOMAN)
                        .build();
                
                VoiceResponse voiceResponse = new VoiceResponse.Builder()
                        .say(say)
                        .hangup(new Hangup.Builder().build())
                        .build();
                return voiceResponse.toXml();
        	}
        	else {
            	String resp = "Unrecognised response.";
                Say say  = new Say.Builder(
                        resp)
                		.voice(Voice.WOMAN)
                        .build();
                
                Redirect redir = new Redirect.Builder("/").build();
                
                VoiceResponse voiceResponse = new VoiceResponse.Builder()
                        .say(say)
                        .redirect(redir)
                        .build();
                return voiceResponse.toXml();
        	}

        });
        
        
        
    }
    
    private static VoiceResponse GoToANE()
    {
    	String resp = "Please go to A and E";
        Say say  = new Say.Builder(
                resp)
        		.voice(Voice.WOMAN)
                .build();
        
        VoiceResponse voiceResponse = new VoiceResponse.Builder()
                .say(say)
                .hangup(new Hangup.Builder().build())
                .build();
        
        return voiceResponse;
    }
}