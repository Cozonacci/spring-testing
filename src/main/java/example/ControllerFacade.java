package example;

import example.person.Person;
import example.person.PersonRepository;
import example.weather.WeatherClient;
import example.weather.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ControllerFacade {

    private final PersonRepository personRepository;
    private final WeatherClient weatherClient;

    @Autowired
    public ControllerFacade(final PersonRepository personRepository, final WeatherClient weatherClient) {
        this.personRepository = personRepository;
        this.weatherClient = weatherClient;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/hello/{lastName}")
    public String hello(@PathVariable final String lastName) {
        Optional<Person> foundPerson = personRepository.findByLastName(lastName);

        return foundPerson
                .map(person -> String.format("Hello %s %s!", person.getFirstName(), person.getLastName()))
                .orElse(String.format("Who is this '%s' you're talking about?", lastName));
    }

    @PostMapping(path = "/introduce", consumes = "application/json")
    public String introduce(@RequestBody final Person person) {
        Person personRecord;
        try {
            personRecord = personRepository.save(person);
        } catch (Exception ex) {
            return String.format("We're sorry %s, we failed to register you.", person.getFirstName());
        }
        return String.format("Nice to meet you, %s! Your registration number is %s.",
                personRecord.getFirstName(), personRecord.getId());
    }

    @GetMapping("/weather")
    public String weather() {
        return weatherClient.fetchWeather()
                .map(WeatherResponse::getSummary)
                .orElse("Sorry, I couldn't fetch the weather for you :(");
    }
}
