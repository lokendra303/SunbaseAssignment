package com.ex.controller;

import com.ex.model.AuthRequest;
import com.ex.model.CreateCustomer;
import com.ex.model.Customer;
import com.ex.service.ApiService;
import com.ex.utils.UtilsMethod;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @Autowired
    ApiService apiService;
    @Autowired
    private UtilsMethod utilsMethod;
    private static final String SESSION_TOKEN_KEY = "token";


//    @RequestMapping("/*")
//    public String goToLogin1(HttpSession session) {
//        if (isLoggedIn(session))
//            return "redirect:/customer-list";
//        return "redirect:/";
//    }

    @GetMapping("/")
    public String goToLogin(HttpSession session) {
        if (isLoggedIn(session))
            return "redirect:/customer-list";
        return "login";
    }

    @PostMapping("/login")
    public String submitForm(@RequestParam String login_id,
                             @RequestParam String password,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {

        AuthRequest user = new AuthRequest();
        user.setLogin_id(login_id);
        user.setPassword(password);

        boolean verify = utilsMethod.verify(login_id, password);
        if (verify) {
            String token = utilsMethod.generateToken(login_id, password);
            session.setAttribute("token", token);

//            List<User> userList = apiService.getUserList(session);        //get date
//            session.setAttribute("userList", userList);

            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/customer-list";

        } else {
            model.addAttribute("error", "Invalid Credentials");
            return "login";
        }
    }


    @GetMapping("/customer-list")
    public String success(HttpSession session, Model model) {
        if (!isLoggedIn(session))
            return "redirect:/";
        return "customer-list";
    }

    private boolean isLoggedIn(HttpSession session) {
        String token = (String) session.getAttribute("token");

        System.out.println("Session " + token);
        return token != null && !token.isEmpty();
    }

    @ResponseBody
    @GetMapping("/logout")
    private void logout(HttpSession session) {
        if (isLoggedIn(session)) {
            session.removeAttribute("token");
        }

    }

    @GetMapping("/deleteCustomer")
    public String delete(HttpSession session, RedirectAttributes model, @RequestParam("uuid") String uuid) {
//        System.out.println("uuid" + uuid);
        boolean delete = apiService.delete(session, uuid);
        if (delete) {
            model.addFlashAttribute("msg", "delete success");
        } else {
            model.addFlashAttribute("msg", "delete fail");
        }

        return "redirect:/customer-list";
    }

    @GetMapping("/customer")
    public String addCustomer(HttpSession session,
                              Model model) {
        return "add-customer";
    }

    @PostMapping("/customer")
    public String addCustomerTo(
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam String street,
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String email,
            @RequestParam String phone,
            HttpSession session,
            Model model) {
        CreateCustomer customer = new CreateCustomer(first_name, last_name, street, address, city, state, email, phone);

        System.out.println("Received CreateCustomer: " + customer);
        boolean add = apiService.add(session, customer);
        if (add) {
            model.addAttribute("msg", "add success");

        } else {
            model.addAttribute("msg", "add fail");
        }
        return "add-customer";
    }


    @GetMapping("/updateCustomer")
    public String updatePage(@RequestParam String uuid,
                             @RequestParam String first_name,
                             @RequestParam String last_name,
                             @RequestParam String street,
                             @RequestParam String address,
                             @RequestParam String city,
                             @RequestParam String state,
                             @RequestParam String email,
                             @RequestParam String phone,
                             HttpSession session,
                             Model model) {
        session.setAttribute("uuid", uuid);
        Customer customer = new Customer(uuid.trim(), first_name.trim(), last_name.trim(), street.trim(),
                address.trim(), city.trim(), state.trim(), email.trim(), phone.trim());
        System.out.println("customer for update " + customer);
        model.addAttribute("customer", customer);
        return "update-customer";
    }

    @PostMapping("/updateCustomer")
    public String updateCustomerTo
            (
                    @RequestParam String first_name,
                    @RequestParam String last_name,
                    @RequestParam String street,
                    @RequestParam String address,
                    @RequestParam String city,
                    @RequestParam String state,
                    @RequestParam String email,
                    @RequestParam String phone,
                    HttpSession session,
                    RedirectAttributes redirectAttributes

            ) {
        String uuid = (String) session.getAttribute("uuid");
        System.out.println("uuid" + uuid);
        CreateCustomer createCustomer = new CreateCustomer(first_name, last_name, street, address, city, state, email, phone);
        System.out.println("Received CreateCustomer: " + createCustomer);
        boolean updated = apiService.update(session, uuid, createCustomer);

        if (updated) {
            session.removeAttribute("uuid");
            redirectAttributes.addFlashAttribute("msg", "update success");

            return "redirect:/customer-list";
        }
        redirectAttributes.addFlashAttribute("msg", "update fail");
        return "update-customer";
    }

}

