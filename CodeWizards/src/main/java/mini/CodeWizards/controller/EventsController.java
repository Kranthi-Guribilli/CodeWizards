package mini.CodeWizards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("public")
public class EventsController {
    @RequestMapping(value={"/funcoding"})
        public String displayFuncodingPage(){
            return "funcoding.html";
    }
    @RequestMapping(value={"/bootcamps"})
    public String displayBootcamps(){
        return "bootcamps.html";}
}
