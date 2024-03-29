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
package pub.doric.library.fs;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import pub.doric.Doric;
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
    private DoricPromise promise;

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
        String pathValue = path.value();
        if (pathValue.startsWith("assets")) {
            String assetPath = pathValue.substring("assets://".length());
            InputStream in = null;
            try {
                AssetManager assetManager = Doric.application().getAssets();
                in = assetManager.open(assetPath);
                promise.resolve(new JavaValue(true));
            } catch (IOException e) {
                e.printStackTrace();
                promise.resolve(new JavaValue(false));
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            File file = new File(pathValue);
            promise.resolve(new JavaValue(file.exists()));
        }
    }

    @DoricMethod
    public void stat(JSString path, DoricPromise promise) {
        String pathValue = path.value();
        if (pathValue.startsWith("assets")) {
            String assetPath = pathValue.substring("assets://".length());
            InputStream in = null;
            try {
                AssetManager assetManager = Doric.application().getAssets();
                in = assetManager.open(assetPath);
                JSONBuilder jsonBuilder = new JSONBuilder();
                jsonBuilder.put("size", in.available());
                promise.resolve(new JavaValue(jsonBuilder.toJSONObject()));
            } catch (IOException e) {
                e.printStackTrace();
                promise.reject(new JavaValue(String.format("File %s does not exists", pathValue)));
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
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
        String pathValue = path.value();
        if (pathValue.startsWith("assets")) {
            String assetPath = pathValue.substring("assets://".length());
            try {
                AssetManager assetManager = Doric.application().getAssets();
                String[] names = assetManager.list(assetPath);
                JSONArray jsonArray = new JSONArray();
                if (names != null) {
                    for (String name : names) {
                        jsonArray.put(pathValue + File.separator + name);
                    }
                }
                promise.resolve(new JavaValue(jsonArray));
            } catch (IOException e) {
                e.printStackTrace();
                promise.reject(new JavaValue(String.format("File %s does not exists", pathValue)));
            }
        } else {
            File dir = new File(pathValue);
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

    }


    @DoricMethod
    public void readFile(JSString path, DoricPromise promise) {
        String pathValue = path.value();

        InputStream inputStream = null;
        try {
            if (pathValue.startsWith("content://")) {
                Uri uri = Uri.parse(pathValue);
                ContentResolver resolver = Doric.application().getContentResolver();
                inputStream = resolver.openInputStream(uri);
            } else if (pathValue.startsWith("assets://")) {
                String assetPath = pathValue.substring("assets://".length());
                AssetManager assetManager = Doric.application().getAssets();
                inputStream = assetManager.open(assetPath);
            } else {
                File file = new File(pathValue);
                if (!file.exists()) {
                    promise.reject(new JavaValue(String.format("File %s does not exists", file.getAbsolutePath())));
                    return;
                }
                if (!file.isFile()) {
                    promise.reject(new JavaValue(String.format("File %s is not file", file.getAbsolutePath())));
                    return;
                }
                inputStream = new FileInputStream(file);
            }
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String result = new String(buffer);
            promise.resolve(new JavaValue(result));
        } catch (Exception e) {
            promise.reject(new JavaValue(e.getLocalizedMessage()));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @DoricMethod
    public void readBinaryFile(JSString path, DoricPromise promise) {
        String pathValue = path.value();

        InputStream inputStream = null;
        try {
            if (pathValue.startsWith("content://")) {
                Uri uri = Uri.parse(pathValue);
                ContentResolver resolver = Doric.application().getContentResolver();
                inputStream = resolver.openInputStream(uri);
            } else if (pathValue.startsWith("assets://")) {
                String assetPath = pathValue.substring("assets://".length());
                AssetManager assetManager = Doric.application().getAssets();
                inputStream = assetManager.open(assetPath);
            } else {
                File file = new File(pathValue);
                if (!file.exists()) {
                    promise.reject(new JavaValue(String.format("File %s does not exists", file.getAbsolutePath())));
                    return;
                }
                if (!file.isFile()) {
                    promise.reject(new JavaValue(String.format("File %s is not file", file.getAbsolutePath())));
                    return;
                }
                inputStream = new FileInputStream(file);
            }
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            promise.resolve(new JavaValue(buffer));
        } catch (Exception e) {
            promise.reject(new JavaValue(e.getLocalizedMessage()));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @DoricMethod
    public void writeFile(JSObject jsObject, DoricPromise promise) {
        JSString path = jsObject.getProperty("path").asString();
        JSValue content = jsObject.getProperty("content");
        if (content.isString()) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(path.value(), false);
                JSString contentString = content.asString();
                fileWriter.write(contentString.value());
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
        } else if (content.isArrayBuffer()) {
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(path.value());
                fileOutputStream.write(content.asArrayBuffer().value());
                promise.resolve(new JavaValue());
            } catch (Exception e) {
                promise.reject(new JavaValue(e.getLocalizedMessage()));
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            promise.reject(new JavaValue("Unsupported type"));
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

    @DoricMethod
    public void copy(JSObject jsObject, DoricPromise promise) {
        String src = jsObject.getProperty("src").asString().value();
        String dest = jsObject.getProperty("dest").asString().value();
        if (src.startsWith("assets")) {
            InputStream input = null;
            OutputStream output = null;
            try {
                String assetPath = src.substring("assets://".length());
                AssetManager assetManager = Doric.application().getAssets();
                input = assetManager.open(assetPath);
                output = new FileOutputStream(dest);
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }
                output.flush();
                promise.resolve(new JavaValue(true));
            } catch (Exception e) {
                promise.reject(new JavaValue(e.getLocalizedMessage()));
            } finally {
                if (input != null) {

                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            FileChannel inputChannel = null;
            FileChannel outputChannel = null;
            try {
                inputChannel = new FileInputStream(new File(src)).getChannel();
                outputChannel = new FileOutputStream(new File(dest)).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                promise.resolve(new JavaValue(true));
            } catch (Exception e) {
                promise.reject(new JavaValue(e.getLocalizedMessage()));
            } finally {
                if (inputChannel != null) {
                    try {
                        inputChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputChannel != null) {
                    try {
                        outputChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @DoricMethod
    public void choose(JSObject jsObject, DoricPromise promise) {
        JSValue value = jsObject.getProperty("mimeType");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (value.isString()) {
            intent.setType(value.toString());
        }
        getDoricContext().startActivityForResult(intent, 10001);
        this.promise = promise;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10001) {
            if (resultCode == Activity.RESULT_OK) {
                promise.resolve(new JavaValue(data.getDataString()));
            } else {
                promise.reject(new JavaValue("Cancelled"));
            }
        }
    }
}
