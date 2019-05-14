package datamodels

import datamodels.general.StatusCode
import datamodels.users.User
import io.javalin.Context
import io.javalin.Handler
import io.javalin.apibuilder.CrudHandler

class SignInController(val DS: DataService){

    fun postSignIn(ctx : Context){
        val req : Map<String, Any> = ctx.body<Map<String, Any>>()
        val user : User? = DS.users.userLogin(
            req["username"].toString(),
            req["password"].toString()
        )
        val res : StatusCode
        if(user == null){
            res = StatusCode(false, "No Username Found", null)
        }
        else{
            res = StatusCode(true, "Username ${user.username} Login Successful", user.username)
            ctx.sessionAttribute("username", user.id.value)
        }
        ctx.json(res)
    }

}