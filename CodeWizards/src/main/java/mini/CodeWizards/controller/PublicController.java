package mini.CodeWizards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PublicController {
    @RequestMapping(value="/register")
    public String displayRegisterPage(){return "register.html";}
}
