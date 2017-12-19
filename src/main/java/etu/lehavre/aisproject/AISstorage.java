/*
 * University of Le Havre 
 * Aboozar Rajabi
 */

package etu.lehavre.aisproject;

import com.mongodb.BasicDBObject;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dk.dma.ais.binary.SixbitException;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage18;
import dk.dma.ais.message.AisMessage19;
import dk.dma.ais.message.AisMessage4;
import dk.dma.ais.message.AisMessage5;
import dk.dma.ais.message.AisMessageException;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.ais.packet.AisPacket;
import dk.dma.ais.packet.AisPacketReader;
import dk.dma.ais.reader.AisReader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.bson.Document;


public class AISstorage {
    private final MongoCollection<Document> messageCollection;
	protected int count = 0;

    public AISstorage(final MongoDatabase aisDatabase, AisReader reader, Path path) throws IOException {
    	messageCollection = aisDatabase.getCollection("messages");
    	//AisPacketReader pReader = AisPacketReader.createFromFile(path, true);
    	//messageCollection.drop();
        readAndBuild(reader, path);
        
    }

    
    private void readAndBuild(AisReader reader, Path path)  {
    		System.out.println(reader.toString());
    		reader.registerPacketHandler(new Consumer<AisPacket>() {
    			@Override
    			public void accept(AisPacket packet) {
    				String timeDate;
    				// Alternative returning null if no valid AIS message
    				AisMessage aisMessage = packet.tryGetAisMessage();

					String[] temp = packet.getStringMessage().split(" ");
					String rawDate = temp[0];
					temp = rawDate.substring(1, rawDate.length()-1).split("/");
					timeDate = temp[0] + "T" + temp[1] + "Z"; 
						
						
					//System.out.println("timedate " + timeDate);	
					System.out.println();
    				System.out.println();
					System.out.println(" **** " + packet.getStringMessage() + " ** " + timeDate +  " ** " + aisMessage + " ******** ");
    				
    				
    				
    				Document message = new Document();
					  
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss'Z'");
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
					//sdf.setTimeZone(TimeZone.getTimeZone("CEST"));
					Date dateInDate = null; //date in Date type
					try {
						dateInDate = sdf.parse(timeDate);
						//System.out.println(dateInDate);
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
					// Handle packet
				    // Position message (message 1/2/3)
				    if (aisMessage instanceof AisPositionMessage) {
				        AisPositionMessage aisPos = (AisPositionMessage) aisMessage;
				        
				        if (aisPos.getPos().getGeoLocation() != null){                   	
				        	
				            message.append("_id", aisPos.getMsgId() + "-" + aisPos.getUserId() + "-" + timeDate)
				            .append("message_type", aisPos.getMsgId())
							.append("repeat_indicator", aisPos.getRepeat())
							.append("mmsi", aisPos.getUserId())
							.append("navigation_status", aisPos.getNavStatus())
							.append("rate_of_turn", aisPos.getRot())
							.append("speed_over_ground", aisPos.getSog())
							.append("position_accuracy", aisPos.getPosAcc())
							.append("latitude", aisPos.getPos().getLatitudeDouble())
							.append("longitude", aisPos.getPos().getLongitudeDouble())
							.append("course_over_ground", aisPos.getCog())
							.append("true_heading", aisPos.getTrueHeading())
							//.append("time_stamp", aisPos.getSourceTag().getTimestamp())
							.append("maneuver_indicator", aisPos.getSpecialManIndicator())
							.append("spare", aisPos.getSpare())
							.append("raim", aisPos.getRaim() )
							.append("utc_sec", aisPos.getUtcSec())
							.append("date", dateInDate);	
				          System.out.println();
				          System.out.println();
				        }
				    }
				    
				    
				    //Message Type 4
				    if (aisMessage instanceof AisMessage4) {
				    	AisMessage4 aisM4 = (AisMessage4) aisMessage;
				    	
				    	if (aisM4.getPos().getGeoLocation() != null){
				        	
				            message.append("_id", aisM4.getMsgId() + "-" + aisM4.getUserId() + "-" + timeDate)
				            .append("message_type", aisM4.getMsgId())
							.append("repeat_indicator", aisM4.getRepeat())
							.append("mmsi", aisM4.getUserId())
							.append("latitude", aisM4.getPos().getLatitudeDouble())
							.append("longitude", aisM4.getPos().getLongitudeDouble())							
							.append("spare", aisM4.getSpare())
							.append("raim_flag", aisM4.getRaim())
							.append("year", aisM4.getUtcYear())
							.append("month", aisM4.getUtcMonth())
							.append("day", aisM4.getUtcDay())
							.append("hour", aisM4.getUtcHour())
							.append("minute", aisM4.getUtcMinute())
							.append("second", aisM4.getUtcSecond())
				        	.append("mDate", aisM4.getDate())
				        	.append("sub_messages", aisM4.getSubMessage())
				        	.append("position_accuracy", aisM4.getPosAcc())
				        	.append("slot_timeout", aisM4.getSlotTimeout())
				        	.append("sync_state", aisM4.getSyncState())
				        	.append("date", dateInDate);
							//.append("fix_quality", aisM4.get)
							//.append("type_epfd", aisM4.get)
							//.append("sotdma_state", aisM4.gets);
				    		
				    	}   	
				    }
					
				    //Message Type 5
				    if (aisMessage instanceof AisMessage5) {
				        AisMessage5 aisM5 = (AisMessage5) aisMessage;
				        
				        message.append("_id", aisM5.getMsgId() + "-" + aisM5.getUserId() + "-" + timeDate)
				        .append("message_type", aisM5.getMsgId())
				        .append("repeat_indicator", aisM5.getRepeat())
						.append("mmsi", aisM5.getUserId())
						.append("spare", aisM5.getSpare())
						.append("ais_version", aisM5.getVersion())
						.append("imo_number", aisM5.getImo())
						.append("call_sign", aisM5.getCallsign())
						.append("vessel_name", aisM5.getName())
						.append("ship_type", aisM5.getShipType())
						.append("dimension_bow", aisM5.getDimBow())
						.append("dimension_stern", aisM5.getDimStern())
						.append("dimension_port", aisM5.getDimPort())
						.append("dimension_starboard", aisM5.getDimStarboard())
						.append("position_type", aisM5.getPosType())
						.append("eta_date", aisM5.getEtaDate())
						//.append("eta_month", aisM5.getEtaDate())
						//.append("eta_day", sav.getETADay())
						//.append("eta_hour", sav.getETAHour())
						//.append("eta_minute", sav.getETAMinute())
						.append("draught", aisM5.getDraught())
						.append("destination", aisM5.getDest())
						.append("dte", aisM5.getDte())
						.append("date", dateInDate);
				    
				    }
				    
				  //Message Type 18
				    if (aisMessage instanceof AisMessage18) {
				        AisMessage18 aisM18 = (AisMessage18) aisMessage;
				        
				        if (aisM18.getPos().getGeoLocation() != null){
				        
					        message.append("_id", aisM18.getMsgId() + "-" + aisM18.getUserId() + "-" + timeDate)
					        .append("message_type", aisM18.getMsgId())
				            .append("repeat_indicator", aisM18.getRepeat())
							.append("mmsi", aisM18.getUserId())
							.append("speed_over_ground", aisM18.getSog())
							.append("mode_flag", aisM18.getModeFlag())
							.append("comm_state", aisM18.getCommState())
							.append("comm_state_selector_flag", aisM18.getCommStateSelectorFlag())
							.append("position_accuracy", aisM18.getPosAcc())
							.append("latitude", aisM18.getPos().getLatitudeDouble())
							.append("longitude", aisM18.getPos().getLongitudeDouble())
							//.append("valid_latitude", aisM18.getValidPosition().getLatitudeAsString())
							//.append("valid_longitude", aisM18.getValidPosition().getLongitudeAsString())
							.append("course_over_ground", aisM18.getCog())
							.append("true_heading", aisM18.getTrueHeading())
							//.append("time_stamp", aisM18.get)
							.append("raim_flag", aisM18.getRaim())
							//.append("cs_unit", aisM18.)
							.append("display_flag", aisM18.getClassBDisplayFlag())
							.append("dsc_flag", aisM18.getClassBDscFlag())
							.append("band_flag", aisM18.getClassBBandFlag())
							.append("unit_flag", aisM18.getClassBUnitFlag())
							.append("msg22_flag", aisM18.getClassBMsg22Flag())
							//.append("radio_status", aisM18.get)
				            .append("spare", aisM18.getSpare())
				            .append("utc_sec", aisM18.getUtcSec())
				            .append("date", dateInDate);
				        }
				    }
				    
				    //Message Type 19
				    if (aisMessage instanceof AisMessage19) {
				        AisMessage19 aisM19 = (AisMessage19) aisMessage;
				        
				        if (aisM19.getPos().getGeoLocation() != null){
					        message.append("_id", aisM19.getMsgId() + "-" + aisM19.getUserId() + "-" + timeDate)
					        .append("message_type", aisM19.getMsgId())
				            .append("repeat_indicator", aisM19.getRepeat())
							.append("mmsi", aisM19.getUserId())
							.append("spare1", aisM19.getSpare1())
							.append("spare2", aisM19.getSpare2())
							.append("spare3", aisM19.getSpare3())
							.append("speed_over_ground", aisM19.getSog())
							.append("position_accuracy", aisM19.getPosAcc())
							.append("latitude", aisM19.getPos().getLatitudeDouble())
							.append("longitude", aisM19.getPos().getLongitudeDouble())
							//.append("valid_latitude", aisM19.getValidPosition().getLatitudeAsString())
							//.append("valid_longitude", aisM19.getValidPosition().getLongitudeAsString())
							.append("course_over_ground", aisM19.getCog())
							.append("true_heading", aisM19.getTrueHeading())
							.append("utc_sec", aisM19.getUtcSec())
							.append("vessel_name", aisM19.getName())
							.append("ship_type", aisM19.getShipType())
							.append("dimension_bow", aisM19.getDimBow())
							.append("dimension_stern", aisM19.getDimStern())
							.append("dimension_port", aisM19.getDimPort())
							.append("dimension_starboard", aisM19.getDimStarboard())
							.append("position_type", aisM19.getPosType())
							.append("raim", aisM19.getRaim())
							.append("raim_flag", aisM19.getRaimFlag())
							.append("dte", aisM19.getDte())
							.append("mode_flag", aisM19.getModeFlag())
							.append("date", dateInDate);
				        	
				        }
				    }
				  message.append("source", "aishub");
				  try {
					checkTime();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  insertMessage(message);
    					
    			}
    		});
    		reader.start();
    		try {
    			reader.join();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    }
    
    
    
	public void insertMessage(Document msg){
        try {
        	if (msg.containsKey("message_type"))
        		messageCollection.insertOne(msg);
        } catch (MongoWriteException e) {
        		//System.out.println("code " + e.getCode());
                System.out.println(e);
               // throw e;
        }
	}
	
	public void checkTime() throws InterruptedException {
		int hour = new Date().getHours();
		int min = new Date().getMinutes();
		//System.out.println(hour);
		if ((hour == 1) && min == 45) {
			System.out.println("Wating for backup to be finished!");
			TimeUnit.MINUTES.sleep(150);
		}
	}
    
}
