package mini.CodeWizards.service;

import mini.CodeWizards.model.Submission;
import mini.CodeWizards.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    public void saveSubmission(Submission submission) {
        submissionRepository.save(submission);
    }
}
