package ingsw.group1.findmyphone;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ingsw.group1.findmyphone.alarm.AlarmManager;
import ingsw.group1.findmyphone.location.CommandResponseLocation;
import ingsw.group1.findmyphone.location.LocationManager;
import ingsw.group1.msglibrary.ReceivedMessageListener;
import ingsw.group1.msglibrary.SMSManager;
import ingsw.group1.msglibrary.SMSMessage;
import ingsw.group1.msglibrary.SMSPeer;

/**
 * This is the main manager
 *
 * @author Pardeep
 * @author Giorgia Bortoletti (refactoring)
 */
public class Manager {
    private static final String MANAGER_TAG = "Manager";
    private static Context currentContext;

    private AlarmManager alarmManager;
    private LocationManager locationManager;
    private SMSManager smsManager;
    private CommandResponseLocation sendResponseSms;

    /**
     * Constructor
     *
     * @param context application context
     */
    public Manager(Context context) {
        currentContext = context.getApplicationContext();
        locationManager = new LocationManager();
        alarmManager = new AlarmManager();
        smsManager = SMSManager.getInstance(Manager.currentContext);
    }

    //---------------------------- LISTENERS ----------------------------

    /**
     * Method to remove receivedListener
     */
    public void removeReceiveListener() {
        smsManager.removeReceiveListener();
    }

    /**
     * Setter for receivedListener in SMSHandler
     *
     * @param newReceivedListener the new listener
     */
    public void setReceiveListener(ReceivedMessageListener<SMSMessage> newReceivedListener) {
        smsManager.setReceiveListener(newReceivedListener);
    }

    //---------------------------- SEND REQUEST ----------------------------

    /**
     * @author Turcato
     * Send an urgent message to the peer with a location request
     *
     * @param smsPeer the peer to which  send sms Location request
     */
    public void sendLocationRequest(SMSPeer smsPeer) {
        String requestStringMessage = locationManager.getRequestLocationMessage();
        SMSMessage smsMessage = new SMSMessage(smsPeer, requestStringMessage);
        smsManager.sendUrgentMessage(smsMessage);
    }

    /**
     * @author Turcato
     * Send an urgent message to the peer for an Alarm request
     *
     * @param smsPeer the peer to which  send sms Location & alarm request
     */
    public void sendAlarmRequest(SMSPeer smsPeer) {
        String requestStringMessage = alarmManager.getAlarmRequestMessage();
        SMSMessage smsMessage = new SMSMessage(smsPeer, requestStringMessage);
        smsManager.sendUrgentMessage(smsMessage);
    }

    //---------------------------- ACTIONS AFTER RECEIVING A REQUEST ----------------------------

    /**
     * This method checks the received string and can active alarm or send location based on its content
     *
     * @param requestMessage the request message that decide which action to do
     * @param phoneNumber    the number to which send your phone's location or active alarm
     */
    public void analyzeRequest(String requestMessage, String phoneNumber) {
        if (locationManager.isLocationRequest(requestMessage)) {
            //Action to execute when device receives a Location request
            sendResponseSms = new CommandResponseLocation(phoneNumber, currentContext.getApplicationContext());
            locationManager.getLastLocation(currentContext.getApplicationContext(), sendResponseSms);
        }
        //User has to close app manually to stop the alarm
        if (alarmManager.isAlarmRequest(requestMessage))
            alarmManager.startAlarm(currentContext.getApplicationContext());
    }

    /**
     * Based on the response this method opens the activityClass or open the default map app
     *
     * @param messageResponse The message received
     */
    public void activeResponse(SMSMessage messageResponse, Class activityClass) {
        String requestMessage = messageResponse.getData();
        if (locationManager.isLocationRequest(requestMessage)
                || alarmManager.isAlarmRequest(requestMessage)) {
            openRequestsActivity(requestMessage, messageResponse.getPeer().getAddress(), activityClass);
        }

        //The only expected response
        if (locationManager.isLocationResponse(requestMessage)) {
            Double longitude;
            Double latitude;
            try {
                longitude = Double.parseDouble(locationManager.getLongitudeFrom(requestMessage));
                latitude = Double.parseDouble(locationManager.getLatitudeFrom(requestMessage));
                locationManager.openMapsUrl(latitude, longitude);
            } catch (Exception e) {
                //Written in log for future users to report
                Log.e(MANAGER_TAG, e.getMessage());
            }
        }
    }

    /**
     * @author Turcato
     * Opens an activityClass, forwarding the receivedMessageText and the receivedMessageReturnAddress
     *
     * @param activityClass the activity to be opened
     * @param receivedMessageText the text of the request message
     * @param receivedMessageReturnAddress the return address of the request message
     */
    private void openRequestsActivity(String receivedMessageText, String receivedMessageReturnAddress, Class activityClass) {
        Log.d(MANAGER_TAG, "OpenRequestsActivity");
        Intent openAlarmAndLocateActivityIntent = new Intent(currentContext.getApplicationContext(), activityClass);
        openAlarmAndLocateActivityIntent.putExtra(Constants.receivedStringMessage, receivedMessageText);
        openAlarmAndLocateActivityIntent.putExtra(Constants.receivedStringAddress, receivedMessageReturnAddress);
        openAlarmAndLocateActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        currentContext.getApplicationContext().startActivity(openAlarmAndLocateActivityIntent);
    }

}