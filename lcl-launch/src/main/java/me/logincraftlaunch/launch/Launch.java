package me.logincraftlaunch.launch;

import lombok.SneakyThrows;
import me.logincraftlaunch.launch.data.LaunchData;

import java.util.logging.Logger;

public class Launch {
    private static final Logger logger = Logger.getGlobal();//日志记录器

    LaunchData launchData;

    public Launch(LaunchData launchData){
        this.launchData = launchData;
    }

    public void launch() throws Exception {
    }
}
