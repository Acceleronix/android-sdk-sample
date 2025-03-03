package com.acceleronix.app.demo.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acceleronix.app.demo.databinding.ActivityAuthCodeLoginBinding
import com.quectel.app.usersdk.QuecResultCallback
import com.quectel.app.usersdk.userservice.IUserService
import com.quectel.app.usersdk.utils.UserServiceFactory
import com.quectel.basic.common.entity.QuecResult

class AuthCodeLoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuthCodeLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthCodeLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvLogin.setOnClickListener {
            val text = binding.editCode.text;
            if (text.isNullOrEmpty()) {
                Toast.makeText(this, "please enter you code ", Toast.LENGTH_SHORT).show()
            } else {
                val repo = Repo();
                repo.login(text.toString(), object : QuecResultCallback<QuecResult<String?>> {
                    override fun onSuccess(result: QuecResult<String?>) {

                        Toast.makeText(
                            this@AuthCodeLoginActivity,
                            "success",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    override fun onFail(p0: QuecResult<String?>) {
                        Toast.makeText(
                            this@AuthCodeLoginActivity,
                            "fail ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }
}


class Repo() {
    fun login(auth: String, resultCallback: QuecResultCallback<QuecResult<String?>>) {
        UserServiceFactory.getInstance().getService(IUserService::class.java)
            .loginByAuthCode(
                auth,
                object : QuecResultCallback<QuecResult<String?>> {
                    override fun onSuccess(successResult: QuecResult<String?>) {
                        resultCallback.onSuccess(successResult)
                    }

                    override fun onFail(failResult: QuecResult<String?>) {
                        if (null == failResult.msg) {
                            Log.e("loginAuth fail", failResult.data.toString());
                        } else {
                            Log.e("loginAuth fail", failResult.msg);
                        }
                        resultCallback.onFail(failResult)
                    }
                })
    }
}



