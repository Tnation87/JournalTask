package com.toka.legendarynews;

import android.util.Log;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel implements Observable {

    private static final String TAG = ViewModel.class.getSimpleName();
    private String username;
    private String password;
    private String error;
    private final MutableLiveData<Status> status = new MutableLiveData<>(Status.IDLE);

    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    @Override
    public void addOnPropertyChangedCallback(
            Observable.OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(
            Observable.OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }

    public void onLogin(LifecycleOwner lifecycleOwner) {
        Log.d(TAG, "onLogin");
        status.setValue(Status.LOADING);
        Repo.login(username, password).observe(lifecycleOwner, task -> {
            if (task.isSuccessful())
                status.postValue(Status.SUCCESS);
            else {
                Exception exception = task.getException();
                error = exception != null ? exception.getLocalizedMessage() : null;
                status.postValue(Status.ERROR);
            }
        });
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setStatus(Status status) {
        this.status.setValue(status);
    }

    public MutableLiveData<Status> getStatus() {
        return status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
