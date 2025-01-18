package libby.pashinsky.chaquopy;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

/**
 * A service that handles text-to-speech functionality.
 * This service initializes the TextToSpeech engine, speaks provided text,
 * and manages the lifecycle of the TTS engine.
 */
public class TextToSpeechService extends Service implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private boolean isInitialized = false;
    private final Queue<String> speechQueue = new LinkedList<>();

    /**
     * Called when the service is first created.
     * Initializes the TextToSpeech engine.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(this, this);
    }

    /**
     * Called when an activity or other component requests the service to start.
     * Retrieves the text to speak from the intent and speaks it if the TTS engine is initialized.
     * If the TTS engine is not initialized, the text is added to a queue to be spoken later.
     * If the intent has the action "STOP_SPEAKING", it stops the current speech.
     *
     * @param intent  The Intent supplied to {@link Context#startService}, as given.
     * @param flags   Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     * @return The return value indicates what semantics the system should use for the service's current started state.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String text = intent.getStringExtra("TEXT_TO_SPEAK");
        if (text != null) {
            if (isInitialized) {
                speak(text);
            } else {
                speechQueue.add(text);
            }
        }
        if (intent.getAction() != null && intent.getAction().equals("STOP_SPEAKING")) {
            stopSpeaking();
        }
        return START_NOT_STICKY;
    }

    /**
     * Called when an activity or other component binds to the service.
     * This service does not support binding, so it returns null.
     *
     * @param intent The Intent that was used to bind to this service.
     * @return null, as this service does not support binding.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not using binding in this example
    }

    /**
     * Called to signal the completion of the TextToSpeech engine initialization.
     * Sets the language to US English if initialization is successful.
     * If there are any queued speech requests, they are processed.
     *
     * @param status {@link TextToSpeech#SUCCESS} or {@link TextToSpeech#ERROR}.
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
            isInitialized = true;
            processSpeechQueue();
        } else {
            Log.e("TextToSpeechService", "Initialization failed");
        }
    }

    /**
     * Speaks the given text using the TextToSpeech engine.
     *
     * @param text The text to speak.
     */
    private void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    /**
     * Processes the speech queue by speaking each queued text.
     */
    private void processSpeechQueue() {
        while (!speechQueue.isEmpty()) {
            speak(speechQueue.remove());
        }
    }

    /**
     * Stops the TextToSpeech engine from speaking.
     */
    private void stopSpeaking() {
        if (tts != null) {
            tts.stop();
        }
    }

    /**
     * Called when the service is being destroyed.
     * Shuts down the TextToSpeech engine to release resources.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.shutdown();
        }
    }

    /**
     * Starts the TextToSpeechService to speak the given text.
     *
     * @param context The application context.
     * @param text    The text to speak.
     */
    public static void startService(Context context, String text) {
        Intent intent = new Intent(context, TextToSpeechService.class);
        intent.putExtra("TEXT_TO_SPEAK", text);
        context.startService(intent);
    }

    /**
     * Stops the TextToSpeechService from speaking.
     *
     * @param context The application context.
     */
    public static void stopSpeaking(Context context) {
        Intent intent = new Intent(context, TextToSpeechService.class);
        intent.setAction("STOP_SPEAKING");
        context.startService(intent);
    }
}