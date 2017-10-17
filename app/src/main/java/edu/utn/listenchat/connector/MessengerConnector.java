package edu.utn.listenchat.connector;

import android.content.Intent;
import android.widget.Toast;

import edu.utn.listenchat.activity.MainActivity;

import static android.widget.Toast.LENGTH_LONG;

public class MessengerConnector {

    private MainActivity mainActivity;

    public void send(String message, int id) {
        //Uri uri = Uri.parse("fb-messenger://user/");
        //uri = ContentUris.withAppendedId(uri,id);
        //Intent intent = new Intent(Intent.ACTION_SEND, uri);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.facebook.orca");

        try {
            mainActivity.startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mainActivity, "Please Install Facebook Messenger",LENGTH_LONG).show();
        }
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

}
