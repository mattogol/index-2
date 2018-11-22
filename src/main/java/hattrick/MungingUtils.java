package hattrick;

public class MungingUtils {
	
	public static Position getPosition(String positionStyleAttribute, String attitude) {
		
		if (positionStyleAttribute.equals("top:18px;left:253px;position:absolute;"))
			return Position.GK;
		if (positionStyleAttribute.equals("top:98px;left:20px;position:absolute;"))
			return Position.BW_R;
		if (positionStyleAttribute.equals(""))
			return Position.BW_L;
		if (positionStyleAttribute.equals(""))
			return Position.CD_C;
		if (positionStyleAttribute.equals(""))
			return Position.CD_L;
		if (positionStyleAttribute.equals(""))
			return Position.CD_R;
		if (positionStyleAttribute.equals(""))
			return Position.FW_C;
		if (positionStyleAttribute.equals(""))
			return Position.FW_L;
		if (positionStyleAttribute.equals(""))
			return Position.FW_R;
		if (positionStyleAttribute.equals(""))
			return Position.MID_C;
		if (positionStyleAttribute.equals(""))
			return Position.MID_L;
		if (positionStyleAttribute.equals(""))
			return Position.MID_R;
		if (positionStyleAttribute.equals(""))
			return Position.W_L;
		if (positionStyleAttribute.equals(""))
			return Position.W_R;
		else
			throw new RuntimeException("Position Absolute NON GESTITA: " + positionStyleAttribute + " - Attitude: " + attitude);
	}
}
