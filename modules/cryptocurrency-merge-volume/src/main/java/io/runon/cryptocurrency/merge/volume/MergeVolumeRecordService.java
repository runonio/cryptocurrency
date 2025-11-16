package io.runon.cryptocurrency.merge.volume;

import io.runon.commons.service.Service;
import io.runon.commons.utils.ExceptionUtils;
import io.runon.cryptocurrency.trading.CryptocurrencyDataPath;
import io.runon.trading.data.TradingDataPath;
import io.runon.trading.data.file.LineOutManager;
import io.runon.trading.data.file.PathTimeLine;
import io.runon.trading.data.file.TimeLineLock;
import lombok.extern.slf4j.Slf4j;

/**
 * 기록을 디스크에 저장하는 서비스
 * @author macle
 */
@Slf4j
public class MergeVolumeRecordService extends Service {

    private final MergeVolumeService mergeVolumeService;

    public MergeVolumeRecordService(MergeVolumeService mergeVolumeService){
        setServiceId(this.getClass().getName());
        this.mergeVolumeService = mergeVolumeService;
        setDelayStartTime(10000L);
        setSleepTime(10000L);
        setState(State.START);

    }

    @Override
    public void work() {
        try{

            if(mergeVolumeService.isStop()){
                killService();
                return;
            }

            String [] lines;
            synchronized (mergeVolumeService.lock){
                if(mergeVolumeService.lineList.isEmpty()){
                    return ;
                }
                lines = mergeVolumeService.lineList.toArray(new String[0]);
                mergeVolumeService.lineList.clear();
            }
            LineOutManager lineOutManager = LineOutManager.getInstance();

            String relativePath = TradingDataPath.getRelativePath( CryptocurrencyDataPath.getMergeVolumeDirPath());
            String absolutePath = TradingDataPath.getAbsolutePath(relativePath);

            TimeLineLock timeLineLock = lineOutManager.get(absolutePath, PathTimeLine.JSON,  MergeVolumeService.TIME_NAME_TYPE);
            timeLineLock.add(lines);
//
//            String volumeDirPath = CryptocurrencyDataPath.getMergeVolumeDirPath();
//            String volumePath = volumeDirPath+ "/" + TimeName.getName(time, TIME_NAME_TYPE, ZONE_ID);
//            FileUtils.fileOutput(volumeJson + "\n", volumePath, true);

        }catch (Exception e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
