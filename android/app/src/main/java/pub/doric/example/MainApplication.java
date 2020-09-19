package pub.doric.example;

import android.app.Application;

import pub.doric.Doric;
import pub.doric.extension.fs.DoricFsLibrary;

/**
 * @Description: pub.doric.example
 * @Author: pengfei.zhou
 * @CreateDate: 2019-12-05
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Doric.init(this);
        Doric.registerLibrary(new DoricFsLibrary());
    }
}
