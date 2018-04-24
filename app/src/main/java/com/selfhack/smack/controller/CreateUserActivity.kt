package com.selfhack.smack.controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.selfhack.smack.R
import com.selfhack.smack.services.AuthService
import com.selfhack.smack.utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5,0.5,0.5,1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.visibility = View.INVISIBLE
    }

    fun generateUserAvatar(view : View){
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if(color == 0){
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }

        val resoruceID = resources.getIdentifier(userAvatar, "drawable", packageName)
        createAvatarImage.setImageResource(resoruceID)
    }

    fun generateBackgroundColor(view: View){
        val random = Random()
        val r = random.nextInt(255)
        val b = random.nextInt(255)
        val g = random.nextInt(255)

        createAvatarImage.setBackgroundColor(Color.rgb(r,g,b))

        val savedR = r.toDouble()/255
        val savedB = b.toDouble()/255
        val savedG = g.toDouble()/255

        avatarColor = "[$savedR,$savedG,$savedB,1]"

    }

    fun createUserClicked(view: View){
        enableSpinner(true)
        val email = createEmailText.text.toString()
        val password = createPasswordText.text.toString()
        val userName = createUserNameText.text.toString()

        if(userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {

            AuthService.registerUser(email, password) { registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(email, password) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(userName, email, userAvatar, avatarColor) { userCreatseSuccess ->
                                if (userCreatseSuccess) {
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Make sure user name, email and password are filled in", Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }
    }

    private fun errorToast(){
        Toast.makeText(this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    private fun enableSpinner(enable : Boolean){
        if(enable){
            createSpinner.visibility = View.VISIBLE
        } else {
            createSpinner.visibility = View.INVISIBLE
        }

        createCreateUserBtn.isEnabled = !enable
        createAvatarImage.isEnabled = !enable
        createGenerateBackgroundBtn.isEnabled = !enable
    }
}
