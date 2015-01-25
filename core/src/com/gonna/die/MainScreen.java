package com.gonna.die;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gonna.die.components.*;
import com.gonna.die.systems.*;

class MainScreen extends ScreenAdapter {
    Engine engine;
    Viewport viewport;

    public MainScreen() {
        engine = new Engine();
        engine.addSystem(new RenderSystem());
        engine.addSystem(new BlueprintSystem());
        engine.addSystem(new TickerSystem());
        MissionSystem ms = new MissionSystem();
        TabSwitcherSystem tss = new TabSwitcherSystem(ms);
        engine.addSystem(tss);
        engine.addSystem(ms);
        engine.addSystem(new BarSystem(ms.ship));

        engine.addEntity(createBlueprintEntity());
        engine.addEntity(createHealthBarEntity(Room.LIFE_SUPPORT));
        engine.addEntity(createHealthBarEntity(Room.ENGINES));
        engine.addEntity(createHealthBarEntity(Room.REACTOR));
        engine.addEntity(createHealthBarEntity(Room.BRIDGE));

        engine.addEntity(createBackgroundEntity());
        engine.addEntity(createShipLayoutEntity());
        engine.addEntity(createTextReadoutEntity());
        engine.addEntity(createStatusReadoutEntity());
        engine.addEntity(createMissionProgressEntity());
        engine.addEntity(createMissionEntity());

        createTextReadoutTabs(engine);
        createTextReadoutText(engine, tss);

        viewport = new FitViewport(1280, 800);
    }

    private Entity createMissionEntity() {
        Entity entity = new Entity();

        MissionComponent mc = new MissionComponent();
        mc.lastTask = System.currentTimeMillis();
        mc.taskRate = 2000;
        entity.add(mc);

        return entity;
    }

    private Entity createBackgroundEntity() {
        Entity entity = new Entity();

        PositionComponent pc = new PositionComponent();
        pc.position.z = -100;

        entity.add(pc);

        return entity;
    }

    private Entity createShipLayoutEntity() {
        Entity entity = new Entity();

        TextureComponent tc = new TextureComponent();
        tc.region = new TextureRegion(new Texture("todo630x500.jpg"), 630, 500);

        PositionComponent pc = new PositionComponent();
        pc.position.x = 50;
        pc.position.y = 250;
        pc.position.z = -50;

        entity.add(tc);
        entity.add(pc);

        return entity;
    }

