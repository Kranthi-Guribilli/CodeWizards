package mini.CodeWizards.controller;


import lombok.extern.slf4j.Slf4j;
import mini.CodeWizards.model.Challenges;
import mini.CodeWizards.model.Courses;
import mini.CodeWizards.model.Person;
import mini.CodeWizards.model.WizardsClass;
import mini.CodeWizards.repository.ChallengesRepository;
import mini.CodeWizards.repository.CoursesRepository;
import mini.CodeWizards.repository.PersonRepository;
import mini.CodeWizards.repository.WizardsClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    WizardsClassRepository wizardsClassRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    CoursesRepository coursesRepository;
    @Autowired
    ChallengesRepository challengesRepository;

    @RequestMapping("/displayClasses")
    public ModelAndView displayClasses(Model model) {
        List<WizardsClass> wizardsClasses= wizardsClassRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("classes.html");
        modelAndView.addObject("wizardsClasses",wizardsClasses);
        modelAndView.addObject("wizardsClass", new WizardsClass());
        return modelAndView;
    }

    @PostMapping("/addNewClass")
    public ModelAndView addNewClass(Model model, @ModelAttribute("wizardsClass") WizardsClass wizardsClass) {
        wizardsClassRepository.save(wizardsClass);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/displayClasses");
        return modelAndView;
    }

    @RequestMapping("/deleteClass")
    public ModelAndView deleteClass(Model model, @RequestParam int id) {
        Optional<WizardsClass> wizardsClass = wizardsClassRepository.findById(id);
        for(Person person : wizardsClass.get().getPersons()){
            person.setWizardsClass(null);
            personRepository.save(person);
        }
        wizardsClassRepository.deleteById(id);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/displayClasses");
        return modelAndView;
    }

    @GetMapping("/displayStudents")
    public ModelAndView displayStudents(Model model, @RequestParam int classId, HttpSession session,
                                        @RequestParam(value = "error", required = false) String error) {
        String errorMessage = null;
        ModelAndView modelAndView = new ModelAndView("students.html");
        Optional<WizardsClass> wizardsClass = wizardsClassRepository.findById(classId);
        modelAndView.addObject("wizardsClass",wizardsClass.get());
        modelAndView.addObject("person",new Person());
        session.setAttribute("wizardsClass",wizardsClass.get());
        if(error != null) {
            errorMessage = "Invalid Email entered!!";
            modelAndView.addObject("errorMessage", errorMessage);
        }
        return modelAndView;
    }

    @PostMapping("/addStudent")
    public ModelAndView addStudent(Model model, @ModelAttribute("person") Person person, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        WizardsClass wizardsClass = (WizardsClass) session.getAttribute("wizardsClass");
        Person personEntity = personRepository.readByEmail(person.getEmail());
        if(personEntity==null || !(personEntity.getPersonId()>0)){
            modelAndView.setViewName("redirect:/admin/displayStudents?classId="+wizardsClass.getClassId()
                    +"&error=true");
            return modelAndView;
        }
        personEntity.setWizardsClass(wizardsClass);
        personRepository.save(personEntity);
        wizardsClass.getPersons().add(personEntity);
        wizardsClassRepository.save(wizardsClass);
        modelAndView.setViewName("redirect:/admin/displayStudents?classId="+wizardsClass.getClassId());
        return modelAndView;
    }

    @GetMapping("/deleteStudent")
    public ModelAndView deleteStudent(Model model, @RequestParam int personId, HttpSession session) {
        WizardsClass wizardsClass = (WizardsClass) session.getAttribute("wizardsClass");
        Optional<Person> person = personRepository.findById(personId);
        person.get().setWizardsClass(null);
        wizardsClass.getPersons().remove(person.get());
        WizardsClass wizardsClassSaved = wizardsClassRepository.save(wizardsClass);
        session.setAttribute("wizardsClass",wizardsClassSaved);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/displayStudents?classId="+wizardsClass.getClassId());
        return modelAndView;
    }

    @GetMapping("/displayCourses")
    public ModelAndView displayCourses(Model model) {
        List<Courses> courses = coursesRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("courses_secure.html");
        modelAndView.addObject("courses",courses);
        modelAndView.addObject("course", new Courses());
        return modelAndView;
    }

    @PostMapping("/addNewCourse")
    public ModelAndView addNewCourse(Model model, @ModelAttribute("course") Courses course) {
        ModelAndView modelAndView = new ModelAndView();
        coursesRepository.save(course);
        modelAndView.setViewName("redirect:/admin/displayCourses");
        return modelAndView;
    }

    @GetMapping("/viewStudents")
    public ModelAndView viewStudents(Model model, @RequestParam int id
            ,HttpSession session,@RequestParam(required = false) String error) {
        String errorMessage = null;
        ModelAndView modelAndView = new ModelAndView("course_students.html");
        Optional<Courses> courses = coursesRepository.findById(id);
        modelAndView.addObject("courses",courses.get());
        modelAndView.addObject("person",new Person());
        session.setAttribute("courses",courses.get());
        if(error != null) {
            errorMessage = "Invalid Email entered!!";
            modelAndView.addObject("errorMessage", errorMessage);
        }
        return modelAndView;
    }

    @PostMapping("/addStudentToCourse")
    public ModelAndView addStudentToCourse(Model model, @ModelAttribute("person") Person person,
                                           HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Courses courses = (Courses) session.getAttribute("courses");
        Person personEntity = personRepository.readByEmail(person.getEmail());
        if(personEntity==null || !(personEntity.getPersonId()>0)){
            modelAndView.setViewName("redirect:/admin/viewStudents?id="+courses.getCourseId()
                    +"&error=true");
            return modelAndView;
        }
        personEntity.getCourses().add(courses);
        courses.getPersons().add(personEntity);
        personRepository.save(personEntity);
        session.setAttribute("courses",courses);
        modelAndView.setViewName("redirect:/admin/viewStudents?id="+courses.getCourseId());
        return modelAndView;
    }

    @GetMapping("/deleteStudentFromCourse")
    public ModelAndView deleteStudentFromCourse(Model model, @RequestParam int personId,
                                                HttpSession session) {
        Courses courses = (Courses) session.getAttribute("courses");
        Optional<Person> person = personRepository.findById(personId);
        person.get().getCourses().remove(courses);
        courses.getPersons().remove(person);
        personRepository.save(person.get());
        session.setAttribute("courses",courses);
        ModelAndView modelAndView = new
                ModelAndView("redirect:/admin/viewStudents?id="+courses.getCourseId());
        return modelAndView;
    }

    @GetMapping("/displayChallenges")
    public ModelAndView displayChallenges(Model model) {
        List<Challenges> challenges = challengesRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("challenges.html");
        modelAndView.addObject("challenges",challenges);
        modelAndView.addObject("challenge", new Challenges());
        return modelAndView;
    }
    @PostMapping("/addNewChallenge")
    public ModelAndView addNewChallenge(Model model, @ModelAttribute("challenge") Challenges challenge) {
        ModelAndView modelAndView = new ModelAndView();
        challengesRepository.save(challenge);
        modelAndView.setViewName("redirect:/admin/displayChallenges");
        return modelAndView;
    }
}