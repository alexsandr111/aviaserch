package com.example.demo1111;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Optional;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String index() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {

        Optional<User> user = userService.findUserByUsername(username);

        if (user.isPresent()) {
            if (BCrypt.checkpw(password, user.get().getPassword())) {
                UserContext.getInstance().setCurrentUser(user.get());
                return "redirect:/";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("username")) {
            model.addAttribute("username", "");
        }
        if (!model.containsAttribute("email")) {
            model.addAttribute("email", "");
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email,
                               Model model) {

        Optional<User> userByEmail = userService.findByEmail(email);

        Optional<User> userByUsername = userService.findByUsername(username);

        if (userByUsername.isPresent()) {
            model.addAttribute("username", "Имя уже занято");
            return "register";
        }

        if (userByEmail.isPresent()) {
            model.addAttribute("email", "Email уже используется");
            return "register";
        }

        userService.saveUser(new User(username, password, email));
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout() {
        UserContext.getInstance().clearCurrentUser();
        return "redirect:/";
    }

    @GetMapping("/profile/{id}")public String getUserProfile(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);    model.addAttribute("user", user);
        return "profile";
    }
    @GetMapping("/edit_profile")public String editProfile(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);    return "redirect:/profile/" + id;
    }

    @PostMapping("/edit_profile/{id}")public String updateProfile(@PathVariable("id") Long id,
                                                                  @RequestParam("username") String username,
                                                                  @RequestParam("email") String email,
                                                                  @RequestParam("password") String password,
                                                                  @RequestParam("confirmPassword") String confirmPassword,
                                                                  Model model) {
        if (!password.equals(confirmPassword)) {        model.addAttribute("error", "Пароли не совпадают");
            model.addAttribute("user", userService.getUserById(id)); // добавляем пользователя в модель        return "redirect:/profile/" + id;
        }
        User user = userService.getUserById(id);    if (user != null) {
            user.setUsername(username);
            user.setEmail(email);
            if (!password.isEmpty()) {
                user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));        }
            userService.saveUser(user);
        }
        return "redirect:/profile/" + id;}

    @PostMapping("/change-role")
    public String changeUserRole(@RequestParam("role") String role, @RequestParam("id") Long id) {
        userService.updateUserRole(id, role);
        return "redirect:/profile/" + id; // Перенаправление на профиль пользователя
    }




}
