package com.nongsan.controller;

import com.nongsan.entity.User;
import com.nongsan.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @PostMapping("/login")
    public String login(String username, String password, HttpSession session, Model model){

        User user = userRepository.findByUsername(username);

        if(user == null){
            model.addAttribute("error", "Sai tài khoản");
            return "login";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(!encoder.matches(password, user.getPassword())){
            model.addAttribute("error", "Sai mật khẩu");
            return "login";
        }

        session.setAttribute("user", user);

        if("ADMIN".equals(user.getRole())){
            return "redirect:/admin";
        }

        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @PostMapping("/register")
    public String register(User user){

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String encodedPassword = encoder.encode(user.getPassword());

        user.setPassword(encodedPassword);   // 🔥 thay password

        user.setRole("USER");


        if(userRepository.findByUsername(user.getUsername()) != null){
            return "redirect:/register?error";
        }

        userRepository.save(user);

        return "redirect:/login";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "redirect:/";
    }

}