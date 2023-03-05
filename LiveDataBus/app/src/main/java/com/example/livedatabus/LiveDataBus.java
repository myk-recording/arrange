package com.example.livedatabus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public final class LiveDataBus {

    private final Map<String, BusMutableLiveData<Object>> bus;

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveDataBus DEFAULT_BUS = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return SingletonHolder.DEFAULT_BUS;
    }

    public synchronized <T> MutableLiveData<T> with(String key,Class<T> type) {
        if (!bus.containsKey(key)) {
            bus.put(key,new BusMutableLiveData<Object>());
        }
        return (MutableLiveData<T>) bus.get(key);
    }

    public static class BusMutableLiveData<T> extends MutableLiveData<T> {
        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super.observe(owner, observer);
            try {
                hook((Observer<T>) observer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         *  observe.mLastVersion = mVersion
         * @param observer
         */
        private void hook(Observer<T> observer) throws Exception {
            Class<LiveData> liveDataClass = LiveData.class;
            Field observersFile = liveDataClass.getDeclaredField("mObservers");
            observersFile.setAccessible(true);
            Object observersObject = observersFile.get(this);
            Class<?> observersClass = observersObject.getClass();
            //  获取map对象的get方法
            Method getMethod = observersClass.getDeclaredMethod("get",Object.class);
            getMethod.setAccessible(true);
            Object wrapperEntryObject = getMethod.invoke(observersObject,observer);
            Object wrapperObject = null;
            if (wrapperEntryObject instanceof Map.Entry) {
                //  LifecycleBoundObserver
                wrapperObject = ((Map.Entry<?, ?>) wrapperEntryObject).getValue();
            }
            if (wrapperObject == null) {
                throw new NullPointerException("Wrapper is null");
            }
            Class<?> observerWrapperClass = wrapperObject.getClass().getSuperclass();
            Field lastVersionField = observerWrapperClass.getDeclaredField("mLastVersion");
            lastVersionField.setAccessible(true);
            Field versionField = liveDataClass.getDeclaredField("mVersion");
            versionField.setAccessible(true);
            Object versionObject = versionField.get(this);
            lastVersionField.set(wrapperObject,versionObject);
        }
    }

}
