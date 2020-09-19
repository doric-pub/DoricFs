/*
 * Copyright [2019] [Doric.Pub]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pub.doric.extension.fs;

import android.os.Build;
import android.os.Environment;

import com.github.pengfeizhou.jscore.JSONBuilder;
import com.github.pengfeizhou.jscore.JSObject;
import com.github.pengfeizhou.jscore.JSString;
import com.github.pengfeizhou.jscore.JSValue;
import com.github.pengfeizhou.jscore.JavaValue;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import pub.doric.DoricContext;
import pub.doric.extension.bridge.DoricMethod;
import pub.doric.extension.bridge.DoricPlugin;
import pub.doric.extension.bridge.DoricPromise;
import pub.doric.plugin.DoricJavaPlugin;

/**
 * @Description: pub.doric.extension.fs
 * @Author: pengfei.zhou
 * @CreateDate: 2020/9/19
 */
@DoricPlugin(name = "fs")
public class DoricFsPlugin extends DoricJavaPlugin {
    public DoricFsPlugin(DoricContext doricContext) {
        super(doricContext);
    }

    @DoricMethod
    public void getDocumentsDir(JSObject options, DoricPromise promise) {
        JSValue external = options.getProperty("external");
        if (external.isBoolean() && !external.asBoolean().value()) {
            promise.resolve(new JavaValue(getDoricContext().getContext().getFilesDir().getAbsolutePath()));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                File dir = getDoricContext().getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                if (dir != null) {
                    promise.resolve(new JavaValue(dir.getAbsolutePath()));
                } else {
                    promise.resolve(new JavaValue(getDoricContext().getContext().getFilesDir().getAbsolutePath()));
                }
            } else {
                promise.resolve(new JavaValue(getDoricContext().getContext().getFilesDir().getAbsolutePath()));
            }
        }
    }

    @DoricMethod
    public void exists(JSString path, DoricPromise promise) {
        File file = new File(path.value());
        promise.resolve(new JavaValue(file.exists()));
    }

    @DoricMethod
    public void stat(JSString path, DoricPromise promise) {
        File file = new File(path.value());
        if (!file.exists()) {
            promise.reject(new JavaValue(String.format("File %s does not exists", file.getAbsolutePath())));
            return;
        }

        JSONBuilder jsonBuilder = new JSONBuilder();
        jsonBuilder.put("name", file.getName());
        jsonBuilder.put("path", file.getPath());
        jsonBuilder.put("isFile", file.isFile());
        jsonBuilder.put("isDirectory", file.isDirectory());
        jsonBuilder.put("size", file.length());
        jsonBuilder.put("lastModified", file.lastModified());
        promise.resolve(new JavaValue(jsonBuilder.toJSONObject()));
    }

    @DoricMethod
    public void isFile(JSString path, DoricPromise promise) {
        File file = new File(path.value());
        if (!file.exists()) {
            promise.reject(new JavaValue(String.format("File %s does not exists", file.getAbsolutePath())));
            return;
        }
        promise.resolve(new JavaValue(file.isFile()));
    }

    @DoricMethod
    public void isDirectory(JSString path, DoricPromise promise) {
        File file = new File(path.value());
        if (!file.exists()) {
            promise.reject(new JavaValue(String.format("File %s does not exists", file.getAbsolutePath())));
            return;
        }
        promise.resolve(new JavaValue(file.isDirectory()));
    }

    @DoricMethod
    public void mkdir(JSString path, DoricPromise promise) {
        File file = new File(path.value());
        try {
            promise.resolve(new JavaValue(file.mkdir()));
        } catch (Exception e) {
            promise.reject(new JavaValue(e.getLocalizedMessage()));
        }
    }

    @DoricMethod
    public void readDir(JSString path, DoricPromise promise) {
        File dir = new File(path.value());
        if (!dir.exists()) {
            promise.reject(new JavaValue(String.format("File %s does not exists", dir.getAbsolutePath())));
            return;
        }
        if (!dir.isDirectory()) {
            promise.reject(new JavaValue(String.format("File %s is not directory", dir.getAbsolutePath())));
            return;
        }
        File[] files = dir.listFiles();
        JSONArray jsonArray = new JSONArray();
        for (File file : files) {
            jsonArray.put(file.getAbsolutePath());
        }
        promise.resolve(new JavaValue(jsonArray));
    }


    @DoricMethod
    public void readFile(JSString path, DoricPromise promise) {
        File file = new File(path.value());
        if (!file.exists()) {
            promise.reject(new JavaValue(String.format("File %s does not exists", file.getAbsolutePath())));
            return;
        }
        if (!file.isFile()) {
            promise.reject(new JavaValue(String.format("File %s is not file", file.getAbsolutePath())));
            return;
        }
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[fileInputStream.available()];
            fileInputStream.read(buffer);
            String result = new String(buffer);
            promise.resolve(new JavaValue(result));
        } catch (Exception e) {
            promise.reject(new JavaValue(e.getLocalizedMessage()));
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @DoricMethod
    public void writeFile(JSObject jsObject, DoricPromise promise) {
        FileWriter fileWriter = null;
        try {
            JSString path = jsObject.getProperty("path").asString();
            JSString content = jsObject.getProperty("content").asString();
            fileWriter = new FileWriter(path.value(), false);
            fileWriter.write(content.value());
            promise.resolve(new JavaValue());
        } catch (Exception e) {
            promise.reject(new JavaValue(e.getLocalizedMessage()));
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @DoricMethod
    public void appendFile(JSObject jsObject, DoricPromise promise) {
        FileWriter fileWriter = null;
        try {
            JSString path = jsObject.getProperty("path").asString();
            JSString content = jsObject.getProperty("content").asString();
            fileWriter = new FileWriter(path.value(), true);
            fileWriter.write(content.value());
            promise.resolve(new JavaValue());
        } catch (Exception e) {
            promise.reject(new JavaValue(e.getLocalizedMessage()));
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @DoricMethod
    public void delete(JSString path, DoricPromise promise) {
        File file = new File(path.value());
        try {
            promise.resolve(new JavaValue(file.delete()));
        } catch (Exception e) {
            promise.reject(new JavaValue(e.getLocalizedMessage()));
        }
    }

    @DoricMethod
    public void rename(JSObject jsObject, DoricPromise promise) {
        JSString src = jsObject.getProperty("src").asString();
        JSString dest = jsObject.getProperty("dest").asString();
        try {
            promise.resolve(new JavaValue(new File(src.value()).renameTo(new File(dest.value()))));
        } catch (Exception e) {
            promise.reject(new JavaValue(e.getLocalizedMessage()));
        }
    }
}
