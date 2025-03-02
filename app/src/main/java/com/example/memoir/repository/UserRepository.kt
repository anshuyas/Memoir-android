package com.example.memoir.repository

interface UserRepository {
    fun login(email:String,password:String,callback:(Boolean,String)->Unit)

    fun signup(email:String,password:String,callback:(Boolean,String,String)->Unit)

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit)
}