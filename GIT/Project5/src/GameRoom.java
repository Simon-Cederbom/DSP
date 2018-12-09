import java.util.*;

public class GameRoom {
	private List<Connection> players;
	private String name;
	
	public GameRoom(String roomName) {
		name = roomName;
		players = new ArrayList<Connection>();
	}
	
	public String GetName() {
		return name;
	}
	
	public void AddPlayer(Connection player) {
		players.add(player);
	}
	
	public void RemovePlayer(Connection player) {
		players.remove(player);
	}

}
