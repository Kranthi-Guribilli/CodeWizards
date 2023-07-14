package mini.CodeWizards.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mini.CodeWizards.model.*;
import mini.CodeWizards.repository.*;
import mini.CodeWizards.service.SubmissionService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import lombok.extern.slf4j.Slf4j;
import mini.CodeWizards.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("public")
public class PublicController {
    @Autowired
    SubmissionService submissionService;
    @Autowired
    CoursesRepository coursesRepository;

    @Autowired
    WizardsClassRepository wizardsClassRepository;
    @Autowired
    SubmissionRepository submissionRepository;
    @Autowired
    PersonService personService;

    @Autowired
    ChallengesRepository challengesRepository;
    @Autowired
    PersonRepository personRepository;

    @RequestMapping(value="/register", method={RequestMethod.GET})
    public String displayRegisterPage(Model model){
        model.addAttribute("person",new Person());
        return "register.html";
    }

    @RequestMapping(value="/createUser",method = {RequestMethod.POST})
    public String createUser(@Valid @ModelAttribute("person") Person person, Errors errors) {
        if (errors.hasErrors()) {
            return "register.html";
        }
        boolean isSaved = personService.createNewPerson(person);
        if (isSaved) {
            return "redirect:/login?register=true";
        } else {
            return "register.html";
        }
    }

    @RequestMapping("/displayClasses")
    public ModelAndView displayClasses(Model model) {
        List<WizardsClass> wizardsClasses= wizardsClassRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("classes.html");
        modelAndView.addObject("wizardsClasses",wizardsClasses);
        modelAndView.addObject("wizardsClass", new WizardsClass());
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
    @GetMapping("/displayCourses")
    public ModelAndView displayCourses(Model model) {
        List<Courses> courses = coursesRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("courses_secure.html");
        modelAndView.addObject("courses",courses);
        modelAndView.addObject("course", new Courses());
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

    @RequestMapping(value="/viewSubmissions",method= {RequestMethod.GET})
    public ModelAndView viewSubmissions(Model model, @RequestParam int challengeId, HttpSession session,
                                        @RequestParam(required=false) String error){
        Optional<Submission> submissions = submissionRepository.findById(challengeId);
        ModelAndView modelAndView = new ModelAndView("submissions.html");
        modelAndView.addObject("submissions",submissions);
        return modelAndView;
    }

    @GetMapping(value="/displayChallenges")
    public ModelAndView displayChallenges(Model model) {
        List<Challenges> challenges = challengesRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("challenges.html");
        modelAndView.addObject("challenges",challenges);
        modelAndView.addObject("challenge", new Challenges());
        return modelAndView;
    }
    @RequestMapping(value="/attemptChallenge",method= {RequestMethod.GET,RequestMethod.POST})
    public ModelAndView attemptChallenge(@RequestParam int challenge_id, Model model) {
        // Logic to retrieve the challenge with the given ID and pass it to the view
        Optional<Challenges> optionalChallenge = challengesRepository.findById(challenge_id);
        if(optionalChallenge.isPresent()){
            Challenges challenge = optionalChallenge.get();
            Submission submission = new Submission();
            ModelAndView modelAndView = new ModelAndView("attempt_challenge.html");
            modelAndView.addObject("challenge", challenge);
            modelAndView.addObject("submission", submission);
            return modelAndView;
        }
        // If the challenge with the given ID doesn't exist, handle the error accordingly
        ModelAndView errorModelAndView = new ModelAndView("error.html");
        errorModelAndView.addObject("message", "Challenge not found");
        return errorModelAndView;
    }
    @RequestMapping(value="/submit",method= {RequestMethod.POST})
    public ModelAndView submitCode(@RequestParam int challenge_id, @RequestParam("code") String code, Model model, Authentication authentication, HttpSession session) {
        code=code.substring(1);
        Optional<Challenges> optionalChallenge = challengesRepository.findById(challenge_id);
        if (optionalChallenge.isPresent()) {
            Challenges challenge = optionalChallenge.get();
            List<String> inputs = challenge.getInputs();
            List<String> expectedOutputs = challenge.getExpectedOutputs();
            List<String> testResults = executeCode(code, inputs, expectedOutputs);

            // Perform the execution of the code against the test cases
            Person person = personRepository.readByEmail(authentication.getName()); // Assuming you have a service to retrieve the logged-in person
            // Perform the execution of the code against the test cases
            // Create a new Submission entity
            Submission submission = new Submission();
            submission.setChallenge(challenge);
            submission.setPerson(person);
            submission.setCode(code);
            // Update the result status based on the test results
            boolean passed = testResults.stream().allMatch(result -> {
                String status = result.split(": ")[1]; // Extract the status (Passed or Failed)
                return status.equals("Passed");
            });
            submission.setResult(passed ? "Passed" : "Failed");

            // Save the submission
            submissionService.saveSubmission(submission);
            ModelAndView modelAndView = new ModelAndView("submit_result.html");
            modelAndView.addObject("submission", submission);

            // Add the test results to the model for displaying in the view
            modelAndView.addObject("testResults", testResults);

            // You can choose to redirect to a separate result page or return the same page with the test results displayed
            return modelAndView;
        } else {
            ModelAndView errorModelAndView = new ModelAndView("error.html");
            errorModelAndView.addObject("message", "Challenge not found");
            return errorModelAndView;
        }
    }

    private List<String> executeCode(String code, List<String> inputs, List<String> expectedOutputs){
        List<String> testResults = new ArrayList<>();
        // Execute the code against the test cases
        for (int i = 0; i < inputs.size(); i++) {
            String input = inputs.get(i);
            String expectedOutput = expectedOutputs.get(i);
            // Perform the code execution and compare the result with the expected output
            String actualOutput = executePythonCode(code, input);
            boolean passed = actualOutput.equals(expectedOutput);
            // Create a test result string
            String testResult = "Test Case " + (i + 1) + ": " + (passed ? "Passed" : "Failed");
            testResults.add(testResult);
            // Log the input, expected output, and actual output
            System.out.println("Code: "+code);
            System.out.println("Input: " + input);
            System.out.println("Expected Output: " + expectedOutput);
            System.out.println("Actual Output: " + actualOutput);
            System.out.println("Test Result: " + testResult);
            System.out.println("--------------------");
        }

        return testResults;
    }

    private String executePythonCode(String code, String input) {
        try {
            // Prepare the command to execute the Python code
            String[] command = {"python", "-c", code};

            // Start a new process for the Python interpreter
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // Get the input stream of the process
            InputStream inputStream = process.getInputStream();

            // Prepare the input to be passed to the Python code
            String fullInput = input + "\n";

            // Write the input to the process
            process.getOutputStream().write(fullInput.getBytes());
            process.getOutputStream().flush();
            process.getOutputStream().close();

            // Read the output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line);
                outputBuilder.append(System.lineSeparator());
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                // Execution was successful, return the output
                return outputBuilder.toString().trim();
            } else {
                // Execution failed, return the error message
                return "Error executing the Python code.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error executing the Python code.";
        }
    }

}
