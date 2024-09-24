package com.example.cow_cow.services

import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import androidx.car.app.model.MessageTemplate
import androidx.car.app.CarContext
import android.content.Intent
import android.util.Log
import com.example.cow_cow.car.MySession

class MyAutoService : CarAppService() {

    private val TAG = "MyAutoService"

    // Implement createHostValidator method
    override fun createHostValidator(): HostValidator {
        Log.d(TAG, "createHostValidator Called")
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        Log.d(TAG, "onCreateSession Called")
        return MySession() // This should point to the session with game logic
    }
}
