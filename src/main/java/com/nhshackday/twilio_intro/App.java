package com.nhshackday.twilio_intro;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.*;
import com.twilio.twiml.voice.Say.Voice;
import com.twilio.type.PhoneNumber;

import java.net.URI;

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


		// Yes, Do you feel like shit? Yes, come in, no, go to 5
		post("/signsofinfection", (request, response) -> {
			boolean check = request.queryParams().contains("Digits");
			if (request.queryParams("Digits").equals("1"))
			{
				String resp = "Do you feel like shit? Press one for yes, and two for no.";
				return SayAndRedirect(resp, "/feelbad").toXml();
			}
			else if (request.queryParams("Digits").equals("2"))
			{
				return FirstFive().toXml();
			}
			else {
				return UnrecognisedResponse().toXml();
			}
		});

		post("/feelbad", (request, response) -> {
			boolean check = request.queryParams().contains("Digits");
			if (request.queryParams("Digits").equals("1"))
			{
				return SpeakProfessional().toXml();
			}
			else if (request.queryParams("Digits").equals("2"))
			{
				return FirstFive().toXml();
			}
			else {
				return UnrecognisedResponse().toXml();
			}
		});

		post("/firstfive", (request, response) -> {
			boolean check = request.queryParams().contains("Digits");
			if (request.queryParams("Digits").equals("1") ||
				request.queryParams("Digits").equals("2") ||
				request.queryParams("Digits").equals("3") ||
				request.queryParams("Digits").equals("4") ||
				request.queryParams("Digits").equals("5"))
			{
				return SpeakProfessional().toXml();
			}
			else if (request.queryParams("Digits").equals("6"))
			{
				return FirstFive().toXml();
			}
			else {
				return UnrecognisedResponse().toXml();
			}
		});

		post("/speakprofessional", (request, response) -> {
			boolean check = request.queryParams().contains("Digits");
			if (request.queryParams("Digits").equals("1"))
			{
				String resp = "Transferring you.";
				Say say  = new Say.Builder(
						resp)
						.voice(Voice.WOMAN)
						.build();

				VoiceResponse voiceResponse = new VoiceResponse.Builder()
						.say(say)
						.dial(new Dial.Builder()
								.conference(new Conference.Builder("demo").build())
								.build())
						.build();

				TwilioRestClient client = new TwilioRestClient
						.Builder(System.getenv("TWILIO_SID"), System.getenv("TWILIO_TOKEN"))
						.build();

				Call call = Call.creator(new PhoneNumber(System.getenv("TRANSFER")),
						new PhoneNumber(System.getenv("NUMBER")),
						URI.create(System.getenv("BASE") + "/professional")).create(client);


				String re = voiceResponse.toXml();
				return re;
			}
			else if (request.queryParams("Digits").equals("2"))
			{
				return SayAndHangup("Thank you. A medical professional will call you back at this number as soon as possible. Goodbye!").toXml();
			}
			else {
				return UnrecognisedResponse().toXml();
			}
		});


		post("/professional", (request, response) -> {


				VoiceResponse voiceResponse = new VoiceResponse.Builder()
						.dial(new Dial.Builder()
								.conference(new Conference.Builder("demo").build())
								.build())
						.build();

				String re = voiceResponse.toXml();
				return re;

		});
    }

    private static VoiceResponse GoToANE()
    {
    	String resp = "If you've had chemotherapy in the last 8 weeks, you could now be at risk of infection. " +
				"Please go for further assessment at your nearest A and E, or call nine-nine-nine if you are unable to travel.";
        return SayAndHangup(resp);
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

	private static VoiceResponse SayAndHangup(String resp)
	{
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

	private static VoiceResponse SayAndRedirect(String resp, String redirect)
	{
		Say say  = new Say.Builder(
				resp)
				.voice(Voice.WOMAN)
				.build();

		Gather gather = new Gather.Builder()
				.numDigits(1)
				.action(redirect)
				.build();

		VoiceResponse voiceResponse = new VoiceResponse.Builder()
				.say(say)
				.gather(gather)
				.build();
		return voiceResponse;
	}

	private static VoiceResponse SpeakProfessional()
	{
		Say say  = new Say.Builder(
				"You need to speak with someone about your symptoms. We can transfer you, or store your " +
						"phone number and someone will call you as soon as possible. Press one to transfer now, or two for a callback.")
				.voice(Voice.WOMAN)
				.build();

		Gather gather = new Gather.Builder()
				.numDigits(1)
				.action("/speakprofessional")
				.build();

		VoiceResponse voiceResponse = new VoiceResponse.Builder()
				.say(say)
				.gather(gather)
				.build();
		return voiceResponse;
	}

	private static VoiceResponse FirstFive()
	{
		String resp = "Do you have any of the following five symptoms? Press one for shortness of breath, " +
				"two for a rash, three for bleeding, four for mobility problems or numbness, five for urinary " +
				"problems. Press six for none of these.";
		return SayAndRedirect(resp, "/firstfive");
	}
}