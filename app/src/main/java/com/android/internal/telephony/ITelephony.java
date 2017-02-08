package com.android.internal.telephony;

/**
 * Created by varunrao on 08/02/17.
 */

public interface ITelephony {
    boolean endCall();

    void answerRingingCall();

    void silenceRinger();
}
