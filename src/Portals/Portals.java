package Portals;

import net.risingworld.api.Plugin;
import net.risingworld.api.database.Database;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.events.player.PlayerEnterAreaEvent;
import net.risingworld.api.objects.Player;
import net.risingworld.api.utils.Area;
import net.risingworld.api.utils.Vector3i;
import net.risingworld.api.utils.Vector3f;
import net.risingworld.api.utils.Utils.ChunkUtils;
import net.risingworld.api.worldelements.WorldArea;
import net.risingworld.api.worldelements.World3DText;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;



public class Portals extends Plugin implements Listener{
    
    @Override
    public void onEnable(){
        //Register event listener
        registerEventListener(this);
        
        Database sqlite = getSQLiteConnection(getPath() + "/assets/portals_" + getWorld().getName() + ".db");
        DatabaseStuff db = new DatabaseStuff();
        db.setDB(sqlite);
        db.initDB();
        ArrayList<DatabaseStuff.portal> portals = db.getPortals();
        PortalDataBank.setPortals(portals);
        CreateWorldObjects();
    }
    
    public static class PortalDataBank{
        private static ArrayList<DatabaseStuff.portal> portals;
        private static ArrayList<Area> portalareas;
        
        public static ArrayList<DatabaseStuff.portal> getPortals(){
            return portals;
        }
        
        public static void setPortals(ArrayList<DatabaseStuff.portal> P){
            portals = P;
        }
        
        public static ArrayList<Area> getPortalAreas(){
            return portalareas;
        }
        
        public static void setPortalAreas(ArrayList<Area> PA){
            portalareas = PA;
        }
    }
    
    public static class WorldAreasDataBank{
        private static ArrayList<WorldArea> worldareas;
        
        public static ArrayList<WorldArea> getWorldAreas(){
            return worldareas;
        }
        
        public static void setWorldAreas(ArrayList<WorldArea> WA){
            worldareas = WA;
        }
    }
    
    public static class World3DTextDataBank{
        private static ArrayList<World3DText> worldtext;
        
        public static ArrayList<World3DText> getWorld3DText(){
            return worldtext;
        }
        
        public static void setWorld3DText(ArrayList<World3DText> WT){
            worldtext = WT;
        }
    }
    
    public static class DestinationAreasand3DTexts{
        private static ArrayList<WorldArea> destareas;
        private static ArrayList<World3DText> desttext;
        
        public static ArrayList<World3DText> getDest3DText(){
            return desttext;
        }
        
        public static void setDest3DText(ArrayList<World3DText> DT){
            desttext = DT;
        }
        
        public static ArrayList<WorldArea> getDestAreas(){
            return destareas;
        }
        
        public static void setDestAreas(ArrayList<WorldArea> DA){
            destareas = DA;
        }
    }
    
    public void CreateWorldObjects(){
        ArrayList<Area> portalareas = new ArrayList<>();
        ArrayList<WorldArea> worldareas = new ArrayList<>();
        ArrayList<World3DText> worldtext = new ArrayList<>();
        ArrayList<WorldArea> destareas = new ArrayList<>();
        ArrayList<World3DText> desttext = new ArrayList<>();
        
        for (DatabaseStuff.portal p : PortalDataBank.getPortals()){
            Vector3i StartChunk = new Vector3i(p.StartChunkposX,p.StartChunkposY,p.StartChunkposZ);
            Vector3i StartBlock = new Vector3i(p.StartBlockposX,p.StartBlockposY,p.StartBlockposZ);
            Vector3i EndChunk = new Vector3i(p.EndChunkposX,p.EndChunkposY,p.EndChunkposZ);
            Vector3i EndBlock = new Vector3i(p.EndBlockposX,p.EndBlockposY,p.EndBlockposZ);
            
            Area a = new Area(StartChunk, StartBlock, EndChunk, EndBlock);
            getServer().addArea(a);
            portalareas.add(a);
            
            WorldArea wa = new WorldArea(a);
            wa.setAlwaysVisible(false);
            wa.setFrameVisible(true);
            wa.setColor(0, 0, 1, 0.2f);
            worldareas.add(wa);
            
            Vector3f destcoordsstart = new Vector3f(p.TargetposX, p.TargetposY, p.TargetposZ);
            Vector3i destchunkstart = new Vector3i();
            Vector3i destblockstart = new Vector3i();
            Vector3f destcoordsend = new Vector3f(p.TargetposX, p.TargetposY+2, p.TargetposZ);
            Vector3i destchunkend = new Vector3i();
            Vector3i destblockend = new Vector3i();
            ChunkUtils.getChunkAndBlockPosition(destcoordsstart, destchunkstart, destblockstart);
            ChunkUtils.getChunkAndBlockPosition(destcoordsend, destchunkend, destblockend);
            Area d = new Area(destchunkstart, destblockstart, destchunkend, destblockend);
            WorldArea dest = new WorldArea(d);
            dest.setAlwaysVisible(false);
            dest.setFrameVisible(true);
            dest.setColor(0, 1, 0, 0.2f);
            destareas.add(dest);
            
            World3DText wt = new World3DText("ID: " + p.idNo + " Name: " + p.Name);
            wt.setAlwaysVisible(true);
            wt.setFontColor(1, 0, 0, 1);
            wt.setPosition(p.GlobalEndposX, p.GlobalEndposY+1, p.GlobalEndposZ);
            worldtext.add(wt);
            
            World3DText dt = new World3DText("Destination of Portal ID: " + p.idNo);
            dt.setAlwaysVisible(true);
            dt.setFontColor(1, 0, 0, 1);
            dt.setPosition(p.TargetposX, p.TargetposY+3, p.TargetposZ);
            desttext.add(dt);
        }
        
        WorldAreasDataBank.setWorldAreas(worldareas);
        World3DTextDataBank.setWorld3DText(worldtext);
        DestinationAreasand3DTexts.setDestAreas(destareas);
        DestinationAreasand3DTexts.setDest3DText(desttext);
        PortalDataBank.setPortalAreas(portalareas);
    }
    
