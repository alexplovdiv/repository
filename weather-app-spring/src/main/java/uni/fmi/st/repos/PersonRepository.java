package uni.fmi.st.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uni.fmi.st.models.Person;
import uni.fmi.st.models.User;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
	public List<Person> findByUser(final User user);
	public List<Person> findByUserAndNameContaining(final User user, final String name);
}
