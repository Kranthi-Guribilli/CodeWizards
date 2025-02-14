package mini.CodeWizards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class HomeController {
    @RequestMapping(value={"", "/","/public/home","home"})
    public String displayHomePage(){
        return "home.html";
    }

    @RequestMapping(value={"/privacy","/public/privacy"})
    public String displayPrivacyPage(){return "privacy.html";}

    @RequestMapping(value={"/termsncond","/public/termsncond"})
    public String displayTermsPage(){return "T&C.html";}
}
