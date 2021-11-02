package io.runon.cryptocurrency.service;

import com.seomse.commons.config.Config;
import com.seomse.commons.exception.IORuntimeException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * 암호화폐 yml 공통
 * @author macle
 */
public class CryptocurrencyYml {

    public static Map<String, Object> getYmlMap(String key){
        try {

            String path =  Config.getConfig("cryptocurrency.yml.path", "config/cryptocurrency.yml");

            File file = new File(path);
            if(!file.isFile()){
                throw new IORuntimeException("yml file not found path: " + path);
            }

            Map<String, Object> propMap = new Yaml().load(new FileReader(path));
            if(!propMap.containsKey(key)){
                throw new IORuntimeException("yml key contains false key: " + key);
            }

            //noinspection unchecked
            return (Map<String, Object>) propMap.get(key);
        }catch(IOException e){
            throw new IORuntimeException(e);
        }
    }
}
