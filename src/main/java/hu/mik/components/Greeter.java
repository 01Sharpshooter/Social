package hu.mik.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class Greeter {
	public String sayHello(){
		return "Greetings Traveler! I'm "+this.toString();
	}
}
