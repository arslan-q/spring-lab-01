package kz.iitu.springlab.web;

import kz.iitu.springlab.security.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private final RegistrationService registration;

    public LoginController(RegistrationService registration) {
        this.registration = registration;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, Model model) {
        if (username.isBlank() || username.length() > 60 || password.length() < 8) {
            model.addAttribute("error", "Use a username of 1–60 characters and a password of at least 8 characters.");
            model.addAttribute("username", username);
            return "register";
        }
        if (!registration.register(username.trim(), password)) {
            model.addAttribute("error", "That username is already registered.");
            model.addAttribute("username", username);
            return "register";
        }
        return "redirect:/login?registered";
    }
}
