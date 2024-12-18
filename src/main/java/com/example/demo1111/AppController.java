package com.example.demo1111;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
public class AppController {

    @Autowired
    private CargoService service;

    public AppController(CargoService cargoService) {
        this.service = cargoService;
    }

    @RequestMapping("/")
    public String viewHomePage(Model model, @Param("keyword") String keyword) {
        List<Cargo> listCargos = service.listAll(keyword);
        model.addAttribute("listCargos", listCargos);
        model.addAttribute("keyword", keyword);

        // Получаем текущего пользователя из UserContext и добавляем его в модель
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) {
            model.addAttribute("user", user);
            // Добавляем роль пользователя в модель
            model.addAttribute("isAdmin", user.getRole().equals("ADMIN"));
        }
        return "index";
    }


    @RequestMapping("/new")
    public String showNewCargoForm(Model model) {
        Cargo cargo = new Cargo();
        model.addAttribute("cargos", cargo);
        return "new_cargo";
    }

    @RequestMapping("/toabout")
    public String abobusabout() {
        return "about";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<Object> saveCargo(@RequestBody Cargo cargo) {
        service.save(cargo);
        return ResponseEntity.ok().build(); // Сообщаем о успешном выполнении
    }


    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseEntity<Cargo> getCargoById(@PathVariable Long id) {

        Cargo cargo = service.get(id);
        if (cargo == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(cargo); // 200 OK с данными книги
    }

    @RequestMapping("/delete/{id}")
    public String deleteCargo(@PathVariable(name = "id") Long id) {
        service.delete(id);
        return "redirect:/";
    }

    @GetMapping("/arrivalDateTime")
    @ResponseBody
    public Map<String, Integer> getCargoDate() {
        List<Cargo> cargos = service.listAll(null); // Получаем все книги
        Map<String, Integer> CargoDate = new HashMap<>();

        for (Cargo cargo : cargos) {
            if (cargo.getArrivalDateTime() != null) {
                String date = cargo.getArrivalDateTime().toString(); // Форматируем дату
                CargoDate.put(date, CargoDate.getOrDefault(date, 0) + 1); // Увеличиваем счетчик
            }
        }
        return CargoDate; // Возвращаем карту с данными
    }
}