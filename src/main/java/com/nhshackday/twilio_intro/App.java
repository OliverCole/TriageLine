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
            return SayAndRedirect("Welcome to the chemotherapy tree-arj line. Are you experiencing any chest pain? Push 1 for yes and 2 for no.", "/chestpain").toXml();
        });

        post("/chestpain", (request, response) -> {
        	boolean check = request.queryParams().contains("Digits");
        	if (request.queryParams("Digits").equals("1"))
        	{
        		return GoToANE().toXml();
        	}
        	else if (request.queryParams("Digits").equals("2"))
        	{
            	String resp = "Do you have a fever, or a temperature of over 37.5 celsius? " +
						"Press one for yes and two for no.";
            	return SayAndRedirect(resp, "/fever").toXml();
        	}
        	else {
				return UnrecognisedResponse().toXml();
        	}

        });
        
        post("/fever", (request, response) -> {
        	boolean check = request.queryParams().contains("Digits");
        	if (request.queryParams("Digits").equals("1"))
        	{
        		return GoToANE().toXml();
        	}
        	else if (request.queryParams("Digits").equals("2"))
        	{
            	String resp = "Do you have signs of an infection, such as burning when passing urine, " +
						"coughing up anything or any shivering or shaking? Press one for yes or two for no.";
				return SayAndRedirect(resp, "/signsofinfection").toXml();
        	}
        	else {
				return UnrecognisedResponse().toXml();
        	}

        });
        
        
        
    }
    
    private static VoiceResponse GoToANE()
    {
    	String resp = "If you've had chemotherapy in the last 8 weeks, you could now be at risk of infection. " +
				"Please go for further assessment at your nearest A and E, or call nine-nine-nine if you are unable to travel.";
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

    private static VoiceResponse UnrecognisedResponse()
	{
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
		return voiceResponse;
	}

	private static VoiceResponse SayAndRedirect(String resp, String redirect)
	{
		Say say  = new Say.Builder(
				resp)
				.voice(Voice.WOMAN)
				.build();

		Gather gather = new Gather.Builder()
				.numDigits(1)
				.action("/fever")
				.build();

		VoiceResponse voiceResponse = new VoiceResponse.Builder()
				.say(say)
				.gather(gather)
				.build();
		return voiceResponse;
	}
}