package uni.fmi.st.vaadin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import uni.fmi.st.models.Person;
import uni.fmi.st.models.User;
import uni.fmi.st.repos.PersonRepository;
import uni.fmi.st.vaadin.view.PersonForm;


@Theme("apptheme")
@SpringUI(path = "/users")
@SpringComponent
@UIScope
public class UsersUI extends UI {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private PersonRepository personRepo;
	
    private Grid<Person> grid;
    private TextField filterText;
    private PersonForm form;
    private User currentUser;
    
	@Override
	protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        
        currentUser = (User)request.getWrappedSession().getAttribute("currentUser");

        grid = new Grid<>(Person.class);
        filterText = new TextField();
        form = new PersonForm(this);

        filterText.setPlaceholder("filter by name...");
        filterText.addValueChangeListener(e -> updateList());
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        Button clearFilterTextBtn = new Button(FontAwesome.TIMES);
        clearFilterTextBtn.setDescription("Clear the current filter");
        clearFilterTextBtn.addClickListener(e -> filterText.clear());

        CssLayout filtering = new CssLayout();
        filtering.addComponents(filterText, clearFilterTextBtn);
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        Button addCustomerBtn = new Button("Add new friend");
        addCustomerBtn.addClickListener(e -> {
            grid.asSingleSelect().clear();
            Person person= new Person();
            person.setUser(currentUser);
            form.setPerson(person);
        });

        HorizontalLayout toolbar = new HorizontalLayout(filtering, addCustomerBtn);

        grid.setColumns("name", "email", "birthDate");

        HorizontalLayout main = new HorizontalLayout(grid, form);
        main.setSizeFull();
        grid.setSizeFull();
        main.setExpandRatio(grid, 1);

        layout.addComponents(toolbar, main);

        // fetch list of Persons and assign it to Grid
        updateList();

        setContent(layout);

        form.setVisible(false);
    	
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                form.setVisible(false);
            } else {
                form.setPerson(event.getValue());
            }
        });
		
	}

	public void updateList() {
		if(currentUser != null) {
			Optional<String> str= filterText.getOptionalValue();
			List<Person> persons= str.isPresent() ?
				personRepo.findByUserAndNameContaining(currentUser, str.get()) :
				personRepo.findByUser(currentUser);
	        grid.setItems(persons);
		}
    }

	public PersonRepository getPersonRepo() {
		return personRepo;
	}
	
    public User getCurrentUser() {
		return currentUser;
	}

}

