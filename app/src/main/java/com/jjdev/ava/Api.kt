package com.jjdev.ava

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request

class Api {
    val BASE_HOST = "http://192.168.137.1:5000" // YOUR CONNECTION TO THE BACKEND
    val ROUTES = hashMapOf(
            "send_text" to "/talk_to_bot",
            "login" to "/login",
            "register" to "/register"
    )

    fun sendText(query: String, token: String? = null, lat: String? = null, lon: String? = null) : Request{
        //val path = "$BASE_HOST${ROUTES["send_text"]}"
        val path = "${BASE_HOST}/talk_to_bot"
        println("Calling to: $path")
        return Fuel.post(
                path,
                listOf("text" to query, "token" to token, "lat" to lat, "lon" to lon)
        )
    }

    fun login(email: String, password: String) : Request{
        val path = "${BASE_HOST}/login"
        return Fuel.post(
                path,
                listOf("email" to email, "password" to password)
        )
    }

    fun register(email: String, password: String, role: String) : Request {
        val path = "${BASE_HOST}/register"
        return Fuel.post(
                path,
                listOf("email" to email, "password" to password, "role" to role)
        )
    }

    fun petitionList(token: String, lat: String, lon: String) : Request {
        val path = "${BASE_HOST}/request/list"
        return Fuel.post(
                path,
                listOf("token" to token, "lat" to lat,"lon" to lon)
        )
    }
}