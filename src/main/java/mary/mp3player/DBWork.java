package mary.mp3player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mary.mp3player.Track;

public class DBWork {
	
 	static Connection conn;
 	final static String UNKNOWN = "unknown";
	
	public static void connect() {
		try {
			String url = "jdbc:sqlite:playlist.sqlite";
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(url);
			String createAlbumsTable = "CREATE TABLE IF NOT EXISTS `albums`  (\n" + 
					"	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" + 
					"	`title`	TEXT NOT NULL UNIQUE\n" + 
					");"; 			
			String createArtistsTable = "CREATE TABLE IF NOT EXISTS `artists` (\n" + 
					"	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" + 
					"	`name`	TEXT NOT NULL UNIQUE\n" + 
					");";
			String createTracksTable = "CREATE TABLE IF NOT EXISTS `tracks` (\n" + 
					"	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" + 
					"	`path`	TEXT NOT NULL UNIQUE,\n" + 
					"	`title`	TEXT,\n" + 
					"	`artist_fk`	INTEGER,\n" + 
					"	`album_fk`	INTEGER,\n" + 
					"	`duration`	INTEGER,\n" + 
					"	FOREIGN KEY(`album_fk`) REFERENCES `albums`(`id`),\n" + 
					"	FOREIGN KEY(`artist_fk`) REFERENCES `artists`(`id`)\n" + 
					");";
			
			Statement stmt = conn.createStatement();
			stmt.addBatch(createAlbumsTable);
			stmt.addBatch(createArtistsTable);
			stmt.addBatch(createTracksTable);
			
			stmt.executeBatch();
					
			System.out.println("Connected.");			
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static ObservableList<Track> getAllTracks() {
		
		ObservableList<Track> playlist = FXCollections.observableArrayList();	
		String select = "select tracks.id as id, tracks.path as path, tracks.title as title, artists.name as artist, albums.title as album " + 
						"from tracks left join artists on tracks.artist_fk = artists.id left join albums on tracks.album_fk = albums.id ";
		

		try{
			Statement stmt = conn.createStatement();
			ResultSet rs    = stmt.executeQuery(select);
			System.out.println("Selected");
			
			 while (rs.next()) {
				 playlist.add(new Track(rs.getInt("id"), rs.getString("path"), rs.getString("title"), rs.getString("artist"), rs.getString("album")));
				 System.out.println("added " + rs.getString("id")+ " " + rs.getString("path"));
			 }
			 
			// conn.close();

		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return playlist;
		
	}
	

	public static int addTrack(String path) {
		
		int artistId = insertArtist(path);
		int albumId = insertAlbum(path);
		int trackId = insertTrack(path, artistId, albumId);
		
		return trackId;
		
	}
	
	
	
	public static boolean removeTrack(int id) {
		
		//TODO deleting album and artist
		
		return deleteTrack(id);
	}
	

	//return id of artist
	private static int insertArtist(String path) { 
		PreparedStatement prStmt = null;
		String metaArtist;
		
		if (MetadataExtractor.getMetaArtist(path) == null || MetadataExtractor.getMetaArtist(path).equals(""))
			metaArtist = UNKNOWN;
		else
			metaArtist = MetadataExtractor.getMetaArtist(path);
		
		
		String insertArtist = "insert into artists (name) " +
				"select (?) " +
				"where not exists " +
				"(select id from artists where name = ?)";
		
		String selectArtistId = "select id from artists where name = ? ";
		
		try {
			prStmt = conn.prepareStatement(insertArtist);

			prStmt.setString(1, metaArtist);
			prStmt.setString(2, metaArtist);
			prStmt.execute();
			
			prStmt = conn.prepareStatement(selectArtistId);
			prStmt.setString(1, metaArtist);
			ResultSet rs = prStmt.executeQuery();
			System.out.println("added artist: " + metaArtist);			
			//conn.close();
			return rs.getInt("id");	
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	//return id of album
	private static int insertAlbum(String path) { 
		PreparedStatement prStmt = null;
		
		String metaAlbum;
		
		if ((MetadataExtractor.getMetaAlbum(path) == null ||MetadataExtractor.getMetaAlbum(path).equals("")) ) //sometimes getMetaAlbum returns null string, but sometimes - empty string
			metaAlbum = UNKNOWN;
		else
			metaAlbum = MetadataExtractor.getMetaAlbum(path);
		
		String insertAlbum = "insert into albums (title) " +
				"select (?) " +
				"where not exists " +
				"(select id from albums where title = ?)";
		
		String selectAlbumId = "select id from albums where title = ? ";
		
		
		try {
			prStmt = conn.prepareStatement(insertAlbum);
			prStmt.setString(1, metaAlbum);
			prStmt.setString(2, metaAlbum);
			prStmt.execute();
			
			prStmt = conn.prepareStatement(selectAlbumId);
			prStmt.setString(1, metaAlbum);
			ResultSet rs = prStmt.executeQuery();
			System.out.println("added album: " + metaAlbum);
			//conn.close();
			return rs.getInt("id");
			
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}		
	}
	
	private static int insertTrack(String path, int artistId, int albumId) {
		PreparedStatement prStmt = null;
		
		String metaTitle;
		
		if (MetadataExtractor.getMetaTitle(path) == null || MetadataExtractor.getMetaTitle(path).equals(""))
			metaTitle = UNKNOWN; 
		else
			metaTitle = MetadataExtractor.getMetaTitle(path);
		
		String insertTrack = "insert into tracks (path, title, artist_fk, album_fk) " +
							"select ?, ?, ?, ? " +
							"where not exists " +	
							"(select id from tracks where path = ?) ";
		
		String selectTrackId = "select id from tracks where path = ? ";
		
		
		try {
			prStmt = conn.prepareStatement(insertTrack);
			prStmt.setString(1, path);
			prStmt.setString(2, metaTitle);
			prStmt.setInt(3, artistId);
			prStmt.setInt(4, albumId);
			prStmt.setString(5, path);
			prStmt.execute();
			
			prStmt = conn.prepareStatement(selectTrackId);
			prStmt.setString(1, path);
			ResultSet rs = prStmt.executeQuery();
			System.out.println("added track: " + metaTitle);
			//conn.close();
			return rs.getInt("id");	
			
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}		
	}
	
	
	private static boolean deleteTrack(int id) {
		
		PreparedStatement prStmt = null;
		String deleteTrack = "delete from tracks where id = ?";
		try {
			prStmt  = conn.prepareStatement(deleteTrack);
			prStmt.setInt(1, id);
			prStmt.execute();
			//conn.close();
			return true;
			
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	

}	