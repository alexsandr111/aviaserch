package com.example.demo1111;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/blog")
public class BlogController {
    private final BlogPostService blogPostService;
    private final UserService userService;
    private final CargoService cargoService;

    public BlogController(BlogPostService blogPostService, UserService userService, CargoService cargoService) {
        this.blogPostService = blogPostService;
        this.userService = userService;
        this.cargoService = cargoService;
    }

    // Метод для поиска записей
    @GetMapping("/search")
    public String searchBlogPosts(
            @RequestParam String query,
            @RequestParam String criteria,
            Model model) {
        List<BlogPost> searchResults = blogPostService.searchBlogPosts(query, criteria);
        model.addAttribute("posts", searchResults);
        model.addAttribute("criteria", criteria);
        model.addAttribute("query", query);
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "blog/main";  // Возвращаем главную страницу блога с результатами поиска
    }

    // Главная страница блога
    @GetMapping
    public String showBlogMainPage(Model model) {
        model.addAttribute("posts", blogPostService.getAllPosts());
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "blog/main";
    }

    @GetMapping("/admin")
    public String showAdminPanel(Model model) {
        User currentUser = UserContext.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.getRole().equals("ADMIN")) {
            return "redirect:/blog"; // Перенаправление, если пользователь не администратор
        }

        List<User> users = userService.findAllUsers(); // Получаем список всех пользователей
        model.addAttribute("users", users);
        model.addAttribute("posts", blogPostService.getAllPosts());
        model.addAttribute("user", currentUser);




        List<Cargo> goodsList = cargoService.listAll(null);

        Map<LocalDateTime, Integer> dateMap = new HashMap<>();

        for (Cargo goods : goodsList) {
            LocalDateTime dateGood = goods.getDepartureDateTime();
            dateMap.put(dateGood, dateMap.getOrDefault(dateGood, 0) + 1);
        }

        List<List<Object>> dateCountMap = new ArrayList<>();

        for (Map.Entry<LocalDateTime, Integer> entry : dateMap.entrySet()) {
            List<Object> subList = new ArrayList<>();
            subList.add(entry.getKey());
            subList.add(entry.getValue());
            dateCountMap.add(subList);}
        model.addAttribute("chartData", dateCountMap);
            return "blog/admin"; // Возвращаем страницу админа
    }

    @PostMapping("/admin/changeRole")
    public String changeUserRole(@RequestParam Long userId, @RequestParam String newRole, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.findUserById(userId);

        if (user.isEmpty()) {
            // Добавляем сообщение об ошибке в атрибуты редиректа
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
            return "redirect:/blog/admin"; // Редирект с ошибкой
        }

        user.get().setRole(newRole);
        userService.saveUser(user.get()); // Сохранение изменений в базе данных

        // Добавляем сообщение об успешном изменении роли
        redirectAttributes.addFlashAttribute("successMessage", "Роль успешно изменена");

        return "redirect:/blog/admin"; // Редирект на страницу /blog/admin
    }




    @RequestMapping("/newPost")
    public String showNewPostForm(Model model) {
        BlogPost post = new BlogPost();
        model.addAttribute("post", post);
        return "new_post";  // Страница для добавления поста
    }

    @RequestMapping(value = "/savePost", method = RequestMethod.POST)
    public ResponseEntity<Object> savePost(@RequestBody BlogPost post) {
        User currentUser = UserContext.getInstance().getCurrentUser(); // Получаем текущего пользователя
        if (currentUser != null) {
            post.setAuthor(currentUser.getUsername()); // Устанавливаем автора
        }
        if (post.getDate() == null) {
            post.setDate(LocalDate.now()); // Устанавливаем текущую дату, если она не указана
        }
        blogPostService.savePost(post); // Сохраняем пост
        return ResponseEntity.ok().build(); // Возвращаем успешный ответ
    }

    @GetMapping("/editPost/{id}")
    @ResponseBody
    public ResponseEntity<BlogPost> getPostById(@PathVariable Long id) {
        BlogPost post = blogPostService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }
        return ResponseEntity.ok(post);  // 200 OK с данными поста
    }

    @RequestMapping("/deletePost/{id}")
    public String deletePost(@PathVariable(name = "id") Long id) {
        blogPostService.deletePost(id);
        return "redirect:/blog";  // Перенаправление на главную страницу блога после удаления
    }
}
