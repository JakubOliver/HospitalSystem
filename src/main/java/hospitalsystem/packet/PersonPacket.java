package hospitalsystem.packet;

import hospitalsystem.personnel.Person;

public class PersonPacket extends GeneralPacket {
    public final Person person;

    public PersonPacket(Person person) {
        this.person = person;
    }
}
