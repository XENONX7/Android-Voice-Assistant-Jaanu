package com.example.jaanu7;
import android.Manifest;
import android.annotation.SuppressLint;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.EditText;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;




public class MainActivity extends AppCompatActivity implements OnInitListener {



    @SuppressLint("StaticFieldLeak")
    private static MainActivity inst;
    public TextView voiceInput, ST;
    MediaPlayer mp = new MediaPlayer();
    String ContShare, Sip, ContSend ,SPT, SERVER_IP, name, phonenumber, MESSAGE, OTP;
    EditText  SIP;
    WebView SWV;
    private TextToSpeech TTS;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    public WebView wv1,wvupi;
    private static final int REQUEST_ENABLE_BT = 0;
    Uri uri;
    Cursor cursor ;
    int SERVER_PORT;
    ArrayList<String> NUM= new ArrayList<String>();
    ArrayList<String> NAME= new ArrayList<String>();
    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        uri = i.getParcelableExtra("uri");
        TTS = new TextToSpeech(this, this);
        askSpeechInput();
        voiceInput = (TextView) findViewById(R.id.DT);
        ST = (TextView) findViewById(R.id.AV);

        Button speakButton = (Button) findViewById(R.id.SB);
        speakButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askSpeechInput();
            }
        });


        CONTACT_UPDATER();



    }


    //##
    //CONTACT UPDATER ,Updates contact list whenever app is opened.
    public void CONTACT_UPDATER()
    {
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        while (true) {
            assert cursor != null;
            if (!cursor.moveToNext()) break;
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            NAME.add(name.toLowerCase());
            NUM.add(phonenumber);
        }
        cursor.close();
    }
    //##

    //##
    //GOOGLE VOICE API
    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "SPEAK BABY");
        try {

            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        }
        catch (ActivityNotFoundException a)
        {

        }
    }

    //##

    @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                assert result != null;
                SPT = result.get(0);


            }
        }

            //##

        voiceInput.setText(SPT);
        String SPTD = SPT.toLowerCase();//IN VOICE OUTPUT IN LOWERCASE




        if (SPTD.startsWith("call")) {
            String CALL = SPTD.substring(5).trim();
            String NUMBER = "";
            int namei = NAME.indexOf(CALL);
            if (namei != -1) {
                NUMBER = NUM.get(namei);
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + NUMBER));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                    Toast.makeText(getBaseContext(), "Calling: " + CALL, Toast.LENGTH_LONG).show();
                    speakOut("Ok sweetheart, calling " + CALL);
                }
            }
        }





        if (SPT.startsWith("message") || SPT.startsWith("sms") || SPT.startsWith("text")) {

            String[] cont = SPTD.split(" ");
            List<String> Cont = new ArrayList<String>(Arrays.asList(cont));
            List<String> mess = new ArrayList<String>();
            List<String> num = new ArrayList<String>();
            Cont.remove(0);
            int ti = Cont.indexOf("that");
            int soc = Cont.size();
            for (int i = ti + 1; i < soc; ++i) {
                String msp = String.join("", Cont.get(i));
                mess.add(msp);
            }
            String MSG = String.join(" ", mess);


            for (int i = 0; i <= ti - 1; ++i) {
                String COnt = String.join("", Cont.get(i));
                num.add(COnt);
            }
            String NUMB = String.join(" ", num);


            String no = NUMB;
            String msg = MSG;


                int namei =0;
                String NUMBER="";
                if(NAME.contains(no))
                {
                    namei=NAME.indexOf(no);
                    NUMBER=NUM.get(namei);
                }

                        try {


                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(NUMBER, null, msg, pi, null);
                        }
                        catch (Exception ignored) {
                            throw new RuntimeException(ignored);
                        }

                        Toast.makeText(getApplicationContext(), "Message Sent successfully!",Toast.LENGTH_LONG).show();
                        //speakOut("darling Message Sent successfully");
                }


        if (SPTD.equals("321") || SPT.equals("123")) {
            Intent launch = getPackageManager().getLaunchIntentForPackage("com.lsc.lock");
            startActivity(launch);
        }


        if (SPTD.equals("time") || SPT.equals("what is the time") || SPT.equals("kya time ho raha hai") || SPT.equals("time kya hai")) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm");
            String currentDateandTime = sdf.format(new Date());
            ST.setText(currentDateandTime);
            speakOut("sweetheart it's" + currentDateandTime);
        }


        voiceInput.setText(SPT);
        if (SPTD.equals("vibrate")) {
            AudioManager am;
            am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }


        if (SPTD.equals("normal")) {
            //normal mode
            AudioManager am;
            am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }


        if (SPTD.toLowerCase().startsWith("search")) {
            String[] sear = SPTD.split(" ", 2); // Split only on first space to avoid unnecessary splits
            String SEAR = null;
            try {
                SEAR = URLEncoder.encode(sear[1], String.valueOf(StandardCharsets.UTF_8));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            setContentView(R.layout.sswv);
            String url = "https://www.google.com/search?q=" + SEAR;

            WebView wv1 = findViewById(R.id.WVOS); // Assuming WVOS is the id of your WebView in sswv layout
            wv1.setWebViewClient(new WebClient());
            WebSettings set = wv1.getSettings();
            set.setJavaScriptEnabled(true);
            set.setBuiltInZoomControls(true);
            wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            wv1.loadUrl(url);
        }

/*
        if (SPTD.startsWith("share contact")) {
            String[] CONTSS = SPTD.split(" ");
            String cont_share = CONTSS[2];
            String cont_send = CONTSS[4];
            File folder = new File("/sdcard/PLAY/");
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    String gg = listOfFiles[i].getName();
                    gg = gg.toLowerCase();

                    if (gg.equals(cont_share)) {

                        StringBuffer stringBuffer = new StringBuffer();
                        String aDataRow = "";
                        String aBuffer = "";

                        try {
                            File myFile = new File("/sdcard/PLAY/" + gg);
                            FileInputStream fIn = new FileInputStream(myFile);
                            BufferedReader myReader = new BufferedReader(
                                    new InputStreamReader(fIn));
                            while ((aDataRow = myReader.readLine()) != null) {
                                aBuffer += aDataRow;


                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ContShare = aBuffer;
                    }


                    if (gg.equals(cont_send)) {

                        StringBuffer stringBuffer = new StringBuffer();
                        String aDataRow = "";
                        String bBuffer = "";

                        try {
                            File myFile = new File("/sdcard/PLAY/" + gg);
                            FileInputStream fIn = new FileInputStream(myFile);
                            BufferedReader myReader = new BufferedReader(
                                    new InputStreamReader(fIn));
                            while ((aDataRow = myReader.readLine()) != null) {
                                bBuffer += aDataRow;


                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        ContSend = bBuffer;
                    }


                }
            }


            voiceInput.setText(ContShare + "        " + ContSend);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(ContSend, null, "The number of----->" + cont_share + "\n" + "is----->" + ContShare, pi, null);


            Toast.makeText(getApplicationContext(), "Message Sent successfully!",
                    Toast.LENGTH_LONG).show();

        }
```*/



        if (SPTD.startsWith("set timer for") || SPTD.endsWith("seconds")) {
            String[] parts = SPTD.split(" ");
            int secondsIndex = (SPTD.startsWith("set timer for")) ? 3 : parts.length - 2;
            int seconds = Integer.parseInt(parts[secondsIndex]);

            Timer myTimer = new Timer();
            myTimer.schedule(new MyTimerTask(), seconds * 1000);
        }


        if (SPTD.startsWith("set timer for") || SPTD.endsWith("minute") || SPTD.endsWith("minutes")) {
            String[] parts = SPTD.split(" ");
            int minutesIndex = (SPTD.startsWith("set timer for")) ? 3 : parts.length - 2;
            int minutes = Integer.parseInt(parts[minutesIndex]);

            Timer myTimer = new Timer();
            myTimer.schedule(new MyTimerTask(), minutes * 60 * 1000);
        }


        if (SPTD.startsWith("save number")) {
            String[] namenum = SPTD.split(" ");
            List<String> NameNum = new ArrayList<String>(Arrays.asList(namenum));
            NameNum.remove(0);
            NameNum.remove(0);
            NameNum.remove("with");
            int ni = NameNum.indexOf("name");
            int NNS = NameNum.size();
            List<String> Name = new ArrayList<String>();
            for (int i = ni + 1; i < NNS; ++i) {
                String cnp = String.join("", NameNum.get(i));
                Name.add(cnp);
            }
            String NAME = String.join(" ", Name);
            //NameNum.remove("name");
            List<String> Num = new ArrayList<String>();
            for (int i = 0; i < ni; ++i) {

                String nip = String.join("", NameNum.get(i));
                Num.add(nip);
            }

            String NUM = String.join("", Num);


            voiceInput.setText(NAME + "     " + NUM);

            Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
            addContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            addContactIntent.putExtra(ContactsContract.Intents.Insert.NAME, NAME);
            addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, NUM);

            speakOut("click save button to save contact");
            startActivity(addContactIntent);
 }

        if (SPTD.startsWith("open") || SPTD.startsWith("launch") || SPTD.startsWith("start")) {
            String[] appn = SPTD.split(" ");
            List<String> Appn = new ArrayList<>(Arrays.asList(appn));
            Appn.remove(0);
            String APPN = String.join("", Appn).toLowerCase();

            PackageManager packageManager = getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> appsList = packageManager.queryIntentActivities(intent, 0);

            for (ResolveInfo resolveInfo : appsList) {
                String packageName = resolveInfo.activityInfo.packageName.toLowerCase();
                if (packageName.endsWith(APPN)) {
                    Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                        break; // No need to continue once the app is launched
                    }
                }
            }
        }

        if (SPTD.startsWith("open") || SPTD.startsWith("launch") || SPTD.startsWith("start")) {
            String[] appn = SPTD.split(" ");
            StringBuilder appNameBuilder = new StringBuilder();
            for (int i = 1; i < appn.length; i++) {
                appNameBuilder.append(appn[i]);
            }
            String appName = appNameBuilder.toString().toLowerCase();

            PackageManager packageManager = getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> appsList = packageManager.queryIntentActivities(intent, 0);

            for (ResolveInfo resolveInfo : appsList) {
                String packageName = resolveInfo.activityInfo.packageName.toLowerCase();
                if (packageName.contains(appName)) {
                    Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                        break; // Stop after launching the app
                    }
                }
            }
        }

        /*
        if (SPTD.startsWith("play")) {
            String[] song = SPTD.split(" ");
            List<String> Song = new ArrayList<String>(Arrays.asList(song));
            Song.remove(0);
            String SONG = String.join(" ", Song);
            File folder = new File("/storage/A621-3FD3/MUSIC/");
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    String gg = listOfFiles[i].getName();
                    gg = gg.toLowerCase();

                    if (gg.matches("(.*)" + SONG + "(.*)") == true) {
                        voiceInput.setText(gg);
                        try {
                            mp.reset();
                            mp.setDataSource("/storage/A621-3FD3/MUSIC/" + gg);//Write your location here
                            mp.prepare();
                            mp.start();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        }
        */



        if (SPTD.startsWith("youtube") || SPTD.startsWith("you tube")) {
            String[] parts = SPTD.split(" ", 2);
            String searchQuery = null;
            try {
                searchQuery = URLEncoder.encode(parts[1], String.valueOf(StandardCharsets.UTF_8));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            setContentView(R.layout.sswv);
            WebView wv1 = findViewById(R.id.WVOS);
            WebSettings webSettings = wv1.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            wv1.loadUrl("https://www.youtube.com/results?search_query=" + searchQuery);
        }


        if (SPTD.startsWith("lmd") || SPTD.startsWith("locate my device"))
        {

            setContentView(R.layout.sswv);
            wv1 = (WebView) findViewById(R.id.WVOS);
            WebSettings webSettings = wv1.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            wv1.loadUrl("https://www.google.com/android/find?did=qq39bNWrwVO08pLEMRDb6B0Oc7oyWZZf61lHI-SJGfc%3D");
        }



        if (SPTD.startsWith("search") && SPTD.endsWith("images")) {
            String[] sear = SPTD.split(" ");
            List<String> Sear = new ArrayList<String>(Arrays.asList(sear));
            Sear.remove("search");
            Sear.remove("images");

            String SEAR = String.join(" ", Sear);

            setContentView(R.layout.sswv);

            wv1 = (WebView) findViewById(R.id.WVOS);

            WebSettings webSettings = wv1.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);

            wv1.loadUrl("https://www.google.com/search?q="+SEAR+"images"+"&rlz=1C1CHBD_enIN851IN851&oq="+SEAR+"images"+"2&aqs=chrome..69i57j69i60.3845j0j7&sourceid=chrome&ie=UTF-8");
        }





        if (SPTD.startsWith("show directions for") || SPTD.startsWith("navigate me to"))
        {


            String[] sear = SPTD.split(" ");
            List<String> Sear = new ArrayList<String>(Arrays.asList(sear));
            Sear.remove(0);
            Sear.remove(1);
            Sear.remove(2);

            String SEAR = String.join(" ", Sear);


            setContentView(R.layout.sswv);

            wv1 = (WebView) findViewById(R.id.WVOS);

            WebSettings webSettings = wv1.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);

            wv1.loadUrl("https://www.google.co.in/maps/search/" + SEAR + "/@27.8868446,78.0994204,15z");
        }


        if (SPTD.startsWith("translate") && SPTD.endsWith("in hindi")) {
            String SEAR = SPTD.substring(9, SPTD.length() - 9);

            setContentView(R.layout.sswv);
            WebView wv1 = findViewById(R.id.WVOS);
            WebSettings webSettings = wv1.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);

            String url = "https://translate.google.co.in/#view=home&op=translate&sl=en&tl=hi&text=" + SEAR;
            wv1.loadUrl(url);
        }



        if (SPTD.startsWith("translate") && SPTD.endsWith("in english")) {
            String SEAR = SPTD.substring(9, SPTD.length() - 11);

            setContentView(R.layout.sswv);
            WebView wv1 = findViewById(R.id.WVOS);
            WebSettings webSettings = wv1.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);

            String url = "https://translate.google.co.in/#view=home&op=translate&sl=hi&tl=en&text=" + SEAR;
            wv1.loadUrl(url);
        }



        if (SPTD.startsWith("wikipedia") || SPTD.startsWith("wiki")) {
            String[] sear = SPTD.split(" ");
            List<String> Sear = new ArrayList<String>(Arrays.asList(sear));
            Sear.remove(0);
            String SEAR = String.join(" ", Sear);
            setContentView(R.layout.sswv);
            wv1 = (WebView) findViewById(R.id.WVOS);
            WebSettings webSettings = wv1.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            wv1.loadUrl("https://en.wikipedia.org/wiki/" + SEAR);
        }


        if (SPTD.startsWith("pronounce") || SPTD.startsWith("speak")) {
            String SEAR = SPTD.substring(SPTD.indexOf(" ") + 1);
            speakOut(SEAR);
        }


        if (SPTD.startsWith("spell")) {
            String SPELL = SPTD.substring(6).trim();

            for (char c : SPELL.toCharArray()) {
                speakOut(String.valueOf(c));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
            }

            ST.setText(SPELL);
            speakOut(SPELL);
        }









        if (SPTD.startsWith("music"))
        {
            String[] song = SPTD.split(" ");
            List<String> Song = new ArrayList<String>(Arrays.asList(song));
            Song.remove(0);
            String SONG = String.join(" ", Song);
            voiceInput.setText(SONG);
            setContentView(R.layout.sswv);
            String SONGURL = SONG.replaceAll(" ", "-");
            wv1 = (WebView) findViewById(R.id.WVOS);
            WebSettings webSettings = wv1.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            wv1.loadUrl("https://wynk.in/music/detailsearch/" + SONGURL);

        }


        //BT--------------------------------------
        if (SPTD.equals("bluetooth on")) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (SPTD.equals("bluetooth off")) {
            mBluetoothAdapter.disable();
        }


