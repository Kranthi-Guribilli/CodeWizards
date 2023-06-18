package mini.CodeWizards.controller;

import mini.CodeWizards.model.Problem;
import mini.CodeWizards.repository.WizardsClassRepository;
import mini.CodeWizards.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problems")
public class ProblemController {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private WizardsClassRepository wizardsClassRepository;

    @GetMapping
    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    @GetMapping("/{id}")
    public Problem getProblemById(@PathVariable Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
    }

    @PostMapping
    public Problem createProblem(@RequestBody Problem problem) {
        return problemRepository.save(problem);
    }

    @PutMapping("/{id}")
    public Problem updateProblem(@PathVariable Long id, @RequestBody Problem updatedProblem) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        problem.setTitle(updatedProblem.getTitle());
        problem.setDescription(updatedProblem.getDescription());
        return problemRepository.save(problem);
    }

    @DeleteMapping("/{id}")
    public void deleteProblem(@PathVariable Long id) {
        problemRepository.deleteById(id);
    }

   /* @GetMapping("/classes/{classId}")
    public List<Problem> getProblemsByClassId(@PathVariable Long classId) {
        Class userClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));
        // Retrieve problems specific to the class
        // We can modify the query or repository method to filter problems based on the class
    }*/
}


