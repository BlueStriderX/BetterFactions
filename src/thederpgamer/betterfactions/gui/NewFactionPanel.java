package thederpgamer.betterfactions.gui;

import api.common.GameClient;
import thederpgamer.betterfactions.data.federation.Federation;
import thederpgamer.betterfactions.gui.faction.management.FactionManagementTab;
import thederpgamer.betterfactions.gui.faction.news.FactionNewsTab;
import thederpgamer.betterfactions.gui.faction.diplomacy.FactionDiplomacyTab;
import thederpgamer.betterfactions.gui.federation.FederationManagementTab;
import org.schema.game.client.view.gui.faction.newfaction.FactionPanelNew;
import org.schema.schine.input.InputState;
import thederpgamer.betterfactions.utils.FactionUtils;
import thederpgamer.betterfactions.utils.FederationUtils;
import java.util.Objects;

/**
 * NewFactionPanel.java
 * Improved version of FactionPanelNew
 *
 * @since 01/30/2021
 * @author TheDerpGamer
 */
public class NewFactionPanel extends FactionPanelNew {

    private InputState inputState;

    public FactionNewsTab factionNewsTab;
    public FactionDiplomacyTab factionDiplomacyTab;
    public FactionManagementTab factionManagementTab;
    public FederationManagementTab federationManagementTab;

    public NewFactionPanel(InputState inputState) {
        super(inputState);
        this.inputState = inputState;
    }

    public Federation getFederation() {
        if(getOwnFaction() != null) {
            return FederationUtils.getFederation(Objects.requireNonNull(FactionUtils.getFactionData(getOwnFaction())));
        } else {
            return null;
        }
    }

    public boolean isInFederation() {
        return isInFaction() && FactionUtils.getFactionData(getOwnFaction()) != null && FactionUtils.getFactionData(getOwnFaction()).getFederationId() != -1;
    }

    public boolean isInFaction() {
        return FactionUtils.inFaction(GameClient.getClientPlayerState());
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void recreateTabs() {
        int selectedTab = factionPanel.getSelectedTab();
        factionPanel.activeInterface = this;
        factionPanel.clearTabs();

        (factionNewsTab = new FactionNewsTab(inputState, factionPanel, this)).onInit();
        factionPanel.getTabs().add(factionNewsTab);

        (factionDiplomacyTab = new FactionDiplomacyTab(inputState, factionPanel)).onInit();
        factionPanel.getTabs().add(factionDiplomacyTab);

        if(isInFaction()) {
            (factionManagementTab = new FactionManagementTab(inputState, factionPanel, this)).onInit();
            factionPanel.getTabs().add(factionManagementTab);

            if(isInFederation()) {
                (federationManagementTab = new FederationManagementTab(inputState, factionPanel, this)).onInit();
                factionPanel.getTabs().add(federationManagementTab);
            }
        }
        if(selectedTab < factionPanel.getTabs().size()) factionPanel.setSelectedTab(selectedTab);
    }
}
