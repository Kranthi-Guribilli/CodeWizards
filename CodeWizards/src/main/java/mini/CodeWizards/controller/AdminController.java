package mini.CodeWizards.controller;


import lombok.extern.slf4j.Slf4j;
import mini.CodeWizards.model.*;
import mini.CodeWizards.repository.*;
import mini.CodeWizards.service.PersonService;
import mini.CodeWizards.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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



    @PostMapping("/addNewClass")
    public ModelAndView addNewClass(Model model, @ModelAttribute("wizardsClass") WizardsClass wizardsClass) {
        wizardsClassRepository.save(wizardsClass);
        ModelAndView modelAndView = new ModelAndView("redirect:/public/displayClasses");
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
        ModelAndView modelAndView = new ModelAndView("redirect:/public/displayClasses");
        return modelAndView;
    }

    @PostMapping("/addStudent")
    public ModelAndView addStudent(Model model, @ModelAttribute("person") Person person, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        WizardsClass wizardsClass = (WizardsClass) session.getAttribute("wizardsClass");
        Person personEntity = personRepository.readByEmail(person.getEmail());
        if(personEntity==null || !(personEntity.getPersonId()>0)){
            modelAndView.setViewName("redirect:/public/displayStudents?classId="+wizardsClass.getClassId()
                    +"&error=true");
            return modelAndView;
        }
        personEntity.setWizardsClass(wizardsClass);
        personRepository.save(personEntity);
        wizardsClass.getPersons().add(personEntity);
        wizardsClassRepository.save(wizardsClass);
        modelAndView.setViewName("redirect:/public/displayStudents?classId="+wizardsClass.getClassId());
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
        ModelAndView modelAndView = new ModelAndView("redirect:/public/displayStudents?classId="+wizardsClass.getClassId());
        return modelAndView;
    }

    @PostMapping("/addNewCourse")
    public ModelAndView addNewCourse(Model model, @ModelAttribute("course") Courses course) {
        ModelAndView modelAndView = new ModelAndView();
        coursesRepository.save(course);
        modelAndView.setViewName("redirect:/public/displayCourses");
        return modelAndView;
    }

    @PostMapping("/addStudentToCourse")
    public ModelAndView addStudentToCourse(Model model, @ModelAttribute("person") Person person,
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
                ModelAndView("redirect:/public/viewStudents?id="+courses.getCourseId());
        return modelAndView;
    }


    @PostMapping("/addNewChallenge")
    public ModelAndView addNewChallenge(Model model, @ModelAttribute("challenge") Challenges challenge) {
        ModelAndView modelAndView = new ModelAndView();
        challengesRepository.save(challenge);
        modelAndView.setViewName("redirect:/public/displayChallenges");
        return modelAndView;
    }

    @RequestMapping(value = "/editChallenge", method= {RequestMethod.GET,RequestMethod.POST})
    public ModelAndView editChallenge(@RequestParam int challenge_id, @ModelAttribute("challenge") Challenges challenge) {
        // Retrieve the original challenge from the database based on the updatedChallenge's ID
        Optional<Challenges> optionalChallenge = challengesRepository.findById(challenge_id);
        if (optionalChallenge.isPresent()) {
            Challenges originalChallenge = optionalChallenge.get();
            // Update the original challenge with the updated values
            originalChallenge.setName(challenge.getName());
            originalChallenge.setDescription(challenge.getDescription());
            originalChallenge.setInputs(challenge.getInputs());
            originalChallenge.setExpectedOutputs(challenge.getExpectedOutputs());
            // Save the updated challenge
            challengesRepository.save(originalChallenge);
            // Redirect to the displayChallenges endpoint to show the updated list of challenges
            ModelAndView modelAndView = new ModelAndView("redirect:/public/displayChallenges");
            return modelAndView;
        }
        // If the challenge with the given ID doesn't exist, handle the error accordingly
        ModelAndView errorModelAndView = new ModelAndView("error.html");
        errorModelAndView.addObject("message", "Challenge not found");
        return errorModelAndView;
    }

    @GetMapping("/deleteChallenge")
    public ModelAndView deleteChallenge(Model model, @RequestParam int challenge_id) {
        Optional<Challenges> optionalChallenge = challengesRepository.findById(challenge_id);
        if (optionalChallenge.isPresent()) {
            Challenges challenge = optionalChallenge.get();
            challengesRepository.delete(challenge);
            ModelAndView modelAndView = new ModelAndView("redirect:/public/displayChallenges");
            return modelAndView;
        }
        // If the challenge with the given ID doesn't exist, handle the error accordingly
        ModelAndView errorModelAndView = new ModelAndView("error.html");
        errorModelAndView.addObject("message", "Challenge not found");
        return errorModelAndView;
    }

}