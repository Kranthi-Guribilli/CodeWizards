package mini.CodeWizards.controller;

import mini.CodeWizards.model.Contact;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

public class ContactController {
    @RequestMapping("/contact")
    public String displayContactPage(Model model) {
        model.addAttribute("contact", new Contact());
        return "contact.html";
    }
}

