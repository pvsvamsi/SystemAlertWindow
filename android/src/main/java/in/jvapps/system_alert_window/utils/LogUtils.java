package in.jvapps.system_alert_window.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class LogUtils {

    //private int debugMode = 0;

    private static LogUtils _instance;

    private final String TAG = "SAW:LogUtils";

    private boolean isLogFileEnabled = false;

    private File sawFolder;


    private LogUtils() {
    }

    public static LogUtils getInstance() {
        if (_instance == null) {
            _instance = new LogUtils();
        }
        return _instance;
    }

    public void setLogFileEnabled(boolean logFileEnabled) {
        this.isLogFileEnabled = logFileEnabled;
    }

    public void i(String TAG, String text) {
        Log.i(TAG, text);
        appendLog(text);
    }

    public void d(String TAG, String text) {
        Log.d(TAG, text);
        appendLog(text);
    }

    public void w(String TAG, String text) {
        Log.w(TAG, text);
        appendLog(text);
    }

    public void e(String TAG, String text) {
        Log.e(TAG, text);
        appendLog(text);
    }

    private void appendLog(String text) {
        try {
            Context context = ContextHolder.getApplicationContext();
            if (isLogFileEnabled && context != null) {

                if (sawFolder == null) {
                    Log.d(TAG, "sawFolder is null");
                    sawFolder = new File(context.getApplicationContext().getExternalFilesDir(null), "Logs" + File.separator + "SAW");
                    Log.d(TAG, sawFolder.getAbsolutePath());
                    if (!sawFolder.exists()) {
                        if (!sawFolder.mkdirs()) {
                            Log.e(TAG, "sawFolder to create the SAW directory");
                            sawFolder = null;
                            return;
                        }
                    } else {
                        Log.d(TAG, "sawFolder is already created");
                    }
                }


                Date now = new Date();

                String today = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(now);

                File logFile = new File(sawFolder.getAbsolutePath() + File.separator + today + ".log");

                if (!logFile.exists()) {
                    deleteRecursive(sawFolder);
                    try {
                        if (!logFile.createNewFile()) {
                            Log.e(TAG, "Unable to create the log file");
                            return;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }

                try {
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(now);
                    text = timeStamp + " - " + text;
                    //BufferedWriter for performance, true to set append to file flag
                    BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                    buf.append(text);
                    buf.newLine();
                    buf.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getLogFilePath() {
        if (sawFolder != null) {
            Date now = new Date();

            String today = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(now);

            File logFile = new File(sawFolder.getAbsolutePath() + File.separator + today + ".log");
            if (logFile.exists()) {
                return logFile.getAbsolutePath();
            }
        }
        return null;
    }

    void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory()) {
                for (File child : Objects.requireNonNull(fileOrDirectory.listFiles()))
                    deleteRecursive(child);
            } else {
                //noinspection ResultOfMethodCallIgnored
                fileOrDirectory.delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, ex.toString());
        }
    }
}
