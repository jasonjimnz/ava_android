package com.jjdev.ava

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.github.kittinunf.fuel.android.extension.responseJson

class RegisterActivity : AppCompatActivity() {
    private var backBtn: Button? = null
    private var registerBtn: Button? = null
    private var api: Api = Api()
    private var emailField: EditText? = null
    private var passwordField: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        backBtn = findViewById(R.id.button6)
        registerBtn = findViewById(R.id.button7)
        emailField = findViewById(R.id.editText3)
        passwordField = findViewById(R.id.editText4)

        backBtn?.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        registerBtn?.setOnClickListener {
            // TODO: Llamar al registro
            api.register(emailField?.text.toString(), passwordField?.text.toString(), "user").responseJson { _, _, result ->
                val (bytes, error) = result
                if (bytes != null){
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                if (error != null){

                }
            }

        }
    }

}
