package org.sil.hearthis;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Thomson on 1/18/2016.
 */
public class AcceptNotificationHandler implements HttpRequestHandler {

    public interface NotificationListener {
        void onNotification(String message);
    }
    static ArrayList<NotificationListener> notificationListeners= new ArrayList<NotificationListener>();

    public static void addNotificationListener(NotificationListener listener) {
        notificationListeners.add(listener);
    }

    public static void removeNotificationListener(NotificationListener listener) {
        notificationListeners.remove(listener);
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        // Enhance: allow the notification to contain a message, and pass it on.
        // The copy is made because the onNotification calls may well remove listeners, leading to concurrent modification exceptions.
        //for (NotificationListener listener: notificationListeners.toArray(new NotificationListener[notificationListeners.size()])) {
        //    listener.onNotification("");
        //}

        // The notification received from HearThis looks like this (per RFC 7230):
        //    POST /notify?message=sync_success HTTP/1.1
        // Parse the HttpRequest to extract the "message" portion that conveys "sync_success" or
        // "sync_interrupted", and send it to the UI.
        String s1 = request.getRequestLine().toString();
        Log.d("Sync", "AcceptNotificationHandler, parsing request.getRequestLine()");
        Log.d("Sync", "  s1 = " + s1);
        // We want the part between the two space chars, the "message" value.
        String s2 = s1.substring(s1.indexOf(' ') + 1, s1.lastIndexOf(' '));
        Log.d("Sync", "  s2 = " + s2);
        String status = s2.substring(s2.indexOf('=') + 1);
        Log.d("Sync", "AcceptNotificationHandler, status = " + status);

        // Enhance: allow the notification to contain a message, and pass it on.
        // The copy is made because the onNotification calls may well remove listeners, leading to concurrent modification exceptions.
        for (NotificationListener listener: notificationListeners.toArray(new NotificationListener[notificationListeners.size()])) {
            listener.onNotification(status);
        }

        //response.setEntity(new StringEntity("success"));
        response.setEntity(new StringEntity(status));
    }
}
