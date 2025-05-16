package com.springai;

import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.ai.openai.metadata.audio.OpenAiAudioSpeechResponseMetadata;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.Voice;

public class OpenAiAudio {
    private String text;
    private String voice;
    private float speed;

    public OpenAiAudio(String text, String voice, float speed){
        this.text = text;
        this.voice = voice;
        this.speed = speed;
    }

    public byte[] createAudio()
    {
        OpenAiAudioApi openAiAudioApi = new OpenAiAudioApi("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");

        OpenAiAudioSpeechModel openAiAudioSpeechModel = new OpenAiAudioSpeechModel(openAiAudioApi);

        Voice voiceEnum= Voice.valueOf(voice.toUpperCase());
        

        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
            .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
            .voice(voiceEnum)
            .speed(speed)
            .model(OpenAiAudioApi.TtsModel.TTS_1.value)
            .build();

            SpeechPrompt speechPrompt = new SpeechPrompt(text, speechOptions);
        SpeechResponse response = openAiAudioSpeechModel.call(speechPrompt);

        // Accessing metadata (rate limit info)
        OpenAiAudioSpeechResponseMetadata metadata = response.getMetadata();
        return response.getResult().getOutput();
    }

    
}
