import io.runon.trading.data.file.TimeName;
import io.runon.trading.data.json.JsonTimeFilePathChange;

/**
 * 거래량 파일 새로운 표준경로로 이동
 * @author macle
 */
public class VolumePathChange {
    public static void main(String[] args) {

        JsonTimeFilePathChange pathChange = new JsonTimeFilePathChange();
        pathChange.setType(TimeName.Type.DAY_5);

        pathChange.outDirs("D:\\data\\cryptocurrency\\merge\\volume_backup","D:\\data\\cryptocurrency\\merge\\volume");

    }
}
