package uni.fmi.st.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uni.fmi.st.models.Person;
import uni.fmi.st.models.User;
import uni.fmi.st.repos.PersonRepository;
import uni.fmi.st.repos.UserRepository;

@RestController
public class PersonManagerRest {

	private PersonRepository personRepo;
	private UserRepository userRepo;

	@Autowired
	public PersonManagerRest(PersonRepository personRepo, UserRepository userRepo) {
		this.personRepo = personRepo;
		this.userRepo = userRepo;
	}

	
	@Secured({"ROLE_USER"})
	@PostMapping("/removePerson")
	public ResponseEntity<String> removePerson(@RequestParam(name = "id") int id, HttpSession session) {
		final User user = (User) session.getAttribute("currentUser");
		if (null == user) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
													.body("");
		}
		final Person personForRemove = personRepo.findById(id).orElse(null);
		if (null != personForRemove) {
			if (!user.equals(personForRemove.getUser())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			} else {
				user.getFriends().remove(personForRemove);
				personRepo.delete(personForRemove);
				session.setAttribute("currentUser", userRepo.save(user));
			}
		}
		return ResponseEntity.ok().body("Person with id: " + id + " is removed");
	}

	@GetMapping("/getPersons")
	public ResponseEntity<List<Person>> getFriends(HttpSession session) {
		final List<Person> persons = new ArrayList<>();
		final User user = (User) session.getAttribute("currentUser");
		if (null == user) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(persons);
		} else {
			persons.addAll(personRepo.findByUser(user));
		}
		return ResponseEntity.ok(persons);
	}

	@PostMapping("/createPerson")
	public ResponseEntity<Person> createPerson(
			@RequestParam(name = "name") String name,
			@RequestParam(name = "email") String email, 
			@RequestParam(name = "birthDate") LocalDate birthDate,
			HttpSession session) {

		final User user = (User) session.getAttribute("currentUser");
		if (null == user) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		final Person person = new Person(name, email, birthDate);
		person.setUser(user);
		user.addFriend(person);
		session.setAttribute("currentUser", userRepo.save(user));

		return ResponseEntity.ok(person);
	}

}
