package uni.fmi.st.vaadin.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import uni.fmi.st.models.Person;
import uni.fmi.st.models.User;
import uni.fmi.st.repos.PersonRepository;
import uni.fmi.st.vaadin.UsersUI;


@SuppressWarnings("serial")
public class PersonForm extends FormLayout {
	private static final Logger LOGGER = Logger.getLogger(FormLayout.class.getName());

	private TextField name = new TextField("Nickname");
    private TextField email = new TextField("Email");
    private DateField birthdate = new DateField("Birthday");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private PersonRepository personRepo;
    private User currentUser;
    private Person person;
    private UsersUI usersUI;
    private Binder<Person> binder = new Binder<>(Person.class);

    public PersonForm(UsersUI usersUI) {
        this.usersUI = usersUI;
        this.personRepo = usersUI.getPersonRepo();
        this.currentUser = usersUI.getCurrentUser();
        
        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        addComponents(name, email, birthdate, buttons);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        binder.bindInstanceFields(this);

        save.addClickListener(e -> this.save());
        delete.addClickListener(e -> this.delete());
    }

    public void setPerson(Person person) {
        this.person = person;
        binder.setBean(person);

        // Show delete button for only customers already in the database
        delete.setVisible(person.isPersisted());
        setVisible(true);
        name.selectAll();
    }

    private void delete() {
		if(personRepo != null) {
	        personRepo.delete(person);
	        usersUI.updateList();
	        setVisible(false);
		}
    }

    private void save() {
		if(personRepo == null) {
	    	LOGGER.log(Level.SEVERE, "personRepo is null.");
		}
		if(currentUser == null) {
	    	LOGGER.log(Level.SEVERE, "currentUser is null.");
		}

		if(personRepo != null && currentUser != null) {
	        personRepo.save(person);
	        this.usersUI.updateList();
	        setVisible(false);
		}
    }

}
