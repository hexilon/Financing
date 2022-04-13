package com.hexon.mvvm.bus;

import com.hexon.mvvm.binding.command.BindingAction;
import com.hexon.mvvm.binding.command.BindingConsumer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class Messenger {

    private static Messenger sDefaultInstance;
    private HashMap<Type, List<WeakActionAndToken>> mRecipientsOfSubclassesAction;
    private HashMap<Type, List<WeakActionAndToken>> mRecipientsStrictAction;

    public static Messenger getDefault() {
        if (sDefaultInstance == null) {
            sDefaultInstance = new Messenger();
        }
        return sDefaultInstance;
    }

    public static void overrideDefault(Messenger newWeakMessenger) {
        sDefaultInstance = newWeakMessenger;
    }

    public static void reset() {
        sDefaultInstance = null;
    }

    /**
     * @param recipient the receiver,if register in activity the recipient always set "this",
     *                  and "WeakMessenger.getDefault().unregister(this)" in onDestroy,if in ViewModel,
     *                  you can also register with Activity context and also in onDestroy to unregister.
     * @param action    do something on message received
     */
    public void register(Object recipient, BindingAction action) {
        register(recipient, null, false, action);
    }

    /**
     * @param recipient                 the receiver,if register in activity the recipient always set "this",
     *                                  and "WeakMessenger.getDefault().unregister(this)" in onDestroy,if in ViewModel,
     *                                  you can also register with Activity context and also in onDestroy to unregister.
     * @param receiveDerivedMessagesToo whether Derived class of recipient can receive the message
     * @param action                    do something on message received
     */
    public void register(Object recipient, boolean receiveDerivedMessagesToo, BindingAction action) {
        register(recipient, null, receiveDerivedMessagesToo, action);
    }

    /**
     * @param recipient the receiver,if register in activity the recipient always set "this",
     *                  and "WeakMessenger.getDefault().unregister(this)" in onDestroy,if in ViewModel,
     *                  you can also register with Activity context and also in onDestroy to unregister.
     * @param token     register with a unique token,when a messenger send a msg with same token,it
     *                  will receive this msg
     * @param action    do something on message received
     */
    public void register(Object recipient, Object token, BindingAction action) {
        register(recipient, token, false, action);
    }

    /**
     * @param recipient                 the receiver,if register in activity the recipient always set "this",
     *                                  and "WeakMessenger.getDefault().unregister(this)" in onDestroy,if in ViewModel,
     *                                  you can also register with Activity context and also in onDestroy to unregister.
     * @param token                     register with a unique token,when a messenger send a msg with same token,it
     *                                  will
     *                                  receive this msg
     * @param receiveDerivedMessagesToo whether Derived class of recipient can receive the message
     * @param action                    do something on message received
     */
    public void register(Object recipient, Object token, boolean receiveDerivedMessagesToo, BindingAction action) {

        Type messageType = NotMsgType.class;

        HashMap<Type, List<WeakActionAndToken>> recipients;

        if (receiveDerivedMessagesToo) {
            if (mRecipientsOfSubclassesAction == null) {
                mRecipientsOfSubclassesAction = new HashMap<Type, List<WeakActionAndToken>>();
            }

            recipients = mRecipientsOfSubclassesAction;
        } else {
            if (mRecipientsStrictAction == null) {
                mRecipientsStrictAction = new HashMap<Type, List<WeakActionAndToken>>();
            }

            recipients = mRecipientsStrictAction;
        }

        List<WeakActionAndToken> list;

        if (!recipients.containsKey(messageType)) {
            list = new ArrayList<WeakActionAndToken>();
            recipients.put(messageType, list);
        } else {
            list = recipients.get(messageType);
        }

        WeakAction weakAction = new WeakAction(recipient, action);

        WeakActionAndToken item = new WeakActionAndToken(weakAction, token);
        list.add(item);
        cleanup();
    }

    /**
     * @param recipient {}
     * @param tClass    class of T
     * @param action    this action has one params that type of tClass
     * @param <T>       message data type
     */
    public <T> void register(Object recipient, Class<T> tClass, BindingConsumer<T> action) {
        register(recipient, null, false, action, tClass);
    }

    /**
     * see {}
     *
     * @param recipient                 receiver of message
     * @param receiveDerivedMessagesToo whether derived class of recipient can receive the message
     * @param tClass                    class of T
     * @param action                    this action has one params that type of tClass
     * @param <T>                       message data type
     */
    public <T> void register(Object recipient, boolean receiveDerivedMessagesToo, Class<T> tClass, BindingConsumer<T> action) {
        register(recipient, null, receiveDerivedMessagesToo, action, tClass);
    }

    /**
     * see {}
     *
     * @param recipient receiver of message
     * @param token     register with a unique token,when a messenger send a msg with same token,it
     *                  will
     *                  receive this msg
     * @param tClass    class of T for BindingConsumer
     * @param action    this action has one params that type of tClass
     * @param <T>       message data type
     */
    public <T> void register(Object recipient, Object token, Class<T> tClass, BindingConsumer<T> action) {
        register(recipient, token, false, action, tClass);
    }

    /**
     * see {}
     *
     * @param recipient                 receiver of message
     * @param token                     register with a unique token,when a messenger send a msg with same token,it
     *                                  will
     *                                  receive this msg
     * @param receiveDerivedMessagesToo whether derived class of recipient can receive the message
     * @param action                    this action has one params that type of tClass
     * @param tClass                    class of T for BindingConsumer
     * @param <T>                       message data type
     */
    public <T> void register(Object recipient, Object token, boolean receiveDerivedMessagesToo, BindingConsumer<T> action, Class<T> tClass) {

        Type messageType = tClass;

        HashMap<Type, List<WeakActionAndToken>> recipients;

        if (receiveDerivedMessagesToo) {
            if (mRecipientsOfSubclassesAction == null) {
                mRecipientsOfSubclassesAction = new HashMap<Type, List<WeakActionAndToken>>();
            }

            recipients = mRecipientsOfSubclassesAction;
        } else {
            if (mRecipientsStrictAction == null) {
                mRecipientsStrictAction = new HashMap<Type, List<WeakActionAndToken>>();
            }

            recipients = mRecipientsStrictAction;
        }

        List<WeakActionAndToken> list;

        if (!recipients.containsKey(messageType)) {
            list = new ArrayList<WeakActionAndToken>();
            recipients.put(messageType, list);
        } else {
            list = recipients.get(messageType);
        }

        WeakAction weakAction = new WeakAction<T>(recipient, action);

        WeakActionAndToken item = new WeakActionAndToken(weakAction, token);
        list.add(item);
        cleanup();
    }


    private void cleanup() {
        cleanupList(mRecipientsOfSubclassesAction);
        cleanupList(mRecipientsStrictAction);
    }

    /**
     * @param token send with a unique token,when a receiver has register with same token,it will
     *              receive this msg
     */
    public void sendNoMsg(Object token) {
        sendToTargetOrType(null, token);
    }

    /**
     * send to recipient directly with has not any message
     *
     * @param target WeakMessenger.getDefault().register(this, ..) in a activity,if target set this
     *               activity
     *               it will receive the message
     */
    public void sendNoMsgToTarget(Object target) {
        sendToTargetOrType(target.getClass(), null);
    }

    /**
     * send message to target with token,when a receiver has register with same token,it will
     * receive this msg
     *
     * @param token  send with a unique token,when a receiver has register with same token,it will
     *               receive this msg
     * @param target send to recipient directly with has not any message,
     *               WeakMessenger.getDefault().register(this, ..) in a activity,if target set this activity
     *               it will receive the message
     */
    public void sendNoMsgToTargetWithToken(Object token, Object target) {
        sendToTargetOrType(target.getClass(), token);
    }

    /**
     * send the message type of T, all receiver can receive the message
     *
     * @param message any object can to be a message
     * @param <T>     message data type
     */
    public <T> void send(T message) {
        sendToTargetOrType(message, null, null);
    }

    /**
     * send the message type of T, all receiver can receive the message
     *
     * @param message any object can to be a message
     * @param token   send with a unique token,when a receiver has register with same token,it will
     *                receive this message
     * @param <T>     message data type
     */
    public <T> void send(T message, Object token) {
        sendToTargetOrType(message, null, token);
    }

    /**
     * send message to recipient directly
     *
     * @param message any object can to be a message
     * @param target  send to recipient directly with has not any message,
     *                WeakMessenger.getDefault().register(this, ..) in a activity,if target set this activity
     *                it will receive the message
     * @param <T>     message data type
     * @param <R>     target
     */
    public <T, R> void sendToTarget(T message, R target) {
        sendToTargetOrType(message, target.getClass(), null);
    }

    /**
     * Unregister the receiver such as:
     * WeakMessenger.getDefault().unregister(this)" in onDestroy in the Activity is required avoid
     * to
     * memory leak!
     *
     * @param recipient receiver of message
     */
    public void unregister(Object recipient) {
        unregisterFromLists(recipient, mRecipientsOfSubclassesAction);
        unregisterFromLists(recipient, mRecipientsStrictAction);
        cleanup();
    }


    public <T> void unregister(Object recipient, Object token) {
        unregisterFromLists(recipient, token, null, mRecipientsStrictAction);
        unregisterFromLists(recipient, token, null, mRecipientsOfSubclassesAction);
        cleanup();
    }


    private static <T> void sendToList(
            T message,
            Collection<WeakActionAndToken> list,
            Type messageTargetType,
            Object token) {
        if (list != null) {
            // Clone to protect from people registering in a "receive message" method
            // Bug correction Messaging BL0004.007
            ArrayList<WeakActionAndToken> listClone = new ArrayList<>();
            listClone.addAll(list);

            for (WeakActionAndToken item : listClone) {
                WeakAction executeAction = item.getAction();
                if (executeAction != null
                        && item.getAction().isLive()
                        && item.getAction().getTarget() != null
                        && (messageTargetType == null
                        || item.getAction().getTarget().getClass() == messageTargetType
                        || classImplements(item.getAction().getTarget().getClass(), messageTargetType))
                        && ((item.getToken() == null && token == null)
                        || item.getToken() != null && item.getToken().equals(token))) {
                    executeAction.execute(message);
                }
            }
        }
    }

    private static void unregisterFromLists(Object recipient, HashMap<Type, List<WeakActionAndToken>> lists) {
        if (recipient == null || lists == null || lists.size() == 0) {
            return;
        }
        synchronized (lists) {
            for (Type messageType : lists.keySet()) {
                for (WeakActionAndToken item : lists.get(messageType)) {
                    WeakAction weakAction = item.getAction();

                    if (weakAction != null
                            && recipient == weakAction.getTarget()) {
                        weakAction.markForDeletion();
                    }
                }
            }
        }
        cleanupList(lists);
    }

    private static <T> void unregisterFromLists(
            Object recipient,
            BindingConsumer<T> action,
            HashMap<Type, List<WeakActionAndToken>> lists,
            Class<T> tClass) {
        Type messageType = tClass;

        if (recipient == null
                || lists == null
                || lists.size() == 0
                || !lists.containsKey(messageType)) {
            return;
        }

        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(messageType)) {
                WeakAction<T> weakActionCasted = (WeakAction<T>) item.getAction();

                if (weakActionCasted != null
                        && recipient == weakActionCasted.getTarget()
                        && (action == null
                        || action == weakActionCasted.getBindingConsumer())) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }

    private static void unregisterFromLists(
            Object recipient,
            BindingAction action,
            HashMap<Type, List<WeakActionAndToken>> lists
    ) {
        Type messageType = NotMsgType.class;

        if (recipient == null
                || lists == null
                || lists.size() == 0
                || !lists.containsKey(messageType)) {
            return;
        }

        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(messageType)) {
                WeakAction weakActionCasted = (WeakAction) item.getAction();

                if (weakActionCasted != null
                        && recipient == weakActionCasted.getTarget()
                        && (action == null
                        || action == weakActionCasted.getBindingAction())) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }


    private static <T> void unregisterFromLists(
            Object recipient,
            Object token,
            BindingConsumer<T> action,
            HashMap<Type, List<WeakActionAndToken>> lists, Class<T> tClass) {
        Type messageType = tClass;

        if (recipient == null
                || lists == null
                || lists.size() == 0
                || !lists.containsKey(messageType)) {
            return;
        }

        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(messageType)) {
                WeakAction<T> weakActionCasted = (WeakAction<T>) item.getAction();

                if (weakActionCasted != null
                        && recipient == weakActionCasted.getTarget()
                        && (action == null
                        || action == weakActionCasted.getBindingConsumer())
                        && (token == null
                        || token.equals(item.getToken()))) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }

    private static void unregisterFromLists(
            Object recipient,
            Object token,
            BindingAction action,
            HashMap<Type, List<WeakActionAndToken>> lists) {
        Type messageType = NotMsgType.class;

        if (recipient == null
                || lists == null
                || lists.size() == 0
                || !lists.containsKey(messageType)) {
            return;
        }

        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(messageType)) {
                WeakAction weakActionCasted = (WeakAction) item.getAction();

                if (weakActionCasted != null
                        && recipient == weakActionCasted.getTarget()
                        && (action == null
                        || action == weakActionCasted.getBindingAction())
                        && (token == null
                        || token.equals(item.getToken()))) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }

    private static boolean classImplements(Type instanceType, Type interfaceType) {
        if (interfaceType == null
                || instanceType == null) {
            return false;
        }
        Class[] interfaces = ((Class) instanceType).getInterfaces();
        for (Class currentInterface : interfaces) {
            if (currentInterface == interfaceType) {
                return true;
            }
        }

        return false;
    }

    private static void cleanupList(HashMap<Type, List<WeakActionAndToken>> lists) {
        if (lists == null) {
            return;
        }
        for (Iterator it = lists.entrySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            List<WeakActionAndToken> itemList = lists.get(key);
            if (itemList != null) {
                for (WeakActionAndToken item : itemList) {
                    if (item.getAction() == null
                            || !item.getAction().isLive()) {
                        itemList.remove(item);
                    }
                }
                if (itemList.size() == 0) {
                    lists.remove(key);
                }
            }
        }
    }

    private void sendToTargetOrType(Type messageTargetType, Object token) {
        Class messageType = NotMsgType.class;
        if (mRecipientsOfSubclassesAction != null) {
            // Clone to protect from people registering in a "receive message" method
            // Bug correction Messaging BL0008.002
//            var listClone = recipientsOfSubclassesAction.Keys.Take(_recipientsOfSubclassesAction.Count()).ToList();
            List<Type> listClone = new ArrayList<>();
            listClone.addAll(mRecipientsOfSubclassesAction.keySet());
            for (Type type : listClone) {
                List<WeakActionAndToken> list = null;

                if (messageType == type
                        || ((Class) type).isAssignableFrom(messageType)
                        || classImplements(messageType, type)) {
                    list = mRecipientsOfSubclassesAction.get(type);
                }

                sendToList(list, messageTargetType, token);
            }
        }

        if (mRecipientsStrictAction != null) {
            if (mRecipientsStrictAction.containsKey(messageType)) {
                List<WeakActionAndToken> list = mRecipientsStrictAction.get(messageType);
                sendToList(list, messageTargetType, token);
            }
        }

        cleanup();
    }

    private static void sendToList(
            Collection<WeakActionAndToken> list,
            Type messageTargetType,
            Object token) {
        if (list != null) {
            // Clone to protect from people registering in a "receive message" method
            // Bug correction Messaging BL0004.007
            ArrayList<WeakActionAndToken> listClone = new ArrayList<>();
            listClone.addAll(list);

            for (WeakActionAndToken item : listClone) {
                WeakAction executeAction = item.getAction();
                if (executeAction != null
                        && item.getAction().isLive()
                        && item.getAction().getTarget() != null
                        && (messageTargetType == null
                        || item.getAction().getTarget().getClass() == messageTargetType
                        || classImplements(item.getAction().getTarget().getClass(), messageTargetType))
                        && ((item.getToken() == null && token == null)
                        || item.getToken() != null && item.getToken().equals(token))) {
                    executeAction.execute();
                }
            }
        }
    }

    private <T> void sendToTargetOrType(T message, Type messageTargetType, Object token) {
        Class messageType = message.getClass();


        if (mRecipientsOfSubclassesAction != null) {
            // Clone to protect from people registering in a "receive message" method
            // Bug correction Messaging BL0008.002
//            var listClone = recipientsOfSubclassesAction.Keys.Take(_recipientsOfSubclassesAction.Count()).ToList();
            List<Type> listClone = new ArrayList<>();
            listClone.addAll(mRecipientsOfSubclassesAction.keySet());
            for (Type type : listClone) {
                List<WeakActionAndToken> list = null;

                if (messageType == type
                        || ((Class) type).isAssignableFrom(messageType)
                        || classImplements(messageType, type)) {
                    list = mRecipientsOfSubclassesAction.get(type);
                }

                sendToList(message, list, messageTargetType, token);
            }
        }

        if (mRecipientsStrictAction != null) {
            if (mRecipientsStrictAction.containsKey(messageType)) {
                List<WeakActionAndToken> list = mRecipientsStrictAction.get(messageType);
                sendToList(message, list, messageTargetType, token);
            }
        }

        cleanup();
    }

    private class WeakActionAndToken {
        private WeakAction action;
        private Object token;

        public WeakActionAndToken(WeakAction action, Object token) {
            this.action = action;
            this.token = token;
        }

        public WeakAction getAction() {
            return action;
        }

        public void setAction(WeakAction action) {
            this.action = action;
        }

        public Object getToken() {
            return token;
        }

        public void setToken(Object token) {
            this.token = token;
        }
    }

    public static class NotMsgType {

    }
}
