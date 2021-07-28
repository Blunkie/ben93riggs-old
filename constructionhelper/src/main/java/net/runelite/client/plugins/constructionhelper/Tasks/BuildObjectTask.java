package net.runelite.client.plugins.constructionhelper.Tasks;

import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.constructionhelper.ConstructionHelperConfig;
import net.runelite.client.plugins.constructionhelper.ConstructionHelperPlugin;
import net.runelite.client.plugins.constructionhelper.Task;

public class BuildObjectTask extends Task {
    public BuildObjectTask(ConstructionHelperPlugin plugin, Client client, ClientThread clientThread, ConstructionHelperConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 1;
    }

    @Override
    public boolean validate() {
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget == null) {
            return false;
        }

        if (inventoryWidget.getWidgetItems()
                .stream()
                .filter(item -> item.getId() == config.mode().getPlankId())
                .count() < config.mode().getPlankCost()) {
            return false;
        }

        QueryResults<GameObject> gameObjects = new GameObjectQuery()
                .idEquals(config.mode().getObjectSpaceId())
                .result(client);

        if (gameObjects == null || gameObjects.isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public void onGameTick(GameTick event) {
        QueryResults<GameObject> gameObjects = new GameObjectQuery()
                .idEquals(config.mode().getObjectSpaceId())
                .result(client);

        if (gameObjects == null || gameObjects.isEmpty()) {
            return;
        }

        GameObject larderSpaceObject = gameObjects.first();

        if (larderSpaceObject == null) {
            return;
        }

        clientThread.invoke(() ->
                client.invokeMenuAction(
                        "Build",
                        "",
                        config.mode().getObjectSpaceId(),
                        MenuAction.GAME_OBJECT_FIFTH_OPTION.getId(),
                        larderSpaceObject.getSceneMinLocation().getX(),
                        larderSpaceObject.getSceneMinLocation().getY()
                )
        );
    }
}
