package mini.CodeWizards.service;

import mini.CodeWizards.constants.CodeWizardsConstants;
import mini.CodeWizards.model.Person;
import mini.CodeWizards.model.Roles;
import mini.CodeWizards.repository.PersonRepository;
import mini.CodeWizards.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RolesRepository rolesRepository;

    public boolean createNewPerson(Person person){
        boolean isSaved = false;
        Roles role = rolesRepository.getByRoleName(CodeWizardsConstants.STUDENT_ROLE);
        person.setRoles(role);
        person = personRepository.save(person);
        if (null != person && person.getPersonId() > 0)
        {
            isSaved = true;
        }
        return isSaved;
    }
}
