package libby.pashinsky.chaquopy;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * A helper class for managing text-to-speech functionality.
 * Provides methods for initializing, speaking, stopping, and shutting down the text-to-speech engine.
 */
public class TextToSpeechHelper implements TextToSpeech.OnInitListener {

    private final TextToSpeech tts;

    /**
     * Constructor for the TextToSpeechHelper class.
     * Initializes the text-to-speech engine.
     *
     * @param context The application context.
     */
    public TextToSpeechHelper(@NonNull Context context) {
        tts = new TextToSpeech(context, this);
    }

    /**
     * Called to signal the completion of the TextToSpeech engine initialization.
     * Sets the language to US English if initialization is successful.
     *
     * @param status {@link TextToSpeech#SUCCESS} or {@link TextToSpeech#ERROR}.
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
        }
    }

    /**
     * Speaks the given text using the text-to-speech engine.
     *
     * @param text The text to speak.
     */
    public void speak(@NonNull String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    /**
     * Stops the current speech utterance.
     */
    public void stop() {
        tts.stop();
    }

    /**
     * Shuts down the text-to-speech engine.
     */
    public void shutdown() {
        tts.shutdown();
    }
}