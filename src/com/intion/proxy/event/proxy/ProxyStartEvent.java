package com.intion.proxy.event.proxy;

import com.intion.proxy.Loader;
import com.intion.proxy.event.Cancelable;
import com.intion.proxy.event.ProxyEvent;

public class ProxyStartEvent extends ProxyEvent {

    private Loader loader;

    public ProxyStartEvent(Loader loader) {
        this.loader = loader;
    }

    public Loader getLoader() {
        return loader;
    }
}
