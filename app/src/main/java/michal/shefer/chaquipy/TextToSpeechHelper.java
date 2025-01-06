package michal.shefer.chaquipy;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import java.util.Locale;

public class TextToSpeechHelper implements TextToSpeech.OnInitListener {

    private final TextToSpeech tts;

    public TextToSpeechHelper(@NonNull Context context) {
        tts = new TextToSpeech(context, this);
    }

    @Override
    // Initialize TextToSpeech and set the language to US English
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
        }
    }

    // Speak the given text
    public void speak(@NonNull String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    // Stop TextToSpeech
    public void stop() {
        tts.stop();
    }

    // Shutdown TextToSpeech
    public void shutdown() {
        tts.shutdown();
    }
}