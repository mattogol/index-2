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

	public void setPosition(Position p) {
		this.position = p;
	}

	public boolean hasOffensiveRole() {
		return this.position==Position.FW
				|| this.position==Position.MID
				|| this.position==Position.W_L
				|| this.position==Position.W_R
				;
	}

	public boolean hasDefensiveRole() {
		return this.position==Position.CD
				|| this.position==Position.BW_L
				|| this.position==Position.BW_R
				|| this.position==Position.MID
				;
	}

}
