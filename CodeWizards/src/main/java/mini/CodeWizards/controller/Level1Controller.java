package mini.CodeWizards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Level1Controller {
    @RequestMapping(value={"/level1"})
        public String displayLevel1Page(){
            return "level1.html";}
}
