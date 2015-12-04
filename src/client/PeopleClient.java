package client;

import introsde.assignment.soap.*;
import introsde.assignment.soap.Person.CurrentHealth;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.ws.Holder;

public class PeopleClient {

	private int createdPersonId;

	public void printPerson(Person p) {
		System.out.println();
		System.out.println(" ==> ID:" + p.getIdPerson());
		System.out.println(" ==> Firstname: " + p.getFirstname());
		System.out.println(" ==> Lastname: " + p.getLastname());
		System.out.println(" ==> Birthdate: " + p.getBirthdate());
		System.out.println(" ==> email: " + p.getEmail());
		System.out.println(" ==> Username: " + p.getUsername());
		System.out.println(" ==> Current health profile: ");
		for (int i = 0; i < p.getCurrentHealth().getLifeStatus().size(); i++) {
			System.out.println("\t ==> mid: "
					+ p.getCurrentHealth().getLifeStatus().get(i)
							.getIdMeasure());
			System.out.println("\t ==> date: "
					+ p.getCurrentHealth().getLifeStatus().get(i)
							.getDateRegistered());
			System.out.println("\t ==> measure type:"
					+ p.getCurrentHealth().getLifeStatus().get(i)
							.getMeasureType().getMeasureType());
			System.out.println("\t ==> measure value: "
					+ p.getCurrentHealth().getLifeStatus().get(i)
							.getMeasureValue());
			System.out.println("\t ==> measure value type:"
					+ p.getCurrentHealth().getLifeStatus().get(i)
							.getMeasureType().getMeasureValueType());
		}
		System.out.println();
	}

	public void printHistory(HealthMeasureHistory his) {
		System.out.println(" ==> mid: " + his.getMid());
		System.out.println(" ==> date: " + his.getDateRegistered());
		System.out.println(" ==> measure type: "
				+ his.getMeasureDefinition().getMeasureType());
		System.out.println(" ==> measure value: " + his.getMeasureValue());
		System.out.println(" ==> measure value type: "
				+ his.getMeasureDefinition().getMeasureValueType());

		System.out.println();
	}

	public void readPersonList() {
		People_Service service = new People_Service();
		People people = service.getPeopleImplPort();
		List<Person> pList = people.readPersonList();

		System.out.println("  Retrived people:");
		for (int i = 0; i < pList.size(); i++) {
			// System.out.println(" ==> " + pList.get(i).getIdPerson());
			printPerson(pList.get(i));
		}

	}

	public void readPerson(int personId) {

		People_Service service = new People_Service();
		People people = service.getPeopleImplPort();
		Person p = people.readPerson(personId);
		System.out.println("Person with given ID: " + personId);
		printPerson(p);
	}

	public void updatePerson(int personId) {

		People_Service service = new People_Service();
		People people = service.getPeopleImplPort();
		Person p = people.readPerson(personId);

		System.out.println("  Before update");
		printPerson(p);

		p.setFirstname(p.getFirstname() + "Update");
		// p.setFirstname("Chuck");
		Holder<Person> hp = new Holder<Person>(p);
		people.updatePerson(hp);
		Person updatedPerson = people.readPerson(personId);

		System.out.println("  After update");
		printPerson(updatedPerson);
	}

	public int createPerson() {
		People_Service service = new People_Service();
		People people = service.getPeopleImplPort();

		System.out.println("Before creation");
		readPersonList();

		Person np = new Person();
		np.setFirstname("Charles");
		np.setLastname("Darwin");

		CurrentHealth ch = new CurrentHealth();
		LifeStatus ls = new LifeStatus();
		MeasureDefinition def = new MeasureDefinition();
		def.setIdMeasureDef(1);
		def.setMeasureType("weight");
		ls.setMeasureType(def);
		ls.setMeasureValue("90");
		ch.getLifeStatus().add(ls);

		np.setCurrentHealth(ch);
		Holder<Person> holder = new Holder<Person>(np);
		people.createPerson(holder);

		Person created = people.readPersonList().get(
				people.readPersonList().size() - 1);
		// createdPersonId = created.getIdPerson();

		System.out.println("After creation ");
		readPersonList();

		return created.getIdPerson();
	}

	public void deletePerson(int toDelete) {
		People_Service service = new People_Service();
		People people = service.getPeopleImplPort();
		System.out.println("Before delete ");
		readPersonList();

		Holder<Integer> hp = new Holder<Integer>(toDelete);
		people.deletePerson(hp);

		System.out.println("After delete ");
		readPersonList();
	}

