package com.example.mlimagelabel;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class NotificationActivity extends FirebaseMessagingService {

   public  NotificationActivity(){
       super();
   }
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

    }
}