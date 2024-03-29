package ingsw.group1.findmyphone.location;

import android.location.Location;

/**
 * Parser class of static methods that provides composition of message for location request and
 * response
 * Message request is composed as:
 * {@link LocationMessageHelper#LOCATION_REQUEST_TAG}
 * <p>
 * Message response is composed in this way:
 * {@link LocationMessageHelper#LOCATION_RESPONSE_TAG}<LT>value_latitude</LT><LG>value_longitude
 * </LG>
 *
 * @author Giorgia Bortoletti
 */
public class LocationMessageHelper {

    public static final String LOCATION_REQUEST_TAG = "LOCATION_REQUEST";
    public static final String LOCATION_RESPONSE_TAG = "LOCATION_RESPONSE";

    public static final String LONGITUDE_TAG = "<LG>";
    public static final String LONGITUDE_END_TAG = "</LG>";

    public static final String LATITUDE_TAG = "<LT>";
    public static final String LATITUDE_END_TAG = "</LT>";

    //---------------------------- COMPOSE MESSAGE ----------------------------

    /**
     * Compose a location request message
     *
     * @return a formatted message for a location request
     */
    protected static String composeRequestLocation() {
        return LOCATION_REQUEST_TAG;
    }

    /**
     * Return a String formatted as a message containing actual position
     *
     * @param foundLocation the found location the device needs to send back
     * @return a formatted string containing the location as <>longitude</> <>latitude</>
     */
    protected static String composeResponseLocation(Location foundLocation) {
        String locationResponseMessage = LOCATION_RESPONSE_TAG;
        locationResponseMessage += LATITUDE_TAG + foundLocation.getLatitude() + LATITUDE_END_TAG;
        locationResponseMessage += LONGITUDE_TAG + foundLocation.getLongitude() + LONGITUDE_END_TAG;
        return locationResponseMessage;
    }

    //---------------------------- ANALYZE MESSAGES ----------------------------

    /**
     * @param messageReceived the text message received
     * @return true if the received text contains the (formatted) location Request
     */
    protected static boolean isLocationRequest(String messageReceived) {
        return messageReceived.contains(LOCATION_REQUEST_TAG);
    }

    /**
     * @param messageReceived string containing the received txt message
     * @return true if the received message contains a location response message
     */
    protected static boolean isLocationResponse(String messageReceived) {
        return (messageReceived.contains(LOCATION_RESPONSE_TAG)
                && messageReceived.contains(LATITUDE_TAG)
                && messageReceived.contains(LATITUDE_END_TAG)
                && messageReceived.contains(LONGITUDE_TAG)
                && messageReceived.contains(LONGITUDE_END_TAG));
    }

    /**
     * @param receivedMessage string containing the text received sy sms
     * @return the latitude from the receivedMessage, empty string if it isn't present
     * @author Turcato
     * Extract the latitude from a received message if it is present,
     * empty string otherwise.
     */
    protected static String getLatitudeFrom(String receivedMessage) {
        int indexStart = receivedMessage.indexOf(LocationMessageHelper.LATITUDE_TAG);
        int indexEnd = receivedMessage.indexOf(LocationMessageHelper.LATITUDE_END_TAG);
        if (indexStart > -1 && indexStart > -1) {
            indexStart += LocationMessageHelper.LATITUDE_TAG.length();
            return receivedMessage.substring(indexStart, indexEnd);
        }
        return "";
    }

    /**
     * @param receivedMessage string containing the text received sy sms
     * @return the longitude from the receivedMessage, empty string if it isn't present
     * @author Turcato
     * Extract the longitude from a received message if it is present,
     * empty string otherwise.
     */
    protected static String getLongitudeFrom(String receivedMessage) {
        int indexStart = receivedMessage.indexOf(LocationMessageHelper.LONGITUDE_TAG);
        int indexEnd = receivedMessage.indexOf(LocationMessageHelper.LONGITUDE_END_TAG);
        if (indexStart > -1 && indexEnd > -1) {
            indexStart += LocationMessageHelper.LONGITUDE_TAG.length();
            return receivedMessage.substring(indexStart, indexEnd);
        }
        return "";
    }


}
