package com.intion.proxy.event;

public interface Cancelable {

    boolean isCancelled();

    void setCancelled();

    void setCancelled(boolean value);

}