    @EventMethod
    public void onCommand(PlayerCommandEvent event){
        Player player = event.getPlayer();
        String command = event.getCommand();
        
        String[] cmd = command.split(" ");
        if (player.isAdmin()){
            if (cmd[0].equals("/portal")){
                if (cmd.length >= 2){
                    DatabaseStuff db = new DatabaseStuff();
                    switch (cmd[1]){
                        case "select":
                            if (cmd.length == 2){
                                player.enableAreaSelectionTool();
                                player.sendTextMessage("[#FF8800]Select the area you want and use /portal create name to create your new portal");
                            }
                            break;
                        case "create":
                            if (cmd.length == 3){
                                String Name = cmd[2];
                                int idNo;
                                if (PortalDataBank.getPortals().size() > 0){
                                    idNo = PortalDataBank.getPortals().get(PortalDataBank.getPortals().size()-1).idNo + 1;
                                }
                                else{
                                    idNo = 1;
                                }
                                player.getAreaSelectionData((Area portalarea) -> {
                                    if(portalarea == null){
                                        player.sendTextMessage("[#FF0000]No area is selected! Use /portal select to select an area first");
                                    }
                                    else{
                                        portalarea.rearrange();
                                        Vector3i StartChunkPos = portalarea.getStartChunkPosition();
                                        Vector3i StartBlockPos = portalarea.getStartBlockPosition();
                                        Vector3f GlobalStartPos = ChunkUtils.getGlobalPosition(StartChunkPos, StartBlockPos);
                                        
                                        Vector3i EndChunkPos = portalarea.getEndChunkPosition();
                                        Vector3i EndBlockPos = portalarea.getEndBlockPosition();
                                        Vector3f GlobalEndPos = ChunkUtils.getGlobalPosition(EndChunkPos, EndBlockPos);
                                        
                                        DatabaseStuff.portal P = db.new portal();
                                        P.idNo = idNo;
                                        P.Name = Name;
                                        P.StartChunkposX = StartChunkPos.getX();
                                        P.StartChunkposY = StartChunkPos.getY();
                                        P.StartChunkposZ = StartChunkPos.getZ();
                                        P.StartBlockposX = StartBlockPos.getX();
                                        P.StartBlockposY = StartBlockPos.getY();
                                        P.StartBlockposZ = StartBlockPos.getZ();
                                        P.GlobalStartposX = GlobalStartPos.getX();
                                        P.GlobalStartposY = GlobalStartPos.getY();
                                        P.GlobalStartposZ = GlobalStartPos.getZ();
                                        P.EndChunkposX = EndChunkPos.getX();
                                        P.EndChunkposY = EndChunkPos.getY();
                                        P.EndChunkposZ = EndChunkPos.getZ();
                                        P.EndBlockposX = EndBlockPos.getX();
                                        P.EndBlockposY = EndBlockPos.getY();
                                        P.EndBlockposZ = EndBlockPos.getZ();
                                        P.GlobalEndposX = GlobalEndPos.getX();
                                        P.GlobalEndposY = GlobalEndPos.getY();
                                        P.GlobalEndposZ = GlobalEndPos.getZ();
                                        P.TargetposX = 0.0f;
                                        P.TargetposY = 0.0f;
                                        P.TargetposZ = 0.0f;
                                        PortalDataBank.getPortals().add(P);
                                        db.createPortal(P);
                                        
                                        
                                        
                                        Vector3i StartChunk = new Vector3i(P.StartChunkposX,P.StartChunkposY,P.StartChunkposZ);
                                        Vector3i StartBlock = new Vector3i(P.StartBlockposX,P.StartBlockposY,P.StartBlockposZ);
                                        Vector3i EndChunk = new Vector3i(P.EndChunkposX,P.EndChunkposY,P.EndChunkposZ);
                                        Vector3i EndBlock = new Vector3i(P.EndBlockposX,P.EndBlockposY,P.EndBlockposZ);
                                        
                                        Area a = new Area(StartChunk, StartBlock, EndChunk, EndBlock);
                                        PortalDataBank.getPortalAreas().add(a);
                                        
                                        getServer().addArea(a);
                                        
                                        WorldArea WA = new WorldArea(a);
                                        WA.setAlwaysVisible(false);
                                        WA.setFrameVisible(true);
                                        WA.setColor(0, 0, 1, 0.2f);
                                        
                                        Vector3f destcoordsstart = new Vector3f(P.TargetposX, P.TargetposY, P.TargetposZ);
                                        Vector3i destchunkstart = new Vector3i();
                                        Vector3i destblockstart = new Vector3i();
                                        Vector3f destcoordsend = new Vector3f(P.TargetposX, P.TargetposY+2, P.TargetposZ);
                                        Vector3i destchunkend = new Vector3i();
                                        Vector3i destblockend = new Vector3i();
                                        ChunkUtils.getChunkAndBlockPosition(destcoordsstart, destchunkstart, destblockstart);
                                        ChunkUtils.getChunkAndBlockPosition(destcoordsend, destchunkend, destblockend);
                                        Area d = new Area(destchunkstart, destblockstart, destchunkend, destblockend);
                                        WorldArea DA = new WorldArea(d);
                                        DA.setAlwaysVisible(false);
                                        DA.setFrameVisible(true);
                                        DA.setColor(0, 1, 0, 0.2f);
                                        
                                        World3DText WT = new World3DText("ID: " + P.idNo + " Name: " + P.Name);
                                        WT.setAlwaysVisible(true);
                                        WT.setFontColor(1, 0, 0, 1);
                                        WT.setPosition(P.GlobalEndposX, P.GlobalEndposY+1, P.GlobalEndposZ);
                                        
                                        World3DText DT = new World3DText("Destination of Portal ID: " + P.idNo);
                                        DT.setAlwaysVisible(true);
                                        DT.setFontColor(1, 0, 0, 1);
                                        DT.setPosition(P.TargetposX, P.TargetposY+3, P.TargetposZ);
                                        
                                        World3DTextDataBank.getWorld3DText().add(WT);
                                        WorldAreasDataBank.getWorldAreas().add(WA);
                                        DestinationAreasand3DTexts.getDestAreas().add(DA);
                                        DestinationAreasand3DTexts.getDest3DText().add(DT);
                                        
                                        player.sendTextMessage("[#FF8800]Portal " + Integer.toString(idNo) + " named " + Name +  " was successfully created!");
                                        player.disableAreaSelectionTool();
                                    }
                                });
                            }
                            break;
                        case "settarget":
                            if (cmd.length == 3){
                                try{
                                    int idNo = Integer.parseInt(cmd[2]);
                                    Vector3f Targetpos = player.getPosition();
                                    boolean idcheck = false;
                                    int count = 0;
                                    for (DatabaseStuff.portal p : PortalDataBank.getPortals()){
                                        if (p.idNo == idNo){
                                            db.setTargetPosition(idNo, Targetpos);
                                            player.sendTextMessage("[#FF8800]Target position for portal " + Integer.toString(idNo) + " was successfully set!");
                                            idcheck = true;
                                            Vector3f destcoordsstart = new Vector3f(Targetpos);
                                            Vector3i destchunkstart = new Vector3i();
                                            Vector3i destblockstart = new Vector3i();
                                            Vector3f destcoordsend = new Vector3f(Targetpos.getX(), Targetpos.getY()+2, Targetpos.getZ());
                                            Vector3i destchunkend = new Vector3i();
                                            Vector3i destblockend = new Vector3i();
                                            ChunkUtils.getChunkAndBlockPosition(destcoordsstart, destchunkstart, destblockstart);
                                            ChunkUtils.getChunkAndBlockPosition(destcoordsend, destchunkend, destblockend);
                                            Area d = new Area(destchunkstart, destblockstart, destchunkend, destblockend);
                                            DestinationAreasand3DTexts.getDestAreas().get(count).setArea(d);
                                            DestinationAreasand3DTexts.getDest3DText().get(count).setPosition(Targetpos.getX(), Targetpos.getY()+3, Targetpos.getZ());
                                            break;
                                        }
                                        count += 1;
                                    }
                                    
                                    if (idcheck == false){
                                        player.sendTextMessage("[#FF0000]Please enter a valid portal number");
                                    }
                                }
                                catch (NumberFormatException e){
                                    player.sendTextMessage("[#FF0000]Please enter a valid integer");
                                }
                            }
                            break;
                        case "remove":
                            if (cmd.length == 3){
                                try{
                                    int idNo = Integer.parseInt(cmd[2]);
                                    db.removePortal(idNo);
                                    for (int i=0; i < PortalDataBank.getPortals().size(); i++){
                                        if (PortalDataBank.getPortals().get(i).idNo == idNo){
                                            
                                            for (WorldArea wa : WorldAreasDataBank.getWorldAreas()){
                                                player.removeWorldElement(wa);
                                            }
                                            for (World3DText wt : World3DTextDataBank.getWorld3DText()){
                                                player.removeWorldElement(wt);
                                            }
                                            for (WorldArea wa : DestinationAreasand3DTexts.getDestAreas()){
                                                player.removeWorldElement(wa);
                                            }
                                            for (World3DText wt : DestinationAreasand3DTexts.getDest3DText()){
                                                player.removeWorldElement(wt);
                                            }
                                            
                                            WorldAreasDataBank.getWorldAreas().get(i).destroy();
                                            World3DTextDataBank.getWorld3DText().get(i).destroy();
                                            DestinationAreasand3DTexts.getDestAreas().get(i).destroy();
                                            DestinationAreasand3DTexts.getDest3DText().get(i).destroy();
                                            getServer().removeArea(PortalDataBank.getPortalAreas().get(i));
                                            
                                            PortalDataBank.getPortals().remove(i);
                                            PortalDataBank.getPortalAreas().remove(i);
                                            WorldAreasDataBank.getWorldAreas().remove(i);
                                            World3DTextDataBank.getWorld3DText().remove(i);
                                            DestinationAreasand3DTexts.getDestAreas().remove(i);
                                            DestinationAreasand3DTexts.getDest3DText().remove(i);
                                            
                                            for (WorldArea wa : WorldAreasDataBank.getWorldAreas()){
                                                player.addWorldElement(wa);
                                            }
                                            for (World3DText wt : World3DTextDataBank.getWorld3DText()){
                                                player.addWorldElement(wt);
                                            }
                                            for (WorldArea wa : DestinationAreasand3DTexts.getDestAreas()){
                                                player.addWorldElement(wa);
                                            }
                                            for (World3DText wt : DestinationAreasand3DTexts.getDest3DText()){
                                                player.addWorldElement(wt);
                                            }
                                            player.sendTextMessage("[#FF8800]Portal " + Integer.toString(idNo) + " was successfully removed!");
                                            break;
                                        }
                                    }
                                }
                                catch (NumberFormatException | IndexOutOfBoundsException e){
                                    player.sendTextMessage("[#FF0000]Please enter a valid portal number");
                                }
                            }
                            break;
                        case "list":
                            if (cmd.length == 2 || cmd.length == 3){
                                int limit = 9;
                                try{
                                    ResultSet result1 = DatabaseStuff.data.executeQuery(("SELECT count(*) AS count FROM Portals"));
                                    int db_count = result1.getInt("count");
                                    int page_max = ((db_count + limit - 1) / limit);
                                    int page_now = 1;

                                    if (cmd.length == 3){
                                        try{
                                            page_now = Integer.parseInt(cmd[2]);
                                        }
                                        catch (NumberFormatException e){
                                            player.sendTextMessage("[#FF0000]Please enter a valid page number");
                                        }
                                    }
                                    
                                    if (page_now <= page_max){
                                        int start = ((page_now - 1) * limit);

                                        player.sendTextMessage("Available Portals:");
                                        player.sendTextMessage("[#00FF12]-------------------------------------------------------");

                                        ResultSet result2 = DatabaseStuff.data.executeQuery("SELECT * FROM Portals LIMIT " + Integer.toString(start) + ", " + Integer.toString(limit));
                                        while (result2.next()){
                                            player.sendTextMessage("[#FF8800]" + result2.getString("Name") + ", idNo=" + Integer.toString(result2.getInt("idNo")));
                                        }

                                        player.sendTextMessage("[#00FF12]-------------------------------------------------------");
                                        if (page_now == page_max){
                                            player.sendTextMessage("Page " + Integer.toString(page_now) + " / " + Integer.toString(page_max));
                                        }
                                        else{
                                            player.sendTextMessage("Page " + Integer.toString(page_now) + " / " + Integer.toString(page_max) + "       Next Page Command: /portal list " + Integer.toString((page_now + 1)));
                                        }
                                    }
                                }
                                catch (SQLException e){
                                }
                            }
                            break;
                        case "show":
                            if (cmd.length == 2){
                                for (WorldArea wa : WorldAreasDataBank.getWorldAreas()){
                                    player.addWorldElement(wa);
                                }
                                for (World3DText wt : World3DTextDataBank.getWorld3DText()){
                                    player.addWorldElement(wt);
                                }
                                for (WorldArea wa : DestinationAreasand3DTexts.getDestAreas()){
                                    player.addWorldElement(wa);
                                }
                                for (World3DText wt : DestinationAreasand3DTexts.getDest3DText()){
                                    player.addWorldElement(wt);
                                }
                            }
                            break;
                        case "hide":
                            if (cmd.length == 2){
                                for (WorldArea wa : WorldAreasDataBank.getWorldAreas()){
                                    player.removeWorldElement(wa);
                                }
                                for (World3DText wt : World3DTextDataBank.getWorld3DText()){
                                    player.removeWorldElement(wt);
                                }
                                for (WorldArea wa : DestinationAreasand3DTexts.getDestAreas()){
                                    player.removeWorldElement(wa);
                                }
                                for (World3DText wt : DestinationAreasand3DTexts.getDest3DText()){
                                    player.removeWorldElement(wt);
                                }
                            }
                            break;
                        case "cancel":
                            if (cmd.length == 2){
                                player.disableAreaSelectionTool();
                            }
                            break;
                        case "help":
                            if (cmd.length == 2){
                                player.sendTextMessage("[#FF8800]Available Portals Commands:");
                                player.sendTextMessage("[#FF8800]/portal select, enable area selection tool");
                                player.sendTextMessage("[#FF8800]/portal create name, creates portal from the selected area with the specified name");
                                player.sendTextMessage("[#FF8800]/portal settarget PortalID, sets the destination of the specified portal to the current position of the player");
                                player.sendTextMessage("[#FF8800]/portal remove PortalID, removes the specified portal completely");
                                player.sendTextMessage("[#FF8800]/portal list, displays a list of all already defined portals");
                                player.sendTextMessage("[#FF8800]/portal show, shows portals in the world");
                                player.sendTextMessage("[#FF8800]/portal hide, hides portals in the world");
                                player.sendTextMessage("[#FF8800]/portal cancel, disables area selection tool");
                            }
                            break;
                        default:
                            player.sendTextMessage("[#FF0000]Please enter a valid portals command! Use /portal help to see all valid inputs");
                            break;
                    }
                }
            }
        }
    }
    
    @EventMethod
    public void onAreaEnter(PlayerEnterAreaEvent event){
        Player player = event.getPlayer();
        Area area = event.getArea();
        int count = 0;
        
        for (Area a : PortalDataBank.getPortalAreas()){
            if (a.equals(area)){
                DatabaseStuff.portal p = PortalDataBank.getPortals().get(count);
                DatabaseStuff db =  new DatabaseStuff();
                Vector3f endpos = db.getTargetPosition(p.idNo);
                if (!endpos.equals(0.0f, 0.0f, 0.0f)){
                    player.setPosition(endpos);
                }
                break;
            }
            count += 1;
        }
    }
    
    @Override
    public void onDisable(){
    }
    
}
