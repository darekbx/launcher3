package com.darekbx.launcher3.viewmodel

import androidx.lifecycle.ViewModel
import com.darekbx.launcher3.location.LocationProvider

class AntistormViewModel(
    private val locationProvider: LocationProvider
) : ViewModel() {

    /*
    https://antistorm.eu/ajaxPaths.php?lastTimestamp=0&type=radar
    timestamp:1614607201,1614606601,1614606001,1614605401,1614604801,1614604201,1614603602,1614603002,1614602402,1614601801<br>nazwa_folderu:2021.3.1,2021.3.1,2021.3.1,2021.3.1,2021.3.1,2021.3.1,2021.3.1,2021.3.1,2021.3.1,2021.3.1<br>nazwa_pliku:15-0,14-50,14-40,14-30,14-20,14-10,14-0,13-50,13-40,13-30<br>nazwa_pliku_front:20210301.1358,20210301.1348,20210301.1338,20210301.1328,20210301.1318,20210301.138,20210301.1258,20210301.1248,20210301.1238,20210301.1228<br>aktywnosc_burz:0,0,0,0,0,0,0,42,42,54<br>

    https://antistorm.eu/ajaxPaths.php?lastTimestamp=0&type=storm
    timestamp:1614607201,1614606601,1614606003,1614605401,1614604801,1614604202,1614603602,1614603001,1614602401,1614601801<br>nazwa_folderu:blank,blank,blank,blank,blank,blank,blank,blank,blank,blank<br>nazwa_pliku:blank,blank,blank,blank,blank,blank,blank,blank,blank,blank<br>nazwa_pliku_front:20210301.1358,20210301.1348,20210301.1338,20210301.1328,20210301.1318,20210301.138,20210301.1258,20210301.1248,20210301.1238,20210301.1228<br>aktywnosc_burz:0,0,0,0,0,0,0,0,0,0<br>

    https://antistorm.eu/map/final-map.png

    rain: https://antistorm.eu/visualPhenom/20210301.1318-radar-visualPhenomenon.png
    storm: https://antistorm.eu/visualPhenom/20210301.1318-storm-visualPhenomenon.png
    probabilities: https://antistorm.eu/archive/2021.3.1/15-0-radar-probabilitiesImg.png

    or:

    rain:
    download nowcasts: https://api.rainviewer.com/public/weather-maps.json, select newest and replace in path below
    512 - size
    4 - zoom
    35.71 - lat
    -70.87 - lng
    1 - colors or infrared
    1_1 - blur / ?
    https://tilecache.rainviewer.com/v2/radar/nowcast_3a2befc7d294/512/7/35.772912513244925/-5.802136108565397/2/1_1.png

     */

}
