package com.hexon.mvvm.bus;

import com.hexon.mvvm.binding.command.BindingAction;
import com.hexon.mvvm.binding.command.BindingConsumer;

import java.lang.ref.WeakReference;


public class WeakAction<T> {
    private BindingAction action;
    private BindingConsumer<T> consumer;
    private boolean isLive;
    private Object target;
    private WeakReference reference;

    public WeakAction(Object target, BindingAction action) {
        reference = new WeakReference(target);
        this.action = action;

    }

    public WeakAction(Object target, BindingConsumer<T> consumer) {
        reference = new WeakReference(target);
        this.consumer = consumer;
    }

    public void execute() {
        if (action != null && isLive()) {
            action.call();
        }
    }

    public void execute(T parameter) {
        if (consumer != null && isLive()) {
            consumer.call(parameter);
        }
    }

    public void markForDeletion() {
        reference.clear();
        reference = null;
        action = null;
        consumer = null;
    }

    public BindingAction getBindingAction() {
        return action;
    }

    public BindingConsumer getBindingConsumer() {
        return consumer;
    }

    public boolean isLive() {
        if (reference == null) {
            return false;
        }
        if (reference.get() == null) {
            return false;
        }
        return true;
    }


    public Object getTarget() {
        if (reference != null) {
            return reference.get();
        }
        return null;
    }
}
