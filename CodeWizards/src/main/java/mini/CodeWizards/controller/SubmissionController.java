package mini.CodeWizards.controller;


import mini.CodeWizards.model.Submission;
import mini.CodeWizards.model.TestCase;
import mini.CodeWizards.repository.SubmissionRepository;
import mini.CodeWizards.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @PostMapping
    public Submission submitCode(@RequestBody Submission submission) {
        //code evaluation logic
        String code = submission.getCode();
        List<TestCase> testCases = testCaseRepository.findByProblemId(submission.getProblem().getId());

        for (TestCase testCase : testCases) {
            // Execute the code using the input from the test case
            String input = testCase.getInput();
            // Execute the code and capture the output
            String output = executeCode(code, input);
            // Compare the output with the expected output from the test case
            String expectedOutput = testCase.getExpectedOutput();
            if (output.equals(expectedOutput)) {
                testCase.setPassed(true);
            } else {
                testCase.setPassed(false);
            }
        }

        // Calculate the overall result of the submission based on the test case results
        boolean allTestCasesPassed = testCases.stream().allMatch(TestCase::isPassed);
        if (allTestCasesPassed) {
            submission.setResult("Passed");
        } else {
            submission.setResult("Failed");
        }

        // Save the submission with the updated result
        return submissionRepository.save(submission);
    }
    private String executeCode(String code, String input) {
        String output = "";
        try {
            // Compile and execute the code using ProcessBuilder
            Process process = new ProcessBuilder("java", "-cp", "path/to/classpath", "MainClass")
                    .redirectInput(ProcessBuilder.Redirect.PIPE)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .start();

            // Write the input to the process
            process.getOutputStream().write(input.getBytes());
            process.getOutputStream().close();

            // Read the output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output += line + "\n";
            }

            // Wait for the process to finish
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return output;
    }
    // Additional submission-related endpoints as per your requirements
}

