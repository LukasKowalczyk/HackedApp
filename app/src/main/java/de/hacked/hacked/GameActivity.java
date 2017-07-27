package de.hacked.hacked;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    public static final String GAME_URL = "gameUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        final String gameId = intent.getStringExtra(StartActivity.GAME_ID);
        byte[] qrCode = intent.getByteArrayExtra(StartActivity.QR_CODE);

        TextView gameIdTextView = (TextView) findViewById(R.id.gameIdTextView);
        gameIdTextView.setText(getResources().getString(R.string.titleGameIdTextView)  + gameId);

        ImageView image = (ImageView) findViewById(R.id.qRCodeImageView);
        Bitmap bMap = BitmapFactory.decodeByteArray(qrCode, 0, qrCode.length);
        image.setImageBitmap(bMap);

        final FloatingActionButton generateGameButton = (FloatingActionButton) findViewById(R.id.startGameFloatingActionButton);
        generateGameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Hier kommt nun der Sprung zur Webseite
                Intent intent = new Intent(GameActivity.this, WebActivity.class);
                intent.putExtra(GAME_URL, getResources().getString(R.string.hostURL) + "/?gameId=" + gameId);
                startActivity(intent);
            }
        });

        final Button helpImageButton = (Button) findViewById(R.id.gameHelpButton);
        helpImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Hier kommt nun der Sprung zur Webseite
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.hostURL) + "/#!help"));
                startActivity(browserIntent);
            }
        });
    }


}
