package com.intion.proxy.event;

import com.intion.proxy.utils.EventException;

public abstract class ProxyEvent {

    private boolean cancelled;

    public ProxyEvent() {

    }

    public void setCancelled()
    {
        this.setCancelled(true);
    }

    public boolean isCancelled() {
        if (!(this instanceof Cancelable)) {
            throw new EventException("Event is not Cancellable");
        } else {
            return this.cancelled;
        }
    }


    public void setCancelled(boolean value)
    {
        if (!(this instanceof Cancelable))
        {
            throw new EventException("Event is not Cancellable");
        } else {
            this.cancelled = value;
        }
    }

    public final String getEventName() {
        return this.getClass().getSimpleName();
    }

}
