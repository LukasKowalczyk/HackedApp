package de.hacked.hacked;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class StartActivity extends AppCompatActivity {

    public static final String GAME_ID = "gameId";

    public static final String QR_CODE = "qrCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Button helpImageButton = (Button) findViewById(R.id.startHelpButton);
        helpImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Hier kommt nun der Sprung zur Webseite
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.hostURL) + "/#!help"));
                startActivity(browserIntent);
            }
        });

        final FloatingActionButton generateGameButton = (FloatingActionButton) findViewById(R.id.generateGameFloatingActionButton);
        generateGameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Hier der Service Call um die ID zu generieren
                new GameIdHttpRequestTask().execute();
            }
        });
    }


    private class GameIdHttpRequestTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog;

        public GameIdHttpRequestTask() {
            dialog = new ProgressDialog(StartActivity.this);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.generateGameProcessingDialog));
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                final String url = getResources().getString(R.string.hostURL) + "/generateGame";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                return restTemplate.getForObject(url, String.class);
            } catch (Exception e) {
                Log.e("StartActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String gameId) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            //Hier muss der Service Call um den QR-Code zu generieren hin
            new QRCodeHttpRequestTask(gameId).execute();

        }

    }

    private class QRCodeHttpRequestTask extends AsyncTask<Void, Void, byte[]> {
        private ProgressDialog dialog;
        private String gameId;

        public QRCodeHttpRequestTask(String gameId) {
            dialog = new ProgressDialog(StartActivity.this);
            this.gameId = gameId;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.generateQRCodeProcessingDialog));
            dialog.show();
        }

        @Override
        protected byte[] doInBackground(Void... params) {
            try {
                final String url = getResources().getString(R.string.hostURL) + "/getQR?gameId=" + gameId;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
                return restTemplate.getForObject(url, byte[].class);
            } catch (Exception e) {
                Log.e("GameActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(byte[] qrCode) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            //Danach springen wird zurnächsten Maske
            Intent intent = new Intent(StartActivity.this, GameActivity.class);
            intent.putExtra(GAME_ID, gameId);
            intent.putExtra(QR_CODE, qrCode);
            startActivity(intent);
        }

    }
}
