package thederpgamer.betterfactions.utils;

import api.common.GameClient;
import api.common.GameCommon;
import api.mod.config.PersistentObjectUtil;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.Faction;
import org.schema.game.common.data.player.faction.FactionManager;
import thederpgamer.betterfactions.BetterFactions;
import thederpgamer.betterfactions.data.faction.FactionData;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * FactionUtils.java
 * <Description>
 * ==================================================
 * Created 01/30/2021
 * @author TheDerpGamer
 */
public class FactionUtils {

    private static final HashMap<Integer, FactionData> factionData = new HashMap<>();

    public static final String defaultLogo = "https://i.imgur.com/8wKjlBR.png";
    private static final String piratesLogo = ""; //Todo
    private static final String tradingGuildLogo = ""; //Todo

    private static final String defaultDescription = "A Faction";
    private static final String piratesDescription = "Small clans of ravaging space pirates that attack and loot everything in sight." +
            "\nDespite their savagery, they are currently quite weak due to their lack of an organized leadership.";
    private static final String tradingGuildDescription = "A large organization made up of wealthy trading guilds spread across the galaxy." +
            "\nThey are quite friendly and are always open for business. They also boast a large navy made up of the combined forces of their many guild members and will defend smaller factions from larger invaders.";

    public static boolean inFaction(PlayerState playerState) {
        return playerState.getFactionId() != 0;
    }

    public static Faction getFaction(PlayerState playerState) {
        if(playerState.getFactionId() == 0) {
            return null;
        } else {
            return GameCommon.getGameState().getFactionManager().getFaction(playerState.getFactionId());
        }
    }

    public static HashMap<Integer, FactionData> getAllFactions() {
        return factionData;
    }

    public static FactionData getPlayerFactionData() {
        if(GameClient.getClientPlayerState().getFactionId() != 0) {
            return getFactionData(getFaction(GameClient.getClientPlayerState()));
        } else {
            return null;
        }
    }

    public static FactionData getFactionData(int factionId) {
        return getFactionData(GameCommon.getGameState().getFactionManager().getFaction(factionId));
    }

    public static FactionData getFactionData(Faction faction) {
        if(factionData.containsKey(faction.getIdFaction())) {
            return factionData.get(faction.getIdFaction());
        } else {
            if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
                FactionData fData = new FactionData(faction);
                factionData.put(fData.getFactionId(), fData);
                saveData();
                return fData;
            }
            return null;
        }
    }

    public static void initializeFactions() {
        for(Faction faction : GameCommon.getGameState().getFactionManager().getFactionCollection()) {
            if(!FactionManager.isNPCFactionOrPirateOrTrader(faction.getIdFaction())) getFactionData(faction);
        }

        if(!factionData.containsKey(FactionManager.PIRATES_ID)) factionData.put(FactionManager.PIRATES_ID, initializeNPCFaction(FactionManager.PIRATES_ID));
        if(!factionData.containsKey(FactionManager.TRAIDING_GUILD_ID)) factionData.put(FactionManager.TRAIDING_GUILD_ID, initializeNPCFaction(FactionManager.TRAIDING_GUILD_ID));
    }

    public static void loadData() {
        if(GameCommon.isDedicatedServer() || GameCommon.isClientConnectedToServer()) {
            ArrayList<Object> objects = PersistentObjectUtil.getObjects(BetterFactions.getInstance().getSkeleton(), FactionData.class);
            for(Object object : objects) {
                FactionData fData = (FactionData) object;
                factionData.put(fData.getFactionId(), fData);
            }
            initializeFactions();
            saveData();
        }
    }

    public static void saveData() {
        if(GameCommon.isDedicatedServer() || GameCommon.isClientConnectedToServer()) {
            ArrayList<FactionData> toDelete = new ArrayList<>();
            for(FactionData fData : factionData.values()) {
                if(GameCommon.getGameState().getFactionManager().getFactionMap().containsKey(fData.getFactionId())) {
                    PersistentObjectUtil.addObject(BetterFactions.getInstance().getSkeleton(), fData);
                } else {
                    toDelete.add(fData);
                }
            }
            for(FactionData fData : toDelete) factionData.remove(fData.getFactionId());
            PersistentObjectUtil.save(BetterFactions.getInstance().getSkeleton());
        }
    }

    public static void updateFromServer(ArrayList<FactionData> factionDataList) {
        if(GameCommon.isClientConnectedToServer() || GameCommon.isOnSinglePlayer()) {
            for(FactionData fData : factionDataList) factionData.put(fData.getFactionId(), fData);
        }
    }

    private static FactionData initializeNPCFaction(int factionId) {
        Faction faction = GameCommon.getGameState().getFactionManager().getFaction(factionId);
        FactionData fData = new FactionData(faction);

        if(factionId == FactionManager.PIRATES_ID) {
            fData.setFactionLogo(piratesLogo);
            fData.setFactionDescription(piratesDescription);
        } else if(factionId == FactionManager.TRAIDING_GUILD_ID) {
            fData.setFactionLogo(tradingGuildLogo);
            fData.setFactionDescription(tradingGuildDescription);
        } else {
            fData.setFactionLogo(defaultLogo);
            fData.setFactionDescription(defaultDescription);
        }
        return fData;
    }
}
