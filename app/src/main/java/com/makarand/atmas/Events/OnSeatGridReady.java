package com.makarand.atmas.Events;

import Model.Subject;

public interface OnSeatGridReady{
    Subject getSelectedSubject();
    void sendBroadcastMessage();
    String getDatabaseToken();
}
