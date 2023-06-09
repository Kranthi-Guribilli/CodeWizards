package mini.CodeWizards.service;

import mini.CodeWizards.constants.CodeWizardsConstants;
import mini.CodeWizards.model.Contact;
import mini.CodeWizards.model.Person;
import mini.CodeWizards.model.Roles;
import mini.CodeWizards.repository.PersonRepository;
import mini.CodeWizards.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean createNewPerson(Person person){
        boolean isSaved = false;
        Roles role = rolesRepository.getByRoleName(CodeWizardsConstants.STUDENT_ROLE);
        person.setRoles(role);
        person.setPwd(passwordEncoder.encode(person.getPwd()));
        person.setCreatedAt(LocalDateTime.now());
        person.setCreatedBy(CodeWizardsConstants.ANONYMOUS);
        Person savedPerson = personRepository.save(person);
        if (null != savedPerson && person.getPersonId() > 0)
        {
            isSaved = true;
        }
        return isSaved;
    }
}