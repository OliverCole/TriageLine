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


		port(Integer.parseInt(System.getenv("PORT")));

        get("/hello", (req, res) -> "Hello Web");

        post("/", (request, response) -> {
            return SayAndRedirect("Welcome to the chemotherapy line. Are you experiencing any chest pain? Push 1 for yes and 2 for no.", "/chestpain").toXml();
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
				return SecondFive().toXml();
			}
			else {
				return UnrecognisedResponse().toXml();
			}
		});

		post("/secondfive", (request, response) -> {
			boolean check = request.queryParams().contains("Digits");
			if (request.queryParams("Digits").equals("1"))
			{
				return Diarrhoea().toXml();
			}
			else if (request.queryParams("Digits").equals("2"))
			{
				return SayAndHangup("Ensure you are taking your anti-sickness drugs regularly and are drinking plenty of fluids. Thank you for calling.").toXml();
			}
			else if (request.queryParams("Digits").equals("3"))
			{
				return SayAndHangup("Increase fluid intake and take a laxative if you've been prescribed one. Thank you for calling.").toXml();
			}
			else if (request.queryParams("Digits").equals("4"))
			{
				return SayAndHangup("Use mouthwash as directed. Drink plenty of fluids. Use painkillers either as a tablet or a mouthwash. Thank you for calling.").toXml();
			}
			else if (request.queryParams("Digits").equals("5"))
			{
				return SayAndHangup("Take regular analgesia as prescribed. If this you feel this is insufficient please call for advice. Thank you for calling.").toXml();
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

		post("/diarrhoea", (request, response) -> {
			boolean check = request.queryParams().contains("Digits");
			if (request.queryParams("Digits").equals("1"))
			{
				return SpeakProfessional().toXml();
			}
			else if (request.queryParams("Digits").equals("2"))
			{
				return SayAndHangup("Drink plenty of fluids. If you've previously been advised to take antidiarrhoeal medication please do so.");
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

	private static VoiceResponse Diarrhoea() {
    	return SayAndRedirect("Have you had more than 6 episodes in the last 24 hours? Press one for yes or two for no.", "/diarrhoea");
	}

	private static VoiceResponse FirstFive()
	{
		String resp = "Do you have any of the following five symptoms? Press one for shortness of breath, " +
				"two for a rash, three for bleeding, four for mobility problems or numbness, or five for urinary " +
				"problems. Press six for none of these.";
		return SayAndRedirect(resp, "/firstfive");
	}

	private static VoiceResponse SecondFive()
	{
		String resp = "Do you have any of the following six symptoms? Press one for diarrhoea, two for nausea or " +
				"vomiting, three for constipation, four for ulcers or five for pain. Press six for none of these.";
		return SayAndRedirect(resp, "/secondfive");
	}

	private static VoiceResponse End()
	{
		return null;
	}
}