    private void createTextReadoutText(Engine engine, TabSwitcherSystem tss) {
        Entity title = new Entity();
        PositionComponent titlePosition = new PositionComponent();
        titlePosition.position.x = 1010;
        titlePosition.position.y = 720;
        title.add(titlePosition);
        TextureComponent titleText = new TextureComponent();
        //titleText.text = "Test text";

        FreeTypeFontGenerator ftfg = new FreeTypeFontGenerator(Gdx.files.local("fonts/Futura.ttc"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 24;
        titleText.font = ftfg.generateFont(param);

        title.add(titleText);
        engine.addEntity(title);

        Entity description = new Entity();
        PositionComponent descPosition = new PositionComponent();
        descPosition.position.x = 1020;
        descPosition.position.y = 680;
        description.add(descPosition);
        TextureComponent descText = new TextureComponent();
        //descText.text = "Test description\nMore information\nAnd some more";
        //descText.text = "Test description\nMore information\nAnd some more\nAnd more\nAnd more\ndadada";

        //ftfg = new FreeTypeFontGenerator(Gdx.files.local("fonts/Futura Koyu.ttf"));
        ftfg = new FreeTypeFontGenerator(Gdx.files.local("fonts/Pica.ttf"));
        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 14;
        descText.font = ftfg.generateFont(param);
        description.add(descText);

        tss.registerObserver(new TabSwitchedObserver() {
            @Override
            public void tabSwitched(Task task) {
                titleText.text = task.message;
                descText.text = "Hey Dan, please fix\nthis? :)\nNeeds a proper message\nSearch for POTATO\n";
            }
        });

        engine.addEntity(description);
    }

    private void createTextReadoutTabs(Engine engine) {
        for (int i = 0; i < 4; i++) {
            Entity entity = new Entity();

            TextureComponent tc = new TextureComponent();
            StateComponent sc = new StateComponent();
            IdComponent id = new IdComponent(i);
            PositionComponent pc = new PositionComponent();
            TaskComponent taskc = new TaskComponent();
            pc.position.x = 735;
            pc.position.y = 505 + (3 - i) * 60;

            entity.add(tc);
            entity.add(sc);
            entity.add(id);
            entity.add(pc);
            entity.add(taskc);

            engine.addEntity(entity);
        }
    }

    private Entity createTextReadoutEntity() {
        Entity entity = new Entity();

        TextureComponent tc = new TextureComponent();
        tc.region = new TextureRegion(new Texture("ui/tasks/taskScreen_bg.png"), 500, 250);
        //tc.text = "some test\nthat just keeps going on and on and on\nuntil it's done\n";
        //tc.width = 200; // FIXME This is wrong
        //tc.height = 200;

        long current = System.currentTimeMillis();

        TickerComponent tkc = new TickerComponent();
        /*tkc.tasks.add(new Task("Task 1", current + 3000));
        tkc.tasks.add(new Task("Task 2", current + 4000));
        tkc.tasks.add(new Task("Task 3", current + 5000));*/

        PositionComponent pc = new PositionComponent();
        pc.position.x = 730;
        pc.position.y = 500;
        pc.position.z = -50;

        entity.add(tkc);
        entity.add(tc);
        entity.add(pc);

        return entity;
    }

    private Entity createStatusReadoutEntity() {
        Entity entity = new Entity();

        TextureComponent tc = new TextureComponent();
        tc.region = new TextureRegion(new Texture("ui/status/statusScreen_bg.png"), 500, 400);

        PositionComponent pc = new PositionComponent();
        pc.position.x = 730;
        pc.position.y = 50;
        pc.position.z = -50;

        entity.add(tc);
        entity.add(pc);

        return entity;
    }

    private Entity createMissionProgressEntity() {
        Entity entity = new Entity();

        TextureComponent tc = new TextureComponent();
        tc.region = new TextureRegion(new Texture("todo630x150.jpg"), 630, 150);

        PositionComponent pc = new PositionComponent();
        pc.position.x = 50;
        pc.position.y = 50;
        pc.position.z = -50;

        entity.add(tc);
        entity.add(pc);

        return entity;
    }

    private Entity createBlueprintEntity() {
        Entity entity = new Entity();
        TextureComponent tc = new TextureComponent();
        tc.region = new TextureRegion(new Texture("annapurna_south_nepal.jpg"));


        StateComponent sc = new StateComponent();

        PositionComponent pc = new PositionComponent();
        pc.position.x = 0;
        pc.position.y = -775;
        pc.position.z = 100;

        entity.add(tc);
        entity.add(sc);
        entity.add(pc);
        entity.add(new BlueprintComponent());
        return entity;
    }

    private Entity createHealthBarEntity(int roomId) {
        Entity entity = new Entity();
        TextureComponent tc = new TextureComponent();
        tc.region = new TextureRegion(new Texture("healthBar.jpg"), 40, 160);

        PositionComponent pc = new PositionComponent();
        pc.position.x = 780 + (70 * roomId);        //40 wide, 160 tall
        pc.position.y = 255;
        pc.position.z = -150;

        entity.add(tc);
        entity.add(pc);
        entity.add(new BarComponent(roomId));

        return entity;
    }

    @Override
    public void render(float delta) {
        engine.update(delta);
        //update(delta);
        //draw();
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            toggleFullscreen();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void toggleFullscreen() {
        if (!Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width,
                    Gdx.graphics.getDesktopDisplayMode().height, true);
        } else {
            Gdx.graphics.setDisplayMode(1280, 800, false);
        }
    }
}
