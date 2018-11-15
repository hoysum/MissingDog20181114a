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
    private BullRepository bullRepository;

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
    public String listBulls(Model model) {
        model.addAttribute("bulls", bullRepository.findAll());
        return "list";
    }

    @RequestMapping("/all")
    public String allBulls(Model model) {
        model.addAttribute("bulls", bullRepository.findAll());
        return "list";
    }

    @RequestMapping("/followyou")
    public String hashBulls(Model model) {
        String status = "found";
        ArrayList<Bull> results = (ArrayList<Bull>)
                bullRepository.findAllByStatusContainingIgnoreCase(status);

        model.addAttribute("results", results);
        return "followyou";
    }

    @RequestMapping("/youfollow")
    public String foundBulls(Model model) {
        String status = "found";
        ArrayList<Bull> results = (ArrayList<Bull>)
                bullRepository.findAllByStatusContainingIgnoreCase(status);

        model.addAttribute("results", results);
        return "youfollow";
    }



    @GetMapping("/add")
    public String bullForm(Model model) {
        model.addAttribute("bull", new Bull());
        return "bullform";
    }

    @PostMapping("/process")
    public String processBull(@ModelAttribute Bull bull,
                             @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/add";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            bull.setPic(uploadResult.get("url").toString());
           bullRepository.save(bull);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }
        return "redirect:/";

    }

    @RequestMapping("/detail/{id}")
    public String showBull(@PathVariable("id") long id, Model model) {
        model.addAttribute("bull", bullRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateBull(@PathVariable("id") long id, Model model) {
        model.addAttribute("bull", bullRepository.findById(id).get());
        return "bullform";
    }
    @RequestMapping("/delete/{id}")
    public String delBull(@PathVariable("id") long id){
        bullRepository.deleteById(id);
        return "redirect:/";


    }

}
