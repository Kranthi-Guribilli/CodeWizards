# RGUForces
A college-level code testing environment
## Roles
The application will have different roles for different users. They are as follows:
* User with "creator" role can:
  - Create a new contest
  - Submit questions with inputs and expected output
* User with "Admin" role can:
  - See the leaderboard(can also decide whether contestants can view or not)
  - See all the submissions
* User with "Contestant" role can:
  - Register and participate in contests
  - Submit their code, and test for custom inputs
## Tech Stack:
1. Java Spring (Backend)
2. Docker containers (for testing the submittes code)
## User Workflow:
* Host on personal server or cloud
* Create a contest
  - Provide contest name and other details (Description, Guide, Rules, FAQ, Criteria etc)
  - Add questions (Challenge name, score, description, sample_input, sample_output, testcases)
  - Share link generated
* Participate in contest
  - Go to link generated for the contest
  - Register with required details and login
  - Go through questions and submit code
  - Test the code with sample inputs
  - Test with custom inputs (optional)
  - Submit for evaluation with all test cases
