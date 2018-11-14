package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/login")
    public String showlogin() {
        return "login";
    }

    @GetMapping("/register")
    public String showregistration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        model.addAttribute("user", user);
        if (result.hasErrors()) {
            return "registration";
        } else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "list";
    }

    @RequestMapping("/")
    public String listDogs(Model model) {
        model.addAttribute("dogs", dogRepository.findAll());
        return "list";
    }

    @RequestMapping("/all")
    public String allDogs(Model model) {
        model.addAttribute("dogs", dogRepository.findAll());
        return "list";
    }

    @RequestMapping("/lost")
    public String lostDogs(Model model) {
        String status = "lost";
        ArrayList<Dog> results = (ArrayList<Dog>)
                dogRepository.findAllByStatusContainingIgnoreCase(status);

        model.addAttribute("results", results);
        return "lostdogs";
    }

    @RequestMapping("/found")
    public String foundDogs(Model model) {
        String status = "found";
        ArrayList<Dog> results = (ArrayList<Dog>)
                dogRepository.findAllByStatusContainingIgnoreCase(status);

        model.addAttribute("results", results);
        return "founddogs";
    }



    @GetMapping("/add")
    public String dogForm(Model model) {
        model.addAttribute("dog", new Dog());
        return "dogform";
    }

    @PostMapping("/process")
    public String processDog(@ModelAttribute Dog dog,
                             @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/add";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            dog.setPic(uploadResult.get("url").toString());
            dogRepository.save(dog);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }
        return "redirect:/";

    }

//    public String processForm(@Valid Dog dog,
//                              BindingResult result){
//
//        dogRepository.save(dog);
//        return "redirect:/";

    @RequestMapping("/detail/{id}")
    public String showDog(@PathVariable("id") long id, Model model) {
        model.addAttribute("dog", dogRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateDog(@PathVariable("id") long id, Model model) {
        model.addAttribute("dog", dogRepository.findById(id).get());
        return "dogform";
    }
    @RequestMapping("/delete/{id}")
    public String delDog(@PathVariable("id") long id){
        dogRepository.deleteById(id);
        return "redirect:/";


    }

}
