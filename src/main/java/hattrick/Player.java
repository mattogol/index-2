package hattrick;

public class Player {

	private String id;
	private Position position;
	private Attitude attitude;
	private Speciality speciality;
	private Boolean home;
	
	public Player(String id, Position position, Attitude attitude, Speciality speciality, Boolean home) {
		this.id = id;
		this.position = position;
		this.attitude = attitude;
		this.speciality = speciality;
		this.home = home;
	}

	public Player(String id, Position position, Speciality speciality, Boolean home) {
		this.id = id;
		this.position = position;
		this.speciality = speciality;
		this.home = home;
	}

	public String getId() {
		return id;
	}

	public Position getPosition() {
		return position;
	}

	public Attitude getAttitude() {
		return attitude;
	}

	public Speciality getSpeciality() {
		return speciality;
	}

	public boolean isHome() {
		return home;
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", position=" + position + ", attitude=" + attitude + ", speciality=" + speciality
				+ ", home=" + home + "]";
	}

}