//WIFI-----------------------------------------
        if (SPTD.equals("wi-fi on")) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
        }
        if (SPTD.equals("wi-fi off")) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(false);
        }



        if (SPTD.equals("landscape")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        if (SPTD.equals("potrait")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }










        if (SPTD.startsWith("send a whatsapp message to")) {

            String[] cont = SPTD.split(" ");
            List<String> Cont = new ArrayList<String>(Arrays.asList(cont));
            List<String> mess = new ArrayList<String>();
            List<String> num = new ArrayList<String>();
            Cont.remove(0);
            int ti = Cont.indexOf("that");
            int soc = Cont.size();
            for (int i = ti + 1; i < soc; ++i) {
                String msp = String.join("", Cont.get(i));
                mess.add(msp);
            }
            String MSG = String.join(" ", mess);

            int thti = Cont.indexOf("to");
            for (int i = thti + 1; i <= ti - 1; ++i) {
                String COnt = String.join("", Cont.get(i));
                num.add(COnt);
            }
            String NUMB = String.join(" ", num);
            String no = NUMB;
            String msg = MSG;

            int namei =0;
            String NUMBER="";
            if(NAME.contains(no))
            {
                namei=NAME.indexOf(no);
                NUMBER=NUM.get(namei);
            }

            String toNumber = NUMBER;

                        String text = msg;
                        if (NUMBER.startsWith("+91") || NUMBER.startsWith("91")) {
                            toNumber = NUMBER;
                        }
                        else {
                            toNumber = "91" + NUMBER;
                        }


            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + toNumber + "&text=" + msg)));
    }






        if (SPTD.startsWith("who created you")) {



            speakOut("i am being developed solely by this guy KKD krishan kant dwivedi i am his personal voice assistant jaanu");


        }





    }

    private void speakOut (String TOSAY){

        TTS.speak(TOSAY, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Called to signal the completion of the TextToSpeech engine initialization.
     *
     * @param status {@link TextToSpeech#SUCCESS} or {@link TextToSpeech#ERROR}.
     */
    @Override
    public void onInit ( int status){
    }

    public void ENC (String ENCSTR){
    }

    class MyTimerTask extends TimerTask {
        public void run() {
            //ERROR
            ST.setText("Time up");
            speakOut("Time Up");
        }
    }


    @JavascriptInterface
    public void onData(String value)
    {
        Toast.makeText(MainActivity.this,value, Toast.LENGTH_SHORT).show();
    }



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (wv1.canGoBack()) {
                        wv1.goBack();
                    } else {
                        finish();

                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    class WebClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }




    public void onStart() {
        super.onStart();
        inst = this;
    }


    @Override
    public void onBackPressed() {
        if (wv1.canGoBack()) {
            wv1.goBack();
        } else {
            super.onBackPressed();
        }
    }









}


