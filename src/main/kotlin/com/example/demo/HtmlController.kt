package com.example.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.security.MessageDigest
import javax.servlet.http.HttpSession

@Controller
class HtmlController {

    @Autowired
    lateinit var repository: UserRepository

    @GetMapping("/")
    fun index(model : Model) : String {
        model.addAttribute("title", "Home")

        return "index"
    }

    fun crypto(ss : String) : String {
        val sha = MessageDigest.getInstance("SHA-256")
        val hexa = sha.digest(ss.toByteArray())
        val crypto_str = hexa.fold("", {str, it -> str + "%02x".format(it)})

        return crypto_str
    }

    @GetMapping("/{formType}")
    fun htmlForm(model: Model, @PathVariable formType : String) : String {
        model.addAttribute("title", formType)

        return formType
    }

    @PostMapping("/sign")
    fun postSign(
            model: Model,
            @RequestParam(value = "id") userId : String,
            @RequestParam(value = "password") password : String) : String {

        try {
            val cryptoPass = crypto(password)
            repository.save(User(userId, cryptoPass))
        } catch (e: Exception) { e.printStackTrace() }

        model.addAttribute("title", "sign success")

        return "login"
    }

    @PostMapping("/login")
    fun postLogin(
            model: Model,
            session : HttpSession,
            @RequestParam(value = "id") userId : String,
            @RequestParam(value = "password") password : String) : String {
        var ret : String = ""

        try {
            val cryptoPassword = crypto(password)
            val db_user = repository.findByUserId(userId)

            if (db_user != null) {
                val db_pass = db_user.password

                if(cryptoPassword.equals(db_pass)) {
                    session.setAttribute("userId", db_user.id)
                    model.addAttribute("title", "welcome")
                    model.addAttribute("userId", userId)

                    ret = "welcome"
                }
                else {
                    model.addAttribute("title", "login")

                    ret = "login"
                }
            }
        } catch (e : java.lang.Exception) { e.printStackTrace() }

        return ret
    }
}