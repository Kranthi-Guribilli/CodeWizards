package mini.CodeWizards.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import mini.CodeWizards.model.Challenges;
import mini.CodeWizards.model.Courses;
import mini.CodeWizards.model.Person;
import mini.CodeWizards.repository.ChallengesRepository;
import mini.CodeWizards.repository.CoursesRepository;
import mini.CodeWizards.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("student")
public class StudentController {
    @Autowired
    ChallengesRepository challengesRepository;
    @Autowired
    CoursesRepository coursesRepository;
    @Autowired
    PersonRepository personRepository;



    @GetMapping("/enrolledCourses")
    public ModelAndView enrolledCourses(Model model, HttpSession session) {
        Person person = (Person) session.getAttribute("loggedInPerson");
        ModelAndView modelAndView = new ModelAndView("courses_enrolled.html");
        modelAndView.addObject("person",person);
        return modelAndView;
    }

    @PostMapping("/registerToCourse")
    public ModelAndView registerToCourse(Model model, @ModelAttribute("person") Person person,
                                           HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Courses courses = (Courses) session.getAttribute("courses");
        Person personEntity = personRepository.readByEmail(person.getEmail());
        if(personEntity==null || !(personEntity.getPersonId()>0)){
            modelAndView.setViewName("redirect:/public/viewStudents?id="+courses.getCourseId()
                    +"&error=true");
            return modelAndView;
        }
        personEntity.getCourses().add(courses);
        courses.getPersons().add(personEntity);
        personRepository.save(personEntity);
        // Decrement the lmt field
        courses.setLmt(courses.getLmt() - 1);
        // Save the updated course entity
        coursesRepository.save(courses);
        session.setAttribute("courses",courses);
        modelAndView.setViewName("redirect:/public/viewStudents?id="+courses.getCourseId());
        return modelAndView;
    }

}
