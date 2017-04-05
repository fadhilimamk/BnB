package main.java;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by fadhil on 06/04/17.
 */
public class MapReader {
    Integer[][] map;

    public MapReader(String filename){
        String basePath = "/media/fadhil/Data/Proyek/BnB/src/main/resources/"
                + filename;
        //System
        // .getProperty("user.dir") +
                //"/src/main/resources/" + filename;
        Gson gson = new Gson();
        try {
            map = gson.fromJson(new FileReader(basePath), Integer[][].class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map.length; j++)
                if (map[i][j] == -1)
                    map[i][j] = Integer.MAX_VALUE;
    }

    public Integer[][] getMap(){
        return map;
    }
}