	public void readPersonHistory(int personId, String measuryType) {
		People_Service service = new People_Service();
		People people = service.getPeopleImplPort();
		System.out.println("History");
		System.out.println("With person ID: " + personId + ", measure type: "
				+ measuryType);

		List<HealthMeasureHistory> his = people.readPersonHistory(personId,
				measuryType);
		for (int i = 0; i < his.size(); i++) {
			printHistory(his.get(i));
		}
	}

	public void readMeasureTypes() {
		People_Service service = new People_Service();
		People people = service.getPeopleImplPort();
		CustomMeasureDefinition def = people.readMeasureTypes();
		System.out.println("Measure Types");
		for (int i = 0; i < def.getMeasureType().size(); i++) {
			System.out.println(" ==> " + def.getMeasureType().get(i));
		}
	}

	public void readPersonMeasure(int personId, String measuryType, int mid) {

		People_Service service = new People_Service();
		People people = service.getPeopleImplPort();

		HealthMeasureHistory his = people.readPersonMeasure(personId,
				measuryType, mid);
		System.out.println("A persons's measure with mid: " + mid);
		printHistory(his);
	}

	public void savePersonMeasure(int personId) {
		People_Service service = new People_Service();
		People people = service.getPeopleImplPort();
		System.out.println("Creating new lifeStatus (measure) ");
		Person p = people.readPerson(personId);

		System.out.println("Before saving new life status");
		printPerson(p);

		LifeStatus ls = new LifeStatus();
		MeasureDefinition def = new MeasureDefinition();
		def.setIdMeasureDef(1);
		def.setMeasureType("weight");
		ls.setMeasureType(def);
		String oldValue = p.getCurrentHealth().getLifeStatus()
				.get(p.getCurrentHealth().getLifeStatus().size() - 1)
				.getMeasureValue();
		int intValue = Integer.parseInt(oldValue) + 1;
		String newValue = Integer.toString(intValue);
		ls.setMeasureValue(newValue);
		ls.setDateRegistered("2015-13-03");

		try {
			LifeStatus updated = people.savePersonMeasure(personId, ls);
		} catch (ParseException_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("After saving new life status");
		Person updatedPerson = people.readPerson(personId);
		printPerson(updatedPerson);

	}

	public void updatePersonMeasure(int personId, int mid) {
		String measureType = "weight";

		People_Service service = new People_Service();
		People people = service.getPeopleImplPort();
		System.out.println("Updating a history with person id: " + personId
				+ ", mid: " + mid);

		HealthMeasureHistory oldH = people.readPersonMeasure(personId,
				measureType, mid);
		printHistory(oldH);

		int newvalue = Integer.parseInt(oldH.getMeasureValue()) + 1;
		oldH.setMeasureValue(Integer.toString(newvalue));

		people.updatePersonMeasure(personId, oldH);

		System.out.println("New values");
		HealthMeasureHistory newH = people.readPersonMeasure(personId,
				measureType, mid);
		printHistory(newH);

	}

	public static void main(String[] args) throws Exception {

		PeopleClient c = new PeopleClient();

		System.out
				.println("Server URL: https://thawing-caverns-3996.herokuapp.com/ws/people?wsdl");

		System.out
				.println("-------------- M #1 readPersonList() --------------");
		c.readPersonList();

		System.out
				.println("-------------- M #2 readPerson(int personId) : readPerson(1)  --------------");
		c.readPerson(1);

		System.out
				.println("-------------- M #3 updatePerson(int personId): updatePerson(1)---------------");
		c.updatePerson(1);

		System.out
				.println("-------------- M #4 int createPerson() --------------");
		int createdPerson = c.createPerson();

		System.out
				.println("-------------- M #5 deletePerson(int toDelete): deletePerson(perviouslyCreatedPersonId) --------------");
		c.deletePerson(createdPerson);

		System.out
				.println("-------------- M #6 readPersonHistory(int personId, String measuryType) :readPersonHistory(1, weight)--------------");
		c.readPersonHistory(1, "weight");

		System.out
				.println("-------------- M #7 readMeasureTypes() --------------");
		c.readMeasureTypes();

		System.out.println("-------------- M #8 readPersonMeasure(int personId, String measuryType, int mid): readPersonMeasure(1, weight, 1)--------------");
		c.readPersonMeasure(1, "weight", 1);

		System.out.println("-------------- M #9 savePersonMeasure(int personId) :savePersonMeasure(1)--------------");
		c.savePersonMeasure(1);

		System.out.println("-------------- M #10 updatePersonMeasure(int personId, int mid) :updatePersonMeasure(1, 1)--------------");
		c.updatePersonMeasure(1, 1);

	}

}