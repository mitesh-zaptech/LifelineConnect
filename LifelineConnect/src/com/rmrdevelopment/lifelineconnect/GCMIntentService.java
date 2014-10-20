package com.rmrdevelopment.lifelineconnect;

import static com.rmrdevelopment.lifelineconnect.utils.CommonUtilities.SENDER_ID;
import static com.rmrdevelopment.lifelineconnect.utils.CommonUtilities.displayMessage;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.rmrdevelopment.lifelineconnect.activities.SplashForNotificationActivity;
import com.rmrdevelopment.lifelineconnect.utils.CommonUtilities;

/**
 * {@link IntentService} responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	public String RegId="";
	
	
	public GCMIntentService() {

		super(SENDER_ID);
		Log.i("GCM Intent Service", "Class Called");
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "GCM Intent Service: = " + registrationId);
		RegId=registrationId; 
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit= preferences.edit();
		edit.putString("GCM_APP_ID", registrationId);
		edit.commit();
		//displayMessage(context, getString(R.string.gcm_registered));
		// ServerUtilities.register(context, registrationId);
		CommonUtilities.register = true;
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		//displayMessage(context, getString(R.string.gcm_unregistered));
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			// ServerUtilities.unregister(context, registrationId);
			CommonUtilities.register = false;
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
		String message = getString(R.string.gcm_message);
		// Extract the payload from the message
		final Bundle extras = intent.getExtras();
		if (extras != null) {
			message= extras.get("message").toString();
			System.out.println("RESPONSE:" + extras.get("message"));
		}
		
		displayMessage(context, message);
		
		// notifies user
		generateNotification(context, message);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		//displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		
		Intent notificationIntent = new Intent(context, SplashForNotificationActivity.class);
		
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
				Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
	}

}
