package edu.utn.listenchat.connector;

import android.content.Intent;
import android.widget.Toast;

import java.util.List;

import edu.utn.listenchat.activity.MainActivity;
import edu.utn.listenchat.service.PersistenceService;

import static android.widget.Toast.LENGTH_LONG;

public class MessengerConnector {

    private MainActivity mainActivity;
    private PersistenceService persistenceService;

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

    /** Mocked Service - change for real Messenger contacts*/
    public List<String> obtainContacts() {
        return this.persistenceService.getContacts();
    }


    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

}